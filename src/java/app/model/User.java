package app.model;

import app.enums.Role;

import java.util.Objects;

public class User {
    private Integer id;
    private String email;
    private String hashPassword;
    private Role role;
    private Long endTime;

    public User() {
    }

    public User(Integer id, String email, String hashPassword, Role role, Long endTime) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
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

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
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
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(hashPassword, user.hashPassword) &&
                role == user.role &&
                Objects.equals(endTime, user.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, hashPassword, role, endTime);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", hashPassword='" + hashPassword + '\'' +
                ", role=" + role +
                ", endTime=" + endTime +
                '}';
    }
}
