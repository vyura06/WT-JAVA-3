package app.exception;

import app.enums.HttpStatus;

import java.util.Arrays;

public class ValidatorException extends BaseRuntimeException {
    private final Object value;

    public ValidatorException(String message, Object value) {
        super(HttpStatus.BAD_REQUEST, message);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValidatorException{" +
                "status=" + getStatus() +
                "message=" + getMessage() +
                "value=" + (value == null ? "null" :
                (!value.getClass().isArray() ? value :
                        Arrays.toString((Object[]) value))) +
                '}';
    }
}
