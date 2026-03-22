package com.sba302.electroshop.service;

public interface ReportExportService {
    byte[] exportRevenueReport(java.time.LocalDate startDate, java.time.LocalDate endDate);
    byte[] exportInventoryReport();

    byte[] exportTopProductsReport(Integer limit);
}
