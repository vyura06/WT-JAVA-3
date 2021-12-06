package app.repository.impl;

import app.model.Document;
import app.repository.DocumentRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentRepositoryImpl extends XmlRepository<Document, Long> implements DocumentRepository {
    private AtomicLong seed;

    public DocumentRepositoryImpl(File location) {
        super(location);
        seed = new AtomicLong(0);
    }

    @Override
    protected void init(XmlMapper mapper, XMLStreamReader reader) throws IOException {
        String seedStr = mapper.readValue(reader, String.class);
        seed = new AtomicLong(Long.parseLong(seedStr));
    }

    @Override
    protected void write(XmlMapper mapper, XMLStreamWriter writer) throws IOException {
        mapper.writeValue(writer, String.valueOf(seed.get()));
    }

    @Override
    protected Long getId(Document document) {
        return document.getId();
    }

    @Override
    protected Document newElementFrom(Document document) {
        document.setId(generateId());
        return document;
    }

    private Long generateId() {
        return seed.incrementAndGet();
    }
}
