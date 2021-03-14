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

package li.pitschmann.knx.core.plugin.audit;

import com.vlkan.rfos.RotationCallback;
import com.vlkan.rfos.policy.RotationPolicy;
import li.pitschmann.knx.core.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Rotation Callback for files that requires header line (e.g. TSV files)
 * <p>
 * This callback is used to append a header when a new file is opened or rotated.
 *
 * @author PITSCHR
 */
final class HeaderRotationCallback implements RotationCallback {
    private static final Logger log = LoggerFactory.getLogger(HeaderRotationCallback.class);
    private byte[] headerLineAsBytes;

    /**
     * (package-protected) Creates {@link RotationCallback} for TSV files.
     *
     * @param headerLine the header for TSV file that should be applied for each rotation file
     */
    HeaderRotationCallback(final String headerLine) {
        this.headerLineAsBytes = Bytes.concat(
                headerLine.getBytes(StandardCharsets.UTF_8),
                System.lineSeparator().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Returns the current size of the outputStream
     *
     * @param outputStream the file output stream
     * @return {@code true} if the file is empty, {@code false} otherwise
     */
    private static boolean isFileEmpty(final OutputStream outputStream) {
        if (outputStream instanceof FileOutputStream) {
            final var fileOutputStream = (FileOutputStream) outputStream;
            try {
                return fileOutputStream.getChannel().size() == 0;
            } catch (final IOException ioe) {
                return false; // shall not happen!
            }
        }
        return false;
    }

    @Override
    public void onTrigger(RotationPolicy rotationPolicy, Instant instant) {
        // NO-OP
    }

    @Override
    public void onOpen(RotationPolicy rotationPolicy, Instant instant, OutputStream outputStream) {
        // print header only when file is empty
        if (isFileEmpty(outputStream)) {
            try {
                outputStream.write(headerLineAsBytes);
            } catch (final IOException ex) {
                log.error("Something went wrong writing the header. Permission issue?", ex);
            }
        }
    }

    @Override
    public void onClose(RotationPolicy rotationPolicy, Instant instant, OutputStream outputStream) {
        // NO-OP
    }

    @Override
    public void onSuccess(RotationPolicy rotationPolicy, Instant instant, File file) {
        // NO-OP
    }

    @Override
    public void onFailure(RotationPolicy rotationPolicy, Instant instant, File file, Exception e) {
        // NO-OP
    }
}
