package app.validator;

import app.exception.ValidatorException;

import java.util.Arrays;

public interface Validator<T> {
    ValidatorException[] EMPTY_ARRAY = {};
    ValidatorException[] validate0(T t);

    default void validate(T t) {
        ValidatorException[] errors = validate0(t);
        if (errors.length != 0) {
            throw new ValidatorException(Arrays.toString(errors), errors);
        }
    }
}
