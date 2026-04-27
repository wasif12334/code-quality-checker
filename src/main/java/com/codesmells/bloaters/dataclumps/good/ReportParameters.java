package com.codesmells.bloaters.dataclumps.good;

/**
 * Parameter object to group report parameters and avoid data clumps.
 */
public class ReportParameters {

    private final String startDate;
    private final String endDate;
    private final String format;

    public ReportParameters(String startDate, String endDate, String format) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.format = format;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getFormat() {
        return format;
    }
}
