package li.pitschmann.knx.core.plugin.statistic;

/**
 * Format for {@link FileStatisticPlugin}
 * <p>
 * JSON, TSV and TEXT are supported.
 */
public enum FileStatisticFormat {
    // @formatter:off
    /**
     * Statistic format should be in JSON format.
     */
    JSON(
            // Header
            "",
            // Body Template
            "" +
            "{" +
                "\"datetime\":\"%1$s\"," +
                "\"inbound\":{" +
                    "\"total\":{\"packets\":%2$s,\"bytes\":%3$s}," +
                    "\"search\":{\"request\":%8$s,\"response\":%9$s}," +
                    "\"description\":{\"request\":0,\"response\":%12$s}," +
                    "\"connect\":{\"request\":0,\"response\":%14$s}," +
                    "\"connectionState\":{\"request\":0,\"response\":%16$s}," +
                    "\"tunneling\":{\"request\":%18$s,\"acknowledge\":%19$s}," +
                    "\"indication\":{\"request\":0,\"response\":%26$s}," +
                    "\"disconnect\":{\"request\":%22$s,\"response\":%23$s}" +
                "}," +
                "\"outbound\":{" +
                    "\"total\":{\"packets\":%4$s,\"bytes\":%5$s}," +
                    "\"search\":{\"request\":%10$s,\"response\":%11$s}," +
                    "\"description\":{\"request\":%13$s,\"response\":0}," +
                    "\"connect\":{\"request\":%15$s,\"response\":0}," +
                    "\"connectionState\":{\"request\":%17$s,\"response\":0}," +
                    "\"tunneling\":{\"request\":%20$s,\"acknowledge\":%21$s}," +
                    "\"indication\":{\"request\":%27$s,\"response\":0}," +
                    "\"disconnect\":{\"request\":%24$s,\"response\":%25$s}" +
                "}," +
                "\"error\":{" +
                    "\"total\":{\"packets\":%6$s,\"rate\":%7$.2f}" +
                "}" +
            "}"
    ),
    /**
     * Statistic format should be in TSV format.
     */
    TSV(
            // Header
            "" +
            "Date & Time\t" +
            "Inbound Packets\tInbound Bytes\t" +
            "Outbound Packets\tOutbound Bytes\t" +
            "Error Packets\tError Rate (%)\t" +

            "Inbound Search Requests\tInbound Search Responses\t" +
            "Inbound Description Requests\tInbound Description Responses\t" +
            "Inbound Connect Requests\tInbound Connect Responses\t" +
            "Inbound Connection State Requests\tInbound Connection State Responses\t" +
            "Inbound Disconnect Requests\tInbound Disconnect Responses\t" +
            "Inbound Tunneling Requests\tInbound Tunneling Acknowledges\t" +
            "Inbound Indication Requests\tInbound Indication Responses\t" +

            "Outbound Search Requests\tOutbound Search Responses\t" +
            "Outbound Description Requests\tOutbound Description Responses\t" +
            "Outbound Connect Requests\tOutbound Connect Responses\t" +
            "Outbound Connection State Requests\tOutbound Connection State Responses\t" +
            "Outbound Disconnect Requests\tOutbound Disconnect Responses\t" +
            "Outbound Tunneling Requests\tOutbound Tunneling Acknowledges\t" +
            "Outbound Indication Requests\tOutbound Indication Responses",

            // Body Template
            "" +
            "%1$s\t" +             // date & time in ISO 8601 format
            "%2$s\t%3$s\t" +       // inbound total
            "%4$s\t%5$s\t" +       // outbound total
            "%6$s\t%7$.2f\t" +     // error total
            "%8$s\t%9$s\t" +       // inbound search
            "0\t%12$s\t" +         // inbound description
            "0\t%14$s\t" +         // inbound connect
            "0\t%16$s\t" +         // inbound connectionState
            "%22$s\t%23$s\t" +     // inbound disconnect
            "%18$s\t%19$s\t" +     // inbound tunneling
            "0\t%26$s\t" +         // inbound indication
            "%10$s\t%11$s\t" +     // outbound search
            "%13$s\t0\t" +         // outbound description
            "%15$s\t0\t" +         // outbound connect
            "%17$s\t0\t" +         // outbound connectionState
            "%24$s\t%25$s\t" +     // outbound disconnect
            "%20$s\t%21$s\t" +     // outbound tunneling
            "%27$s\t0"             // outbound indication
    ),
    /**
     * Statistic format should be in TEXT format
     */
    TEXT(
            // Header
            "",
            // Body Template
            "" +
            "Date & Time: %1$s%n" +                                               // line #1
            "%2$s packets received (%3$s bytes)%n" +                              // line #2
            "\t[Search          ] Request: %8$s, Response: %9$s%n" +              // line #3
            "\t[Description     ] Request: 0, Response: %12$s%n" +                // line #4
            "\t[Connect         ] Request: 0, Response: %14$s%n" +                // line #5
            "\t[Connection State] Request: 0, Response: %16$s%n" +                // line #6
            "\t[Tunneling       ] Request: %18$s, Acknowledge: %19$s%n" +         // line #7
            "\t[Indication      ] Request: 0, Response: %26$s%n" +                // line #8
            "\t[Disconnect      ] Request: %22$s, Response: %23$s%n" +            // line #9
            "%4$s packets sent (%5$s bytes)%n" +                                  // line #10
            "\t[Search          ] Request: %10$s, Response: %11$s%n" +            // line #11
            "\t[Description     ] Request: %13$s, Response: 0%n" +                // line #12
            "\t[Connect         ] Request: %15$s, Response: 0%n" +                // line #13
            "\t[Connection State] Request: %17$s, Response: 0%n" +                // line #14
            "\t[Tunneling       ] Request: %20$s, Acknowledge: %21$s%n" +         // line #15
            "\t[Indication      ] Request: %27$s, Response: 0%n" +                // line #16
            "\t[Disconnect      ] Request: %24$s, Response: %25$s%n" +            // line #17
            "%6$s errors (%7$.2f%%)%n" +                                          // line #18
            "-----------------------------------------------------------------"   // line #19
    );
    // @formatter:on

    private final String header;
    private final String template;

    FileStatisticFormat(final String header, final String template) {
        this.header = header;
        this.template = template;
    }

    public String getHeader() {
        return header;
    }

    public String getTemplate() {
        return template;
    }
}
