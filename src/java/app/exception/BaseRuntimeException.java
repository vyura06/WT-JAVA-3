package app.exception;

import app.enums.HttpStatus;

import java.util.Objects;

public class BaseRuntimeException extends RuntimeException {
    private final HttpStatus status;

    public BaseRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = Objects.requireNonNull(status);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
