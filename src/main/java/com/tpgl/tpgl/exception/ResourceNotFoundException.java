package com.tpgl.tpgl.exception;

/**
 * Exception levée lorsqu'une ressource métier demandée est introuvable en base de données.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s introuvable avec %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
