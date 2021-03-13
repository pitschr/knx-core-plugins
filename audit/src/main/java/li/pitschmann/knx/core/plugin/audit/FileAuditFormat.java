package li.pitschmann.knx.core.plugin.audit;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Json;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Format for {@link FileAuditPlugin}
 * <p>
 * JSON and TSV are supported.
 */
public enum FileAuditFormat {
    // @formatter:off
    /**
     * Audit format should be in JSON format.
     */
    JSON(
            // Header
            "",
            // Signal Template (JSON Format)
            "" +
            "{" +
                "\"datetime\":\"%1$s\"," +
                "\"type\":%2$s" +
            "}",
            // Body Template (JSON Format)
            "" +
            "{" +
                "\"datetime\":\"%1$s\"," +
                "\"type\":%2$s," +
                "\"header\":{" +
                    "\"totalLength\":%3$s," +
                    "\"raw\":%4$s" +
                "}," +
                "\"body\":{" +
                    "\"service\":{" +
                        "\"code\":%5$s," +
                        "\"text\":%6$s" +
                    "}," +
                    "\"raw\":%7$s" +
                "}" +
            "}",
            // Error Template (JSON Format)
            "" +
            "{" +
                "\"datetime\":\"%1$s\"," +
                "\"type\":%2$s," +
                "\"message\":%3$s," +
                "\"stacktrace\":%4$s" +
            "}",
            // Escaper for JSON message
            Json::toJson
    ),
    /**
     * Audit format should be in TSV text format
     */
    TSV(
            // Header
            "" +
            "Date & Time\t" +
            "Type\t" +
            "Header Total Length\tHeader Raw\t" +
            "Body Service Code\tBody Service Text\tBody Raw\t" +
            "Error Message\tError Stack Trace",
            // Signal Template (TSV Format)
            "" +
            "%1$s\t" +
            "%2$s",                            // type
            // Body Template (TSV Format)
            "" +
                    "%1$s\t" +                 // date & time
                    "%2$s\t" +                 // type
                    "%3$s\t" +                 // header.totalLength
                    "%4$s\t" +                 // header.raw
                    "%5$s\t" +                 // body.service.code
                    "%6$s\t" +                 // body.service.text
                    "%7$s",                    // body.raw
            // Error Template (TSV Format)
            "" +
                "%1$s\t" +                     // date & time
                "%2$s" +                       // type
                "\t\t\t\t\t\t" +               // reserved for (body)
                "%3$s\t" +                     // error message
                "%4$s",                        // stacktrace
            // Escaper for TSV message
                (obj) -> {
                    return ((obj instanceof Object[]) ? Arrays.toString((Object[])obj) : String.valueOf(obj))
                            // remove all \t
                            .replace("\t", "");
                }
    );
    // @formatter:on

    private final String header;
    private final String signalTemplate;
    private final String bodyTemplate;
    private final String errorTemplate;
    private final Function<Object, String> escaper;

    FileAuditFormat(final String header,
                    final String signalTemplate,
                    final String bodyTemplate,
                    final String errorTemplate,
                    final Function<Object, String> escaper) {
        this.header = header;
        this.signalTemplate = signalTemplate;
        this.bodyTemplate = bodyTemplate;
        this.errorTemplate = errorTemplate;
        this.escaper = escaper;
    }

    public String getHeader() {
        return header;
    }

    public String getSignalTemplate() {
        return signalTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public String getErrorTemplate() {
        return errorTemplate;
    }

    public String escape(final @Nullable Object str) {
        return str == null ? "" : escaper.apply(str);
    }
}
