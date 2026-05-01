package com.shopflow.shopflow.exception;



public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
