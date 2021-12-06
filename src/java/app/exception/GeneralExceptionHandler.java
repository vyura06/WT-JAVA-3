package app.exception;

import app.enums.HttpStatus;
import app.error.ErrorResponse;

public class GeneralExceptionHandler {
    public ErrorResponse generalHandler(BaseRuntimeException exception) {
        HttpStatus status = exception.getStatus();
        return new ErrorResponse(
                status.getCode(),
                status.getDescription(),
                exception.getMessage());
    }
}
