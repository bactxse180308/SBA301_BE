package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.BulkOrderDetail;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.BulkOrderRepository;
import com.sba302.electroshop.service.BulkOrderExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class BulkOrderExportServiceImpl implements BulkOrderExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Thông tin bên bán (ElectroShop)
    private static final String SELLER_NAME = "ELECTROSHOP VIỆT NAM";
    private static final String SELLER_ADDRESS = "123 Đường Công Nghệ, Quận 1, TP. Hồ Chí Minh";
    private static final String SELLER_TAX_CODE = "0123456789";
    private static final String SELLER_PHONE = "1800 1234";
    private static final String SELLER_EMAIL = "business@electroshop.vn";

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    private final BulkOrderRepository bulkOrderRepository;

    // =========================================================================
    // 1. XUẤT ĐƠN XÁC NHẬN (ORDER CONFIRMATION / SALES ORDER)
    // =========================================================================

    @Override
    public byte[] exportOrderConfirmation(Integer bulkOrderId) {
        log.info("Exporting Order Confirmation for bulkOrderId={}", bulkOrderId);

        BulkOrder order = bulkOrderRepository.findById(bulkOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + bulkOrderId));

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            StylePack styles = new StylePack(workbook);
            Sheet sheet = workbook.createSheet("Xác nhận đơn hàng");
            sheet.setColumnWidth(0, 800);   // cột lề trái
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(3, 3500);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4500);

            int r = 0;

            // ---- TIÊU ĐỀ ----
            r = writeDocTitle(sheet, styles, workbook, r,
                    "ĐƠN XÁC NHẬN ĐẶT HÀNG",
                    "SALES ORDER CONFIRMATION",
                    "SO-" + String.format("%06d", bulkOrderId));

            // ---- THÔNG TIN BÊN BÁN ----
            r = writeSectionHeader(sheet, styles, r, "THÔNG TIN BÊN BÁN (SELLER)");
            r = writeInfoRow(sheet, styles, r, "Công ty:", SELLER_NAME);
            r = writeInfoRow(sheet, styles, r, "Địa chỉ:", SELLER_ADDRESS);
            r = writeInfoRow(sheet, styles, r, "MST:", SELLER_TAX_CODE);
            r = writeInfoRow(sheet, styles, r, "Điện thoại:", SELLER_PHONE);
            r = writeInfoRow(sheet, styles, r, "Email:", SELLER_EMAIL);
            r++;

            // ---- THÔNG TIN BÊN MUA ----
            Company company = order.getCompany();
            r = writeSectionHeader(sheet, styles, r, "THÔNG TIN BÊN MUA (BUYER)");
            r = writeInfoRow(sheet, styles, r, "Công ty:", safeStr(company.getCompanyName()));
            r = writeInfoRow(sheet, styles, r, "MST:", safeStr(company.getTaxCode()));
            r = writeInfoRow(sheet, styles, r, "Địa chỉ:", safeStr(company.getAddress()));
            r = writeInfoRow(sheet, styles, r, "Điện thoại:", safeStr(company.getPhone()));
            r = writeInfoRow(sheet, styles, r, "Email:", safeStr(company.getEmail()));
            r = writeInfoRow(sheet, styles, r, "Người đại diện:", safeStr(company.getRepresentativeName())
                    + (company.getRepresentativePosition() != null ? " (" + company.getRepresentativePosition() + ")" : ""));
            r++;

            // ---- THÔNG TIN ĐƠN HÀNG ----
            r = writeSectionHeader(sheet, styles, r, "THÔNG TIN ĐƠN HÀNG (ORDER DETAILS)");
            r = writeInfoRow(sheet, styles, r, "Mã đơn hàng:", "BULK-" + bulkOrderId);
            r = writeInfoRow(sheet, styles, r, "Ngày đặt hàng:", order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FMT) : "");
            r = writeInfoRow(sheet, styles, r, "Trạng thái:", order.getStatus() != null ? order.getStatus().getDescription() : "");
            r = writeInfoRow(sheet, styles, r, "Địa chỉ giao hàng:", safeStr(order.getShippingAddress()));
            r = writeInfoRow(sheet, styles, r, "Ngày xuất tài liệu:", LocalDateTime.now().format(DATE_FMT));
            r++;

            // ---- BẢNG SẢN PHẨM ----
            r = writeProductTableHeader(sheet, styles, r, false);
            List<BulkOrderDetail> details = order.getDetails();
            int stt = 1;
            BigDecimal subtotal = BigDecimal.ZERO;
            for (BulkOrderDetail detail : details) {
                BigDecimal unitPrice = detail.getAppliedTierPrice() != null
                        ? detail.getAppliedTierPrice()
                        : (detail.getUnitPriceSnapshot() != null ? detail.getUnitPriceSnapshot() : BigDecimal.ZERO);
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
                subtotal = subtotal.add(lineTotal);

                r = writeProductRow(sheet, styles, r, stt++,
                        detail.getProduct() != null ? detail.getProduct().getProductName() : "",
                        detail.getQuantity(),
                        unitPrice,
                        lineTotal,
                        false);
            }

            // ---- TỔNG TIỀN ----
            r = writeSummaryBlock(sheet, styles, r, order, subtotal, false);

            // ---- GHI CHÚ ADMIN ----
            if (order.getAdminNote() != null && !order.getAdminNote().isBlank()) {
                r++;
                r = writeSectionHeader(sheet, styles, r, "GHI CHÚ");
                Row noteRow = sheet.createRow(r++);
                Cell noteCell = noteRow.createCell(1);
                noteCell.setCellValue(order.getAdminNote());
            }

            // ---- CHỮ KÝ ----
            r = writeSignatureBlock(sheet, styles, r + 1, false);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error exporting Order Confirmation for bulkOrderId={}", bulkOrderId, e);
            throw new RuntimeException("Failed to export order confirmation", e);
        }
    }

    // =========================================================================
    // 2. XUẤT HÓA ĐƠN (INVOICE / VAT INVOICE)
    // =========================================================================

    @Override
    public byte[] exportInvoice(Integer bulkOrderId) {
        log.info("Exporting Invoice for bulkOrderId={}", bulkOrderId);

        BulkOrder order = bulkOrderRepository.findById(bulkOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + bulkOrderId));

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            StylePack styles = new StylePack(workbook);
            Sheet sheet = workbook.createSheet("Hóa đơn");
            sheet.setColumnWidth(0, 800);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(3, 3500);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4500);
            sheet.setColumnWidth(6, 4500); // cột thuế VAT

            int r = 0;

            // ---- TIÊU ĐỀ ----
            r = writeDocTitle(sheet, styles, workbook, r,
                    "HÓA ĐƠN BÁN HÀNG",
                    "VAT INVOICE",
                    "INV-" + String.format("%06d", bulkOrderId));

            // ---- THÔNG TIN BÊN BÁN ----
            r = writeSectionHeader(sheet, styles, r, "ĐƠN VỊ BÁN HÀNG (SELLER)");
            r = writeInfoRow(sheet, styles, r, "Công ty:", SELLER_NAME);
            r = writeInfoRow(sheet, styles, r, "Địa chỉ:", SELLER_ADDRESS);
            r = writeInfoRow(sheet, styles, r, "MST:", SELLER_TAX_CODE);
            r = writeInfoRow(sheet, styles, r, "Điện thoại:", SELLER_PHONE);
            r = writeInfoRow(sheet, styles, r, "Email:", SELLER_EMAIL);
            r++;

            // ---- THÔNG TIN BÊN MUA ----
            Company company = order.getCompany();
            r = writeSectionHeader(sheet, styles, r, "ĐƠN VỊ MUA HÀNG (BUYER)");
            r = writeInfoRow(sheet, styles, r, "Công ty:", safeStr(company.getCompanyName()));
            r = writeInfoRow(sheet, styles, r, "MST:", safeStr(company.getTaxCode()));
            r = writeInfoRow(sheet, styles, r, "Địa chỉ:", safeStr(company.getAddress()));
            r = writeInfoRow(sheet, styles, r, "Điện thoại:", safeStr(company.getPhone()));
            r = writeInfoRow(sheet, styles, r, "Email:", safeStr(company.getEmail()));
            r = writeInfoRow(sheet, styles, r, "Người đại diện:", safeStr(company.getRepresentativeName())
                    + (company.getRepresentativePosition() != null ? " (" + company.getRepresentativePosition() + ")" : ""));
            r++;

            // ---- THÔNG TIN HÓA ĐƠN ----
            r = writeSectionHeader(sheet, styles, r, "THÔNG TIN HÓA ĐƠN");
            r = writeInfoRow(sheet, styles, r, "Số hóa đơn:", "INV-" + String.format("%06d", bulkOrderId));
            r = writeInfoRow(sheet, styles, r, "Mã đơn gốc:", "BULK-" + bulkOrderId);
            r = writeInfoRow(sheet, styles, r, "Ngày đặt hàng:", order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FMT) : "");
            r = writeInfoRow(sheet, styles, r, "Ngày xuất hóa đơn:", LocalDateTime.now().format(DATE_FMT));
            r = writeInfoRow(sheet, styles, r, "Trạng thái đơn:", order.getStatus() != null ? order.getStatus().getDescription() : "");
            r = writeInfoRow(sheet, styles, r, "Địa chỉ giao hàng:", safeStr(order.getShippingAddress()));
            r++;

            // ---- BẢNG SẢN PHẨM (có cột VAT) ----
            r = writeProductTableHeader(sheet, styles, r, true);
            List<BulkOrderDetail> details = order.getDetails();
            int stt = 1;
            BigDecimal subtotal = BigDecimal.ZERO;
            for (BulkOrderDetail detail : details) {
                BigDecimal unitPrice = detail.getAppliedTierPrice() != null
                        ? detail.getAppliedTierPrice()
                        : (detail.getUnitPriceSnapshot() != null ? detail.getUnitPriceSnapshot() : BigDecimal.ZERO);
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
                subtotal = subtotal.add(lineTotal);

                r = writeProductRow(sheet, styles, r, stt++,
                        detail.getProduct() != null ? detail.getProduct().getProductName() : "",
                        detail.getQuantity(),
                        unitPrice,
                        lineTotal,
                        true);
            }

            // ---- TỔNG TIỀN (có VAT) ----
            r = writeSummaryBlock(sheet, styles, r, order, subtotal, true);

            // ---- CHỮ KÝ ----
            r = writeSignatureBlock(sheet, styles, r + 1, true);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Error exporting Invoice for bulkOrderId={}", bulkOrderId, e);
            throw new RuntimeException("Failed to export invoice", e);
        }
    }

    // =========================================================================
    // HELPER: WRITE SECTIONS
    // =========================================================================

    /** Viết block tiêu đề tài liệu, trả về row index tiếp theo. */
    private int writeDocTitle(Sheet sheet, StylePack s, XSSFWorkbook wb, int startRow,
                              String titleVi, String titleEn, String docNumber) {
        int r = startRow;

        // Dòng tiêu đề chính
        Row titleRow = sheet.createRow(r++);
        titleRow.setHeightInPoints(28);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 1, 5));
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(titleVi);
        titleCell.setCellStyle(s.docTitle);

        // Dòng tiêu đề phụ (EN)
        Row subTitleRow = sheet.createRow(r++);
        sheet.addMergedRegion(new CellRangeAddress(subTitleRow.getRowNum(), subTitleRow.getRowNum(), 1, 5));
        Cell subCell = subTitleRow.createCell(1);
        subCell.setCellValue(titleEn);
        subCell.setCellStyle(s.docSubTitle);

        // Số tài liệu
        Row numRow = sheet.createRow(r++);
        sheet.addMergedRegion(new CellRangeAddress(numRow.getRowNum(), numRow.getRowNum(), 1, 5));
        Cell numCell = numRow.createCell(1);
        numCell.setCellValue("Số: " + docNumber);
        numCell.setCellStyle(s.docNumber);

        r++; // khoảng trắng
        return r;
    }

    /** Viết header section, trả về row index tiếp theo. */
    private int writeSectionHeader(Sheet sheet, StylePack s, int rowIdx, String title) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(18);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 5));
        Cell cell = row.createCell(1);
        cell.setCellValue(title);
        cell.setCellStyle(s.sectionHeader);
        return rowIdx + 1;
    }

    /** Viết 1 dòng label - value, trả về row index tiếp theo. */
    private int writeInfoRow(Sheet sheet, StylePack s, int rowIdx, String label, String value) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(1);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(s.infoLabel);

        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 5));
        Cell valCell = row.createCell(2);
        valCell.setCellValue(value);
        valCell.setCellStyle(s.infoValue);
        return rowIdx + 1;
    }

    /** Viết header bảng sản phẩm. withVat = true sẽ thêm cột Thuế VAT. */
    private int writeProductTableHeader(Sheet sheet, StylePack s, int rowIdx, boolean withVat) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(18);
        int col = 1;
        createHeaderCell(row, col++, "STT", s.tableHeader);
        createHeaderCell(row, col++, "Tên sản phẩm", s.tableHeader);
        createHeaderCell(row, col++, "Số lượng", s.tableHeader);
        createHeaderCell(row, col++, "Đơn giá (VNĐ)", s.tableHeader);
        if (withVat) {
            createHeaderCell(row, col++, "Thuế VAT (10%)", s.tableHeader);
        }
        createHeaderCell(row, col, "Thành tiền (VNĐ)", s.tableHeader);
        return rowIdx + 1;
    }

    /** Viết 1 dòng sản phẩm. */
    private int writeProductRow(Sheet sheet, StylePack s, int rowIdx, int stt,
                                String productName, int quantity,
                                BigDecimal unitPrice, BigDecimal lineTotal,
                                boolean withVat) {
        Row row = sheet.createRow(rowIdx);
        XSSFCellStyle bg = (rowIdx % 2 == 0) ? s.zebraRow : null;

        int col = 1;
        createCell(row, col++, String.valueOf(stt), bg != null ? bg : s.tableCell);
        createCell(row, col++, productName, bg != null ? bg : s.tableCell);

        Cell qtyCell = row.createCell(col++);
        qtyCell.setCellValue(quantity);
        qtyCell.setCellStyle(bg != null ? bg : s.tableCell);

        Cell priceCell = row.createCell(col++);
        priceCell.setCellValue(unitPrice.doubleValue());
        priceCell.setCellStyle(bg != null ? s.zebraCurrency : s.currency);

        if (withVat) {
            BigDecimal vatAmount = lineTotal.multiply(VAT_RATE).setScale(0, RoundingMode.HALF_UP);
            Cell vatCell = row.createCell(col++);
            vatCell.setCellValue(vatAmount.doubleValue());
            vatCell.setCellStyle(bg != null ? s.zebraCurrency : s.currency);
        }

        Cell totalCell = row.createCell(col);
        totalCell.setCellValue(lineTotal.doubleValue());
        totalCell.setCellStyle(bg != null ? s.zebraCurrency : s.currency);

        return rowIdx + 1;
    }

    /** Viết block tổng kết (subtotal, voucher, ship, final). withVat thêm dòng VAT. */
    private int writeSummaryBlock(Sheet sheet, StylePack s, int rowIdx, BulkOrder order,
                                  BigDecimal subtotal, boolean withVat) {
        int r = rowIdx;
        int labelCol = 3;
        int valueCol = 5;

        // Separator
        Row sepRow = sheet.createRow(r++);
        sheet.addMergedRegion(new CellRangeAddress(sepRow.getRowNum(), sepRow.getRowNum(), 1, 5));

        // Tạm tính
        r = writeSummaryLine(sheet, s, r, labelCol, valueCol, "Tạm tính:", subtotal, false);

        // VAT (chỉ trên invoice)
        if (withVat) {
            BigDecimal vatTotal = subtotal.multiply(VAT_RATE).setScale(0, RoundingMode.HALF_UP);
            r = writeSummaryLine(sheet, s, r, labelCol, valueCol, "Thuế VAT (10%):", vatTotal, false);
        }

        // Voucher
        if (order.getVoucherCode() != null && order.getVoucherDiscountAmount() != null
                && order.getVoucherDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            String voucherLabel = "Giảm giá voucher [" + order.getVoucherCode() + "]:";
            BigDecimal discount = order.getVoucherDiscountAmount().negate();
            r = writeSummaryLine(sheet, s, r, labelCol, valueCol, voucherLabel, discount, false);
        }

        // Phí vận chuyển
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        boolean waived = Boolean.TRUE.equals(order.getShippingFeeWaived());
        String shippingLabel = waived ? "Phí vận chuyển: (Miễn phí)" : "Phí vận chuyển:";
        r = writeSummaryLine(sheet, s, r, labelCol, valueCol, shippingLabel,
                waived ? BigDecimal.ZERO : shippingFee, false);

        // Tổng cộng
        BigDecimal finalPrice = order.getFinalPrice() != null ? order.getFinalPrice() : BigDecimal.ZERO;
        r = writeSummaryLine(sheet, s, r, labelCol, valueCol, "TỔNG CỘNG:", finalPrice, true);

        // Số tiền bằng chữ
        Row wordsRow = sheet.createRow(r++);
        sheet.addMergedRegion(new CellRangeAddress(wordsRow.getRowNum(), wordsRow.getRowNum(), 1, 5));
        Cell wordsCell = wordsRow.createCell(1);
        wordsCell.setCellValue("(Bằng chữ: " + numberToWords(finalPrice.longValue()) + " đồng)");
        wordsCell.setCellStyle(s.infoValue);

        return r;
    }

    private int writeSummaryLine(Sheet sheet, StylePack s, int rowIdx,
                                 int labelCol, int valueCol, String label,
                                 BigDecimal value, boolean isFinal) {
        Row row = sheet.createRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, labelCol, valueCol - 1));
        Cell labelCell = row.createCell(labelCol);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(isFinal ? s.summaryFinalLabel : s.summaryLabel);

        Cell valueCell = row.createCell(valueCol);
        valueCell.setCellValue(value.doubleValue());
        valueCell.setCellStyle(isFinal ? s.summaryFinalValue : s.summaryValue);
        return rowIdx + 1;
    }

    /** Viết block chữ ký cuối trang. */
    private int writeSignatureBlock(Sheet sheet, StylePack s, int startRow, boolean includeBuyer) {
        int r = startRow + 1;
        Row dateRow = sheet.createRow(r++);
        sheet.addMergedRegion(new CellRangeAddress(dateRow.getRowNum(), dateRow.getRowNum(), 3, 5));
        Cell dateCell = dateRow.createCell(3);
        dateCell.setCellValue("TP. Hồ Chí Minh, ngày " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd 'tháng' MM 'năm' yyyy")));
        dateCell.setCellStyle(s.infoValue);

        r++;
        Row sigLabelRow = sheet.createRow(r++);

        Cell sellerSigLabel = sigLabelRow.createCell(1);
        sellerSigLabel.setCellValue(includeBuyer ? "ĐẠI DIỆN BÊN BÁN" : "ĐẠI DIỆN ELECTROSHOP");
        sellerSigLabel.setCellStyle(s.signatureLabel);

        if (includeBuyer) {
            Cell buyerSigLabel = sigLabelRow.createCell(4);
            buyerSigLabel.setCellValue("ĐẠI DIỆN BÊN MUA");
            buyerSigLabel.setCellStyle(s.signatureLabel);
        }

        // 3 dòng trắng để ký
        r += 3;
        Row sigNameRow = sheet.createRow(r++);
        Cell sellerName = sigNameRow.createCell(1);
        sellerName.setCellValue("(Ký, ghi rõ họ tên, đóng dấu)");
        sellerName.setCellStyle(s.infoValue);

        if (includeBuyer) {
            Cell buyerName = sigNameRow.createCell(4);
            buyerName.setCellValue("(Ký, ghi rõ họ tên, đóng dấu)");
            buyerName.setCellStyle(s.infoValue);
        }

        return r;
    }

    // =========================================================================
    // HELPER: CELL UTILS
    // =========================================================================

    private void createHeaderCell(Row row, int col, String value, XSSFCellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int col, String value, XSSFCellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }

    private String safeStr(String val) {
        return val != null ? val : "";
    }

    /**
     * Chuyển số thành chữ đơn giản (hỗ trợ đến hàng tỷ).
     * Chỉ phục vụ mục đích hiển thị trên hóa đơn.
     */
    private String numberToWords(long number) {
        if (number == 0) return "không";
        if (number < 0) return "âm " + numberToWords(-number);

        String[] units = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] teens = {"mười", "mười một", "mười hai", "mười ba", "mười bốn",
                "mười lăm", "mười sáu", "mười bảy", "mười tám", "mười chín"};

        StringBuilder result = new StringBuilder();
        if (number >= 1_000_000_000) {
            result.append(numberToWords(number / 1_000_000_000)).append(" tỷ ");
            number %= 1_000_000_000;
        }
        if (number >= 1_000_000) {
            result.append(numberToWords(number / 1_000_000)).append(" triệu ");
            number %= 1_000_000;
        }
        if (number >= 1_000) {
            result.append(numberToWords(number / 1_000)).append(" nghìn ");
            number %= 1_000;
        }
        if (number >= 100) {
            result.append(units[(int) (number / 100)]).append(" trăm ");
            number %= 100;
        }
        if (number >= 20) {
            result.append(units[(int) (number / 10)]).append(" mươi ");
            number %= 10;
            if (number > 0) result.append(number == 5 ? "lăm " : units[(int) number]).append(" ");
        } else if (number >= 10) {
            result.append(teens[(int) (number - 10)]).append(" ");
        } else if (number > 0) {
            result.append(units[(int) number]).append(" ");
        }

        return result.toString().trim();
    }

    // =========================================================================
    // INNER CLASS: STYLE PACK
    // =========================================================================

    /**
     * Gom toàn bộ style dùng chung để tránh tạo lại nhiều lần.
     * Apache POI giới hạn ~64k cell style per workbook.
     */
    private static class StylePack {

        final XSSFCellStyle docTitle;
        final XSSFCellStyle docSubTitle;
        final XSSFCellStyle docNumber;
        final XSSFCellStyle sectionHeader;
        final XSSFCellStyle infoLabel;
        final XSSFCellStyle infoValue;
        final XSSFCellStyle tableHeader;
        final XSSFCellStyle tableCell;
        final XSSFCellStyle zebraRow;
        final XSSFCellStyle currency;
        final XSSFCellStyle zebraCurrency;
        final XSSFCellStyle summaryLabel;
        final XSSFCellStyle summaryValue;
        final XSSFCellStyle summaryFinalLabel;
        final XSSFCellStyle summaryFinalValue;
        final XSSFCellStyle signatureLabel;

        StylePack(XSSFWorkbook wb) {
            DataFormat fmt = wb.createDataFormat();
            short currFmt = fmt.getFormat("#,##0");

            // --- Fonts ---
            Font boldFont = wb.createFont();
            boldFont.setBold(true);

            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font subTitleFont = wb.createFont();
            subTitleFont.setFontHeightInPoints((short) 11);
            subTitleFont.setItalic(true);

            Font whiteFont = wb.createFont();
            whiteFont.setBold(true);
            whiteFont.setColor(IndexedColors.WHITE.getIndex());

            Font labelFont = wb.createFont();
            labelFont.setBold(true);
            labelFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

            Font totalFont = wb.createFont();
            totalFont.setBold(true);
            totalFont.setFontHeightInPoints((short) 12);
            totalFont.setColor(IndexedColors.DARK_GREEN.getIndex());

            // --- Colors ---
            byte[] primaryColor = {(byte) 31, (byte) 114, (byte) 68};   // xanh lá đậm
            byte[] lightGreen   = {(byte) 235, (byte) 247, (byte) 240};  // nền nhạt
            byte[] zebraColor   = {(byte) 242, (byte) 249, (byte) 245};  // xanh lá rất nhạt
            byte[] headerColor  = {(byte) 21, (byte) 87, (byte) 52};     // xanh lá tối hơn

            // docTitle
            docTitle = wb.createCellStyle();
            docTitle.setFont(titleFont);
            docTitle.setAlignment(HorizontalAlignment.CENTER);

            // docSubTitle
            docSubTitle = wb.createCellStyle();
            docSubTitle.setFont(subTitleFont);
            docSubTitle.setAlignment(HorizontalAlignment.CENTER);

            // docNumber
            docNumber = wb.createCellStyle();
            docNumber.setFont(boldFont);
            docNumber.setAlignment(HorizontalAlignment.CENTER);

            // sectionHeader — nền xanh đậm, chữ trắng
            sectionHeader = wb.createCellStyle();
            sectionHeader.setFillForegroundColor(new XSSFColor(headerColor, null));
            sectionHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            sectionHeader.setFont(whiteFont);
            sectionHeader.setAlignment(HorizontalAlignment.LEFT);
            sectionHeader.setVerticalAlignment(VerticalAlignment.CENTER);

            // infoLabel — in đậm, màu xám
            infoLabel = wb.createCellStyle();
            infoLabel.setFont(labelFont);

            // infoValue
            infoValue = wb.createCellStyle();
            infoValue.setWrapText(true);

            // tableHeader — nền xanh lá, chữ trắng, canh giữa
            tableHeader = wb.createCellStyle();
            tableHeader.setFillForegroundColor(new XSSFColor(primaryColor, null));
            tableHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeader.setFont(whiteFont);
            tableHeader.setAlignment(HorizontalAlignment.CENTER);
            tableHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            tableHeader.setBorderBottom(BorderStyle.THIN);

            // tableCell
            tableCell = wb.createCellStyle();
            tableCell.setBorderBottom(BorderStyle.HAIR);

            // zebraRow — nền xanh lá rất nhạt
            zebraRow = wb.createCellStyle();
            zebraRow.setFillForegroundColor(new XSSFColor(zebraColor, null));
            zebraRow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            zebraRow.setBorderBottom(BorderStyle.HAIR);

            // currency
            currency = wb.createCellStyle();
            currency.setDataFormat(currFmt);
            currency.setAlignment(HorizontalAlignment.RIGHT);
            currency.setBorderBottom(BorderStyle.HAIR);

            // zebraCurrency
            zebraCurrency = wb.createCellStyle();
            zebraCurrency.setFillForegroundColor(new XSSFColor(zebraColor, null));
            zebraCurrency.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            zebraCurrency.setDataFormat(currFmt);
            zebraCurrency.setAlignment(HorizontalAlignment.RIGHT);
            zebraCurrency.setBorderBottom(BorderStyle.HAIR);

            // summaryLabel
            summaryLabel = wb.createCellStyle();
            summaryLabel.setAlignment(HorizontalAlignment.RIGHT);

            // summaryValue
            summaryValue = wb.createCellStyle();
            summaryValue.setDataFormat(currFmt);
            summaryValue.setAlignment(HorizontalAlignment.RIGHT);

            // summaryFinalLabel — in đậm, viền trên
            summaryFinalLabel = wb.createCellStyle();
            summaryFinalLabel.setFont(totalFont);
            summaryFinalLabel.setAlignment(HorizontalAlignment.RIGHT);
            summaryFinalLabel.setBorderTop(BorderStyle.MEDIUM);

            // summaryFinalValue — in đậm, số, viền trên
            summaryFinalValue = wb.createCellStyle();
            summaryFinalValue.setFont(totalFont);
            summaryFinalValue.setDataFormat(currFmt);
            summaryFinalValue.setAlignment(HorizontalAlignment.RIGHT);
            summaryFinalValue.setBorderTop(BorderStyle.MEDIUM);

            // signatureLabel
            signatureLabel = wb.createCellStyle();
            signatureLabel.setFont(boldFont);
            signatureLabel.setAlignment(HorizontalAlignment.CENTER);
        }
    }
}