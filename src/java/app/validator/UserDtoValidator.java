package app.validator;

import app.dto.UserDto;
import app.exception.ValidatorException;

import java.util.regex.Pattern;

public class UserDtoValidator implements Validator<UserDto> {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

    @Override
    public ValidatorException[] validate0(UserDto userDto) {
        if (!(userDto.getEmail() == null || EMAIL_PATTERN.matcher(userDto.getEmail()).matches())) {
            return new ValidatorException[]{new ValidatorException("Email", userDto.getEmail())};
        }
        return EMPTY_ARRAY;
    }
}
