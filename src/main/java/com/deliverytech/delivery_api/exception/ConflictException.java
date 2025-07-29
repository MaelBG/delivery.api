package com.deliverytech.delivery_api.exception;

public class ConflictException extends BusinessException { // 
    private String conflictField; // [cite: 283]
    private Object conflictValue; // [cite: 284]

    public ConflictException(String message) { // [cite: 285]
        super(message); // [cite: 286]
        this.setErrorCode("CONFLICT"); // [cite: 287]
    }

    public ConflictException(String message, String conflictField, Object conflictValue) { // [cite: 289]
        super(message); // [cite: 290]
        this.conflictField = conflictField; // [cite: 291]
        this.conflictValue = conflictValue; // [cite: 292]
        this.setErrorCode("CONFLICT"); // [cite: 293]
    }

    public String getConflictField() { // [cite: 295]
        return conflictField; // [cite: 296]
    }

    public Object getConflictValue() { // [cite: 298]
        return conflictValue; // [cite: 299]
    }
}