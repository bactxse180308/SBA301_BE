package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.TopProductResponse;
import com.sba302.electroshop.entity.BranchProductStock;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.repository.BranchProductStockRepository;
import com.sba302.electroshop.repository.BulkOrderRepository;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.service.AdminDashboardService;
import com.sba302.electroshop.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class ReportExportServiceImpl implements ReportExportService {

    private final AdminDashboardService adminDashboardService;
    private final BranchProductStockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final BulkOrderRepository bulkOrderRepository;

    @Override
    public byte[] exportRevenueReport(LocalDate startDate, LocalDate endDate) {
        log.info("Exporting detailed revenue report from {} to {}", startDate, endDate);
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.plusDays(1).atStartOfDay();

        // Calculate metrics
        int totalSingleOrders = orderRepository.countOrdersByDateRange(startOfDay, endOfDay) != null ? orderRepository.countOrdersByDateRange(startOfDay, endOfDay) : 0;
        long totalBulkOrders = bulkOrderRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        
        long cancelledBulkOrders = bulkOrderRepository.countByStatusAndCreatedAtBetween(BulkOrderStatus.CANCELLED, startOfDay, endOfDay);
        long cancelledSingleOrders = 0;
        var orderStats = orderRepository.countByOrderStatusAndDateRange(startOfDay, endOfDay);
        if (orderStats != null) {
            for (var stat : orderStats) {
                if ("CANCELLED".equals(stat.getName())) {
                    cancelledSingleOrders = stat.getValue();
                    break;
                }
            }
        }
        
        long totalCancelled = cancelledSingleOrders + cancelledBulkOrders;
        long totalOrdersAll = totalSingleOrders + totalBulkOrders;
        
        String cancelRate = "0.0%";
        if (totalOrdersAll > 0) {
            cancelRate = String.format("%.1f%%", (double) totalCancelled * 100 / totalOrdersAll);
        }

        BigDecimal singleRevenue = orderRepository.sumRevenueByDateRange(startOfDay, endOfDay);
        if (singleRevenue == null) singleRevenue = BigDecimal.ZERO;

        BigDecimal bulkRevenue = bulkOrderRepository.sumFinalPriceByStatusInAndCreatedAtBetween(
                Arrays.asList(BulkOrderStatus.COMPLETED, BulkOrderStatus.PROCESSING, BulkOrderStatus.CONFIRMED, BulkOrderStatus.SHIPPED, BulkOrderStatus.PAID), 
                startOfDay, endOfDay);
        if (bulkRevenue == null) bulkRevenue = BigDecimal.ZERO;

        BigDecimal totalRevenue = singleRevenue.add(bulkRevenue);

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // 1. Defining Styles
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte) 31, (byte) 114, (byte) 68}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle zebraStyle = workbook.createCellStyle();
            zebraStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte) 242, (byte) 249, (byte) 245}, (org.apache.poi.xssf.usermodel.DefaultIndexedColorMap) null));
            zebraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle currencyStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            currencyStyle.setDataFormat(dataFormat.getFormat("#,##0"));
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle zebraCurrencyStyle = workbook.createCellStyle();
            zebraCurrencyStyle.cloneStyleFrom(zebraStyle);
            zebraCurrencyStyle.setDataFormat(dataFormat.getFormat("#,##0"));
            zebraCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle footerStyle = workbook.createCellStyle();
            footerStyle.cloneStyleFrom(currencyStyle);
            Font footerFont = workbook.createFont();
            footerFont.setBold(true);
            footerStyle.setFont(footerFont);
            footerStyle.setBorderTop(BorderStyle.MEDIUM);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle footerTextStyle = workbook.createCellStyle();
            footerTextStyle.setFont(footerFont);
            footerTextStyle.setBorderTop(BorderStyle.MEDIUM);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle cardHeaderStyle = workbook.createCellStyle();
            cardHeaderStyle.cloneStyleFrom(headerStyle);
            cardHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle cardValueStyle = workbook.createCellStyle();
            Font cardValueFont = workbook.createFont();
            cardValueFont.setBold(true);
            cardValueFont.setFontHeightInPoints((short) 14);
            cardValueStyle.setFont(cardValueFont);
            cardValueStyle.setAlignment(HorizontalAlignment.CENTER);
            cardValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cardValueStyle.setBorderBottom(BorderStyle.THIN);
            cardValueStyle.setBorderTop(BorderStyle.THIN);
            cardValueStyle.setBorderLeft(BorderStyle.THIN);
            cardValueStyle.setBorderRight(BorderStyle.THIN);
            
            // --- SHEET 1: TỔNG QUAN ---
            Sheet summarySheet = workbook.createSheet("Tổng quan");
            // Metric Cards
            Row cardTitleRow = summarySheet.createRow(1);
            Row cardValueRow = summarySheet.createRow(2);
            cardValueRow.setHeightInPoints(30);
            
            String[] cardTitles = {"Tổng doanh thu", "Tổng đơn hàng", "Tổng đơn hủy", "Tỷ lệ hủy"};
            String[] cardValues = {formatCurrency(totalRevenue), String.valueOf(totalOrdersAll), String.valueOf(totalCancelled), cancelRate};
            
            for (int i = 0; i < 4; i++) {
                int colIdx = i * 2 + 1; // leave first column empty
                summarySheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, colIdx, colIdx + 1));
                summarySheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, colIdx, colIdx + 1));
                
                Cell titleCell = cardTitleRow.createCell(colIdx);
                titleCell.setCellValue(cardTitles[i]);
                titleCell.setCellStyle(cardHeaderStyle);
                cardTitleRow.createCell(colIdx+1).setCellStyle(cardHeaderStyle);
                
                Cell valCell = cardValueRow.createCell(colIdx);
                valCell.setCellValue(cardValues[i]);
                valCell.setCellStyle(cardValueStyle);
                cardValueRow.createCell(colIdx+1).setCellStyle(cardValueStyle);
            }
            
            // Detailed Summary Table
            int rIdx = 5;
            Row sHeader = summarySheet.createRow(rIdx++);
            createCell(sHeader, 1, "Chỉ số", headerStyle);
            createCell(sHeader, 2, "Đơn lẻ", headerStyle);
            createCell(sHeader, 3, "Đơn Bulk", headerStyle);
            
            addRowData(summarySheet, rIdx++, new Object[]{"Số lượng đơn", totalSingleOrders, totalBulkOrders}, zebraStyle, null, zebraStyle);
            addRowData(summarySheet, rIdx++, new Object[]{"Đơn hủy", cancelledSingleOrders, cancelledBulkOrders}, null, null, null);
            addRowData(summarySheet, rIdx++, new Object[]{"Doanh thu", singleRevenue.doubleValue(), bulkRevenue.doubleValue()}, zebraStyle, zebraCurrencyStyle, zebraCurrencyStyle);
            
            // Footer
            Row sFooter = summarySheet.createRow(rIdx++);
            createCell(sFooter, 1, "TỔNG CỘNG", footerTextStyle);
            Cell tf1 = sFooter.createCell(2); tf1.setCellValue(totalOrdersAll); tf1.setCellStyle(footerTextStyle);
            Cell tf2 = sFooter.createCell(3); tf2.setCellValue(totalRevenue.doubleValue()); tf2.setCellStyle(footerStyle);

            for (int i=0; i<=9; i++) summarySheet.autoSizeColumn(i);

            // --- SHEET 2: CHI TIẾT THÁNG ---
            Sheet monthSheet = workbook.createSheet("Chi tiết tháng");
            Row mHeader = monthSheet.createRow(0);
            createCell(mHeader, 0, "Tháng", headerStyle);
            createCell(mHeader, 1, "Số đơn", headerStyle);
            createCell(mHeader, 2, "Doanh thu", headerStyle);
            
            int mRow = 1;
            LocalDate current = startDate.withDayOfMonth(1);
            long totalMonthOrders = 0;
            double totalMonthRev = 0;
            
            while (!current.isAfter(endDate)) {
                LocalDateTime mStart = current.atStartOfDay();
                LocalDateTime mEnd = current.plusMonths(1).atStartOfDay();
                if (mEnd.isAfter(endOfDay)) mEnd = endOfDay;
                
                int sOrders = orderRepository.countOrdersByDateRange(mStart, mEnd) != null ? orderRepository.countOrdersByDateRange(mStart, mEnd) : 0;
                long bOrders = bulkOrderRepository.countByCreatedAtBetween(mStart, mEnd);
                long mOrders = sOrders + bOrders;
                
                BigDecimal sRev = orderRepository.sumRevenueByDateRange(mStart, mEnd);
                if (sRev == null) sRev = BigDecimal.ZERO;
                BigDecimal bRev = bulkOrderRepository.sumFinalPriceByStatusInAndCreatedAtBetween(Arrays.asList(BulkOrderStatus.COMPLETED, BulkOrderStatus.PROCESSING, BulkOrderStatus.CONFIRMED, BulkOrderStatus.SHIPPED, BulkOrderStatus.PAID), mStart, mEnd);
                if (bRev == null) bRev = BigDecimal.ZERO;
                double mRev = sRev.add(bRev).doubleValue();
                
                totalMonthOrders += mOrders;
                totalMonthRev += mRev;
                
                org.apache.poi.xssf.usermodel.XSSFCellStyle bg = (mRow % 2 == 0) ? zebraStyle : null;
                org.apache.poi.xssf.usermodel.XSSFCellStyle currBg = (mRow % 2 == 0) ? zebraCurrencyStyle : currencyStyle;
                
                Row mr = monthSheet.createRow(mRow++);
                createCell(mr, 0, current.getMonthValue() + "/" + current.getYear(), bg);
                Cell c1 = mr.createCell(1); c1.setCellValue(mOrders); if(bg!=null) c1.setCellStyle(bg);
                Cell c2 = mr.createCell(2); c2.setCellValue(mRev); c2.setCellStyle(currBg);
                
                current = current.plusMonths(1);
            }
            
            Row mFooter = monthSheet.createRow(mRow++);
            createCell(mFooter, 0, "Tổng cộng", footerTextStyle);
            Cell mfc1 = mFooter.createCell(1); mfc1.setCellValue(totalMonthOrders); mfc1.setCellStyle(footerTextStyle);
            Cell mfc2 = mFooter.createCell(2); mfc2.setCellValue(totalMonthRev); mfc2.setCellStyle(footerStyle);
            
            for (int i=0; i<=2; i++) monthSheet.autoSizeColumn(i);

            // --- SHEET 3: ĐƠN HỦY ---
            Sheet cancelSheet = workbook.createSheet("Đơn hủy");
            Row cHeader = cancelSheet.createRow(0);
            createCell(cHeader, 0, "Mã Đơn", headerStyle);
            createCell(cHeader, 1, "Loại", headerStyle);
            createCell(cHeader, 2, "Ngày đặt", headerStyle);
            createCell(cHeader, 3, "Lý do / Trạng thái", headerStyle);
            
            int cRow = 1;
            List<com.sba302.electroshop.entity.Order> singleCanceled = orderRepository.findCancelledOrders(startOfDay, endOfDay);
            List<com.sba302.electroshop.entity.BulkOrder> bulkCanceled = bulkOrderRepository.findByStatusAndCreatedAtBetween(BulkOrderStatus.CANCELLED, startOfDay, endOfDay);
            
            if (singleCanceled != null) {
                for (com.sba302.electroshop.entity.Order o : singleCanceled) {
                    org.apache.poi.xssf.usermodel.XSSFCellStyle bg = (cRow % 2 == 0) ? zebraStyle : null;
                    Row cr = cancelSheet.createRow(cRow++);
                    createCell(cr, 0, String.valueOf(o.getOrderId()), bg);
                    createCell(cr, 1, "Đơn Lẻ", bg);
                    createCell(cr, 2, o.getOrderDate() != null ? o.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "", bg);
                    createCell(cr, 3, "CANCELLED", bg);
                }
            }
            if (bulkCanceled != null) {
                for (com.sba302.electroshop.entity.BulkOrder b : bulkCanceled) {
                    org.apache.poi.xssf.usermodel.XSSFCellStyle bg = (cRow % 2 == 0) ? zebraStyle : null;
                    Row cr = cancelSheet.createRow(cRow++);
                    createCell(cr, 0, "BULK-" + b.getBulkOrderId(), bg);
                    createCell(cr, 1, "Đơn Bulk", bg);
                    createCell(cr, 2, b.getCreatedAt() != null ? b.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "", bg);
                    createCell(cr, 3, b.getCancelReason() != null ? b.getCancelReason() : "CANCELLED", bg);
                }
            }
            for (int i=0; i<=3; i++) cancelSheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting detailed revenue report", e);
            throw new RuntimeException("Failed to export revenue report", e);
        }
    }
    
    // Helper method to write an entire row
    private void addRowData(Sheet sheet, int rowIdx, Object[] data, org.apache.poi.xssf.usermodel.XSSFCellStyle defaultStyle, org.apache.poi.xssf.usermodel.XSSFCellStyle num1Style, org.apache.poi.xssf.usermodel.XSSFCellStyle num2Style) {
        Row row = sheet.createRow(rowIdx);
        
        Cell c0 = row.createCell(1);
        c0.setCellValue(String.valueOf(data[0]));
        if (defaultStyle != null) c0.setCellStyle(defaultStyle);
        
        Cell c1 = row.createCell(2);
        if (data[1] instanceof Double) c1.setCellValue((Double) data[1]);
        else if (data[1] instanceof Long) c1.setCellValue((Long) data[1]);
        else if (data[1] instanceof Integer) c1.setCellValue((Integer) data[1]);
        
        if (num1Style != null) c1.setCellStyle(num1Style);
        else if (defaultStyle != null) c1.setCellStyle(defaultStyle);
        
        Cell c2 = row.createCell(3);
        if (data[2] instanceof Double) c2.setCellValue((Double) data[2]);
        else if (data[2] instanceof Long) c2.setCellValue((Long) data[2]);
         else if (data[2] instanceof Integer) c2.setCellValue((Integer) data[2]);
         
        if (num2Style != null) c2.setCellStyle(num2Style);
        else if (defaultStyle != null) c2.setCellStyle(defaultStyle);
    }
    
    private void createCell(Row row, int column, String value, org.apache.poi.xssf.usermodel.XSSFCellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "₫ 0";
        return String.format("₫ %,d", amount.longValue());
    }

    @Override
    public byte[] exportInventoryReport() {
        log.info("Exporting inventory report");
        List<BranchProductStock> stocks = stockRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tồn kho");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Chi nhánh");
            headerRow.createCell(1).setCellValue("Tên sản phẩm");
            headerRow.createCell(2).setCellValue("Mã sản phẩm");
            headerRow.createCell(3).setCellValue("Số lượng tồn");
            headerRow.createCell(4).setCellValue("Cập nhật lần cuối");
            
            int rowIdx = 1;
            for (BranchProductStock stock : stocks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(stock.getBranch() != null ? stock.getBranch().getBranchName() : "");
                row.createCell(1).setCellValue(stock.getProduct() != null ? stock.getProduct().getProductName() : "");
                row.createCell(2).setCellValue(stock.getProduct() != null ? String.valueOf(stock.getProduct().getProductId()) : "");
                row.createCell(3).setCellValue(stock.getQuantity() != null ? stock.getQuantity() : 0);
                row.createCell(4).setCellValue(stock.getLastUpdated() != null ? stock.getLastUpdated().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
            }
            
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting inventory report", e);
            throw new RuntimeException("Failed to export inventory report");
        }
    }

    @Override
    public byte[] exportTopProductsReport(Integer limit) {
        log.info("Exporting top {} products report", limit);
        List<TopProductResponse> data = adminDashboardService.getTopProducts(limit);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sản phẩm bán chạy");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tên sản phẩm");
            headerRow.createCell(1).setCellValue("Số lượng đã bán");
            
            int rowIdx = 1;
            for (TopProductResponse item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getProduct());
                row.createCell(1).setCellValue(item.getSales() != null ? item.getSales().doubleValue() : 0.0);
            }
            
            for (int i = 0; i <= 1; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting top products report", e);
            throw new RuntimeException("Failed to export top products report");
        }
    }
}
