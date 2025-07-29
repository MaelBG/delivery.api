package com.deliverytech.delivery_api.exception;

public class BusinessException extends RuntimeException { // 
    private String errorCode; // [cite: 236]

    public BusinessException(String message) { // [cite: 237]
        super(message); // [cite: 239]
    }

    public BusinessException(String message, String errorCode) { // [cite: 240]
        super(message); // [cite: 241]
        this.errorCode = errorCode; // [cite: 242]
    }

    public BusinessException(String message, Throwable cause) { // [cite: 244]
        super(message, cause); // [cite: 246]
    }

    public BusinessException(String message, String errorCode, Throwable cause) { // [cite: 247]
        super(message, cause); // [cite: 248]
        this.errorCode = errorCode; // [cite: 249]
    }

    public String getErrorCode() { // [cite: 251]
        return errorCode; // [cite: 252]
    }

    public void setErrorCode(String errorCode) { // [cite: 256]
        this.errorCode = errorCode; // [cite: 257]
    }
}