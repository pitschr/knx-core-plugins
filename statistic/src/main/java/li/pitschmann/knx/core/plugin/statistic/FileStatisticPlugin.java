/*
 * Copyright (C) 2021 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin.statistic;

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;
import li.pitschmann.knx.core.body.ConnectRequestBody;
import li.pitschmann.knx.core.body.ConnectResponseBody;
import li.pitschmann.knx.core.body.ConnectionStateRequestBody;
import li.pitschmann.knx.core.body.ConnectionStateResponseBody;
import li.pitschmann.knx.core.body.DescriptionRequestBody;
import li.pitschmann.knx.core.body.DescriptionResponseBody;
import li.pitschmann.knx.core.body.DisconnectRequestBody;
import li.pitschmann.knx.core.body.DisconnectResponseBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.SearchRequestBody;
import li.pitschmann.knx.core.body.SearchResponseBody;
import li.pitschmann.knx.core.body.TunnelingAckBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.plugin.EnumConfigValue;
import li.pitschmann.knx.core.plugin.ExtensionPlugin;
import li.pitschmann.knx.core.plugin.LongConfigValue;
import li.pitschmann.knx.core.plugin.PathConfigValue;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
import li.pitschmann.knx.core.utils.Sleeper;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Statistic plug-in to write the statistic to a file
 * <p>
 * You can control the file path and how often the statistic
 * should be printed (interval).
 *
 * @author PITSCHR
 */
public final class FileStatisticPlugin implements ExtensionPlugin {
    /**
     * File path
     */
    public static final PathConfigValue PATH = new PathConfigValue("path", () -> Paths.get("knx-statistic.log"), null);
    /**
     * File format (e.g. JSON, TEXT)
     */
    public static final EnumConfigValue<FileStatisticFormat> FORMAT = new EnumConfigValue<>("format", FileStatisticFormat.class, () -> FileStatisticFormat.JSON);
    /**
     * Interval in milliseconds. Default 5 minutes (300000ms) Minimum 10 sec (10000ms).
     */
    public static final LongConfigValue INTERVAL_MS = new LongConfigValue("intervalMs", () -> 5 * 60 * 1000L, x -> x >= 10000);

    private static final Logger log = LoggerFactory.getLogger(FileStatisticPlugin.class);
    private static final String FILE_ROLLOVER_PATTERN = "-%d{yyyyMMdd}";

    private final ExecutorService executor = Executors.newSingleThreadExecutor(true);
    private KnxClient client;
    private Path path;
    private FileStatisticFormat format;
    private RotatingFileOutputStream fos;

    @Override
    public void onInitialization(final KnxClient client) {
        // configurations
        path = client.getConfig(FileStatisticPlugin.PATH);
        format = client.getConfig(FileStatisticPlugin.FORMAT);
        final var intervalMs = client.getConfig(FileStatisticPlugin.INTERVAL_MS);
        log.info("Initialized '{}' with: [path={}, format={}, intervalMs={}]", getClass().getName(), path, format, intervalMs);

        final var baseFile = path.toString();

        // get file pattern for rollover
        final var lastExtensionDotPosition = baseFile.lastIndexOf('.');
        final var rolloverFile = new StringBuilder()
                .append(baseFile, 0, lastExtensionDotPosition)
                .append(FILE_ROLLOVER_PATTERN)
                .append(baseFile.substring(lastExtensionDotPosition))
                .toString();

        final var config = RotationConfig
                .builder()
                .file(baseFile)
                .filePattern(rolloverFile)
                .policy(DailyRotationPolicy.getInstance())
                .append(true);

        // append header rotation callback if present
        final var header = format.getHeader();
        if (!Strings.isNullOrEmpty(header)) {
            config.callback(new HeaderRotationCallback(header));
        }

        // start rollover stream
        fos = new RotatingFileOutputStream(config.build());

        this.client = Objects.requireNonNull(client);
        executor.execute(new FileStatisticIntervalWriter(intervalMs));
        executor.shutdown();
    }

    @Override
    public void onStart() {
        // NO-OP
    }

    @Override
    public void onShutdown() {
        // close the executor
        Closeables.shutdownQuietly(executor);

        // print last statistic
        writeToStatisticFile();

        // close the rollover stream
        Closeables.closeQuietly(fos);
    }

    /**
     * Writes the statistic to file
     */
    private void writeToStatisticFile() {
        final var statistics = this.client.getStatistic();
        final var statisticsFormatted = String.format( //
                format.getTemplate(),
                Instant.now(),                                                         // %1
                // Total
                statistics.getNumberOfBodyReceived(),                                  // %2
                statistics.getNumberOfBytesReceived(),                                 // %3
                statistics.getNumberOfBodySent(),                                      // %4
                statistics.getNumberOfBytesSent(),                                     // %5
                statistics.getNumberOfErrors(),                                        // %6
                statistics.getErrorRate(),                                             // %7
                // Search
                statistics.getNumberOfBodyReceived(SearchRequestBody.class),           // %8
                statistics.getNumberOfBodyReceived(SearchResponseBody.class),          // %9
                statistics.getNumberOfBodySent(SearchRequestBody.class),               // %10
                statistics.getNumberOfBodySent(SearchResponseBody.class),              // %11
                // Description
                statistics.getNumberOfBodyReceived(DescriptionResponseBody.class),     // %12
                statistics.getNumberOfBodySent(DescriptionRequestBody.class),          // %13
                // Connect
                statistics.getNumberOfBodyReceived(ConnectResponseBody.class),         // %14
                statistics.getNumberOfBodySent(ConnectRequestBody.class),              // %15
                // Connection State
                statistics.getNumberOfBodyReceived(ConnectionStateResponseBody.class), // %16
                statistics.getNumberOfBodySent(ConnectionStateRequestBody.class),      // %17
                // Tunneling
                statistics.getNumberOfBodyReceived(TunnelingRequestBody.class),        // %18
                statistics.getNumberOfBodyReceived(TunnelingAckBody.class),            // %19
                statistics.getNumberOfBodySent(TunnelingRequestBody.class),            // %20
                statistics.getNumberOfBodySent(TunnelingAckBody.class),                // %21
                // Disconnect
                statistics.getNumberOfBodyReceived(DisconnectRequestBody.class),       // %22
                statistics.getNumberOfBodyReceived(DisconnectResponseBody.class),      // %23
                statistics.getNumberOfBodySent(DisconnectRequestBody.class),           // %24
                statistics.getNumberOfBodySent(DisconnectResponseBody.class),          // %25
                // Indication
                statistics.getNumberOfBodyReceived(RoutingIndicationBody.class),       // %26
                statistics.getNumberOfBodySent(RoutingIndicationBody.class)            // %27
        );
        // @formatter:on

        try {
            fos.write(statisticsFormatted.getBytes(StandardCharsets.UTF_8));
            fos.write(System.lineSeparator().getBytes());
        } catch (final IOException e) {
            log.error("Error writing to audit file '{}': {}", path, statisticsFormatted, e);
        }
    }

    /**
     * Runnable to print statistic in every interval
     *
     * @author PITSCHR
     */
    private class FileStatisticIntervalWriter implements Runnable {
        private final long intervalMs;

        private FileStatisticIntervalWriter(final long intervalMilliseconds) {
            this.intervalMs = intervalMilliseconds;
        }

        @Override
        public void run() {
            do {
                FileStatisticPlugin.this.writeToStatisticFile();
            } while (Sleeper.milliseconds(this.intervalMs));
        }
    }
}
