package com.codesmells.bloaters.dataclumps.good;

import org.springframework.stereotype.Service;

/**
 * GOOD EXAMPLE: Data Clumps refactored by extracting parameter object.
 */
@Service
public class ReportServiceGood {

    /**
     * Use a parameter object to avoid repeating startDate/endDate/format.
     */
    public void generateSalesReport(ReportParameters params) {
        System.out.println("Generating sales report from " + params.getStartDate() + " to " + params.getEndDate() + " (" + params.getFormat() + ")");
    }

    public void generateInventoryReport(ReportParameters params) {
        System.out.println("Generating inventory report from " + params.getStartDate() + " to " + params.getEndDate() + " (" + params.getFormat() + ")");
    }

    public void emailReport(String recipient, ReportParameters params) {
        System.out.println("Emailing report to " + recipient + " for period " + params.getStartDate() + " - " + params.getEndDate());
    }
}
