package com.distrischool.student.exception;

/**
 * Exceção de negócio para regras de negócio violadas
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

