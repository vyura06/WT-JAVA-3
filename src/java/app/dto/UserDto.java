package app.dto;

import app.enums.Role;
import app.model.User;

import java.io.Serializable;
import java.util.Objects;

public class UserDto implements Serializable {
    private Integer id;
    private String email;
    private String password;
    private Role role;
    private Long endTime;

    public UserDto() {
    }

    public UserDto(Integer id, String email, String password, Role role, Long endTime) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) &&
                Objects.equals(email, userDto.email) &&
                Objects.equals(password, userDto.password) &&
                role == userDto.role &&
                Objects.equals(endTime, userDto.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, role, endTime);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", endTime=" + endTime +
                '}';
    }

    public static User mapToEntity(UserDto userDto) {
        return userDto == null ? null :
                new User(userDto.getId(),
                        userDto.getEmail(),
                        userDto.getPassword(),
                        userDto.getRole(),
                        userDto.getEndTime());
    }

    public static UserDto mapToDto(User user) {
        return user == null ? null :
                new UserDto(user.getId(),
                        user.getEmail(),
                        user.getHashPassword(),
                        user.getRole(),
                        user.getEndTime());
    }
}
