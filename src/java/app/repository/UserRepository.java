package app.repository;

import app.model.User;

public interface UserRepository extends Repository<User, Integer> {
    User findByEmail(String email);
}
