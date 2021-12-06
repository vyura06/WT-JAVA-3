package app.service;

import app.dto.UserDto;
import app.model.User;
import app.repository.UserRepository;
import app.validator.UserDtoValidator;
import app.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserService {
    private final UserDtoValidator validator = new UserDtoValidator();
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public UserDto findById(Integer id) {
        User user = repository.findById(id);
        return UserDto.mapToDto(user);
    }

    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserDto::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto findByEmail(String email) {
        return UserDto.mapToDto(repository.findByEmail(email));
    }

    public UserDto save(UserDto dto) {
        validator.validate(dto);
        return UserDto.mapToDto(repository.save(UserDto.mapToEntity(dto)));
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Validator<UserDto> getValidator() {
        return validator;
    }
}
