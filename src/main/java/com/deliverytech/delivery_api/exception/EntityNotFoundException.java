package com.deliverytech.delivery_api.exception;

public class EntityNotFoundException extends BusinessException { // 
    private String entityName; // [cite: 261]
    private Object entityId; // [cite: 262]

    public EntityNotFoundException(String entityName, Object entityId) { // [cite: 263]
        super(String.format("%s com ID %s não foi encontrado(a)", entityName, entityId)); // [cite: 264]
        this.entityName = entityName; // [cite: 266]
        this.entityId = entityId; // [cite: 267]
        this.setErrorCode("ENTITY_NOT_FOUND"); // [cite: 268]
    }

    public EntityNotFoundException(String message) { // [cite: 269]
        super(message); // [cite: 270]
        this.setErrorCode("ENTITY_NOT_FOUND"); // [cite: 271]
    }

    public String getEntityName() { // [cite: 273]
        return entityName; // [cite: 274]
    }

    public Object getEntityId() { // [cite: 276]
        return entityId; // [cite: 277]
    }
}