package app.repository.impl;

import app.model.User;
import app.repository.UserRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepositoryImpl extends XmlRepository<User, Integer> implements UserRepository {
    private AtomicInteger seed;

    public UserRepositoryImpl(File location) {
        super(location);
        seed = new AtomicInteger(0);
    }

    @Override
    protected void init(XmlMapper mapper, XMLStreamReader reader) throws IOException {
        String seedStr = mapper.readValue(reader, String.class);
        seed = new AtomicInteger(Integer.parseInt(seedStr));
    }

    @Override
    protected void write(XmlMapper mapper, XMLStreamWriter writer) throws IOException {
        mapper.writeValue(writer, String.valueOf(seed.get()));
    }

    @Override
    protected Integer getId(User user) {
        return user.getId();
    }

    @Override
    protected User newElementFrom(User user) {
        user.setId(generateId());
        return user;
    }

    private Integer generateId() {
        return seed.incrementAndGet();
    }

    @Override
    public User findByEmail(String email) {
        return data.stream()
                .filter(user -> Objects.equals(email, user.getEmail()))
                .findAny()
                .orElse(null);
    }
}
