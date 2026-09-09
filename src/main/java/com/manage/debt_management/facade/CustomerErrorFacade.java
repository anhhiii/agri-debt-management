package com.manage.debt_management.facade;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.manage.debt_management.constant.HttpStatusConstants;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.exception.ConflictException;
import com.manage.debt_management.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerErrorFacade {

        public ResponseEntity<ResponseApi<?>> handleCreateCustomer(Exception e) {
                // Lỗi từ @Valid — map field errors trực tiếp về từng field
                if (e instanceof MethodArgumentNotValidException mav) {
                        Map<String, String> fieldErrors = new HashMap<>();
                        mav.getBindingResult().getFieldErrors()
                                        .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error("Dữ liệu không hợp lệ", fieldErrors));
                }
                if (e instanceof ValidationException ve) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error(ve.getMessage(), ve.getFieldErrors()));
                }
                if (e instanceof ConflictException ce) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error(ce.getMessage(), Map.of(
                                                        "phone", ce.getMessage())));
                }
                if (e instanceof DuplicateKeyException) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error("Số điện thoại đã tồn tại", Map.of(
                                                        "phone", "Số điện thoại đã được sử dụng bởi khách hàng khác")));
                }
                if (e instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error(e.getMessage()));
                }
                if (e instanceof DataAccessException) {
                        return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR)
                                        .body(ResponseApi.error("Lỗi database khi tạo khách hàng"));
                }
                return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR)
                                .body(ResponseApi.error("Lỗi khi tạo khách hàng: " + e.getMessage()));
        }

        public ResponseEntity<ResponseApi<?>> handleUpdateCustomer(Exception e) {
                // Lỗi từ @Valid — map field errors trực tiếp về từng field
                if (e instanceof MethodArgumentNotValidException mav) {
                        Map<String, String> fieldErrors = new HashMap<>();
                        mav.getBindingResult().getFieldErrors()
                                        .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error("Dữ liệu không hợp lệ", fieldErrors));
                }
                if (e instanceof ValidationException ve) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error(ve.getMessage(), ve.getFieldErrors()));
                }
                if (e instanceof ConflictException ce) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error(ce.getMessage(), Map.of(
                                                        "phone", ce.getMessage())));
                }
                if (e instanceof DuplicateKeyException) {
                        return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST)
                                        .body(ResponseApi.error("Số điện thoại đã tồn tại", Map.of(
                                                        "phone", "Số điện thoại đã được sử dụng bởi khách hàng khác")));
                }
                if (e instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatusConstants.NOT_FOUND)
                                        .body(ResponseApi.error(e.getMessage()));
                }
                if (e instanceof DataAccessException) {
                        return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR)
                                        .body(ResponseApi.error("Lỗi database khi cập nhật khách hàng"));
                }
                return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR)
                                .body(ResponseApi.error("Lỗi khi xử lý khách hàng: " + e.getMessage()));
        }
}