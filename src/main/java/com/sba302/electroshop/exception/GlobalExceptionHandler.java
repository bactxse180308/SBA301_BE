package com.sba302.electroshop.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sba302.electroshop.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.sba302.electroshop.exception.InvalidStatusTransitionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - Bắt tất cả lỗi và trả về JSON format chuẩn.
 * Giúp tránh 500 Internal Server Error mù mờ, đồng thời phản hồi rõ ràng cho
 * FE.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 1️⃣ Lỗi validate @Valid trong DTO (RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, "Validation failed", errors, LocalDateTime.now()));
    }

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse<Object>> handleCommonRuntimeErrors(Exception ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, ex.getMessage(), null, LocalDateTime.now()));
    }

    // ✅ 2️⃣ Lỗi validate trong @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> errors.put(cv.getPropertyPath().toString(), cv.getMessage()));
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, "Constraint violation", errors, LocalDateTime.now()));
    }

    // ✅ 3️⃣ Lỗi thiếu tham số trong request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400,
                        "Missing required parameter: " + ex.getParameterName(),
                        null,
                        LocalDateTime.now()));
    }

    // ✅ 4️⃣ Lỗi sai JSON format (ví dụ: date sai định dạng)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType().equals(LocalDate.class)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(400,
                                "Invalid date format. Please use yyyy-MM-dd (e.g., 2000-01-01)",
                                null,
                                LocalDateTime.now()));
            }
        }
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, "Malformed JSON or wrong data type", null, LocalDateTime.now()));
    }

    // ✅ 5️⃣ Lỗi tài nguyên không tồn tại
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(404, ex.getMessage() != null ? ex.getMessage() : "Resource not found", null,
                        LocalDateTime.now()));
    }

    // ✅ 5.1️⃣ Lỗi banner không tồn tại
    @ExceptionHandler(BannerNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBannerNotFound(BannerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(404, ex.getMessage() != null ? ex.getMessage() : "Banner not found", null,
                        LocalDateTime.now()));
    }

    // ✅ 5.5️⃣ Lỗi tài nguyên xung đột (duplicate email, code, etc.)
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceConflict(ResourceConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>(409, ex.getMessage() != null ? ex.getMessage() : "Resource conflict", null,
                        LocalDateTime.now()));
    }

    // ✅ 6️⃣ Lỗi xung đột dữ liệu trong DB (ví dụ: email trùng, unique constraint)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Data conflict or constraint violation in database";
        if (ex.getMostSpecificCause().getMessage().contains("unique")) {
            message = "Duplicate data: one or more fields must be unique";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>(409, message, null, LocalDateTime.now()));
    }

    // ✅ 🔟 Lỗi API custom (do bạn ném ra trong code)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(400, ex.getMessage(), ex.getErrors(), LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ApiResponse<>(403,
                        "Access denied. You do not have permission to perform this action.",
                        null,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", false);
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ✅ 11️⃣ Lỗi hệ thống khác (NullPointer, unexpected,…)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralError(Exception ex) {
        // ✅ In ra console log chi tiết nhất (full stack trace)
        ex.printStackTrace();

        // ✅ Ghi log lỗi qua Logger (thay vì chỉ System.out)
        Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("Unexpected error occurred: ", ex);

        // ✅ Trả thông tin an toàn về FE
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(500,
                        "Internal server error. Please contact administrator.",
                        null,
                        LocalDateTime.now()));
    }

}
