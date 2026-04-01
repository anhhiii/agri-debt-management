package com.manage.debt_management.dto;


public class ResponseApi<T> {

    private boolean success;
    private String message;
    private T data;

    public ResponseApi() {
    }

    public ResponseApi(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseApi<T> ok(T data) {
        return new ResponseApi<>(true, null, data);
    }

    public static <T> ResponseApi<T> ok(String message, T data) {
        return new ResponseApi<>(true, message, data);
    }

    public static <T> ResponseApi<T> error(String message) {
        return new ResponseApi<>(false, message, null);
    }

    public static <T> ResponseApi<T> error(String message, T data) {
        return new ResponseApi<>(false, message, data);
    }
}