package app.repository;

import java.util.List;
import java.util.function.Predicate;

public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T t);
    void deleteById(ID t);
    void deleteAll();
}