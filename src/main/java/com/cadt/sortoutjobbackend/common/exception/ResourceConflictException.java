package com.cadt.sortoutjobbackend.common.exception;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s : '%s' already exists", resourceName, fieldName, fieldValue));
    }
}
