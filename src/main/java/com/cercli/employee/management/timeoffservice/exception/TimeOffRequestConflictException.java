package com.cercli.employee.management.timeoffservice.exception;

public class TimeOffRequestConflictException extends RuntimeException {
    public TimeOffRequestConflictException(String message) {
        super(message);
    }
}
