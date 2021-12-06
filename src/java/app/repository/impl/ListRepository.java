package app.repository.impl;

import app.repository.Repository;

import java.util.*;

public abstract class ListRepository<T, ID> implements Repository<T, ID> {
    protected final List<T> data = new ArrayList<>();

    @Override
    public T findById(ID id) {
        return id == null ? null :
                data.stream()
                        .filter(t -> id.equals(getId(t)))
                        .findAny()
                        .orElse(null);
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public T save(T t) {
        if (t == null)
            return null;
        ID id = getId(t);
        if (id != null) {
            ListIterator<T> iterator = data.listIterator();
            while (iterator.hasNext()) {
                if (id.equals(getId(iterator.next()))) {
                    iterator.set(t);
                    return t;
                }
            }
        }
        T element = newElementFrom(t);
        data.add(element);
        return element;
    }

    @Override
    public void deleteById(ID id) {
        if (id == null) return;
        data.removeIf(t1 -> id.equals(getId(t1)));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    protected abstract ID getId(T t);
    protected abstract T newElementFrom(T t);
}
