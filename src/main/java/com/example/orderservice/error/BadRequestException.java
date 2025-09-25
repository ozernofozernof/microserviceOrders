package com.example.orderservice.error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}

