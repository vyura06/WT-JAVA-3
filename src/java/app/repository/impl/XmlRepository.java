package app.repository.impl;

import app.service.PasswordHasher;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class XmlRepository<T, ID> extends ListRepository<T, ID> implements Flushable {
    private static final String CHARSET_NAME = PasswordHasher.CHARSET.name();
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();
    private static final Supplier<XmlMapper> XML_MAPPER_FACTORY = XmlMapper::new;

    private final XmlMapper mapper;
    private final File location;

    public XmlRepository(File location) {
        this.location = Objects.requireNonNull(location);
        this.mapper = XML_MAPPER_FACTORY.get();
        try {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new FileInputStream(this.location), CHARSET_NAME);

            try {
                reader.next();
                reader.next();

                init(mapper, reader);

                String className = mapper.readValue(reader, String.class);
                Class<?> tClass = Class.forName(className);

                while (reader.hasNext()) {
                    @SuppressWarnings("unchecked")
                    T o = (T) mapper.readValue(reader, tClass);
                    data.add(o);
                }
            } finally {
                reader.close();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public T save(T t) {
        T save = super.save(t);
        flush();
        return save;
    }

    @Override
    public void deleteById(ID id) {
        super.deleteById(id);
        flush();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        flush();
    }

    @Override
    public void flush() {
        try {
            XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(new FileOutputStream(location), CHARSET_NAME);

            writer.writeStartDocument();
            writer.writeStartElement("Data");

            write(mapper, writer);
            if (!data.isEmpty()) {
                mapper.writeValue(writer, data.get(0).getClass().getName());

                for (T t : data)
                    mapper.writeValue(writer, t);
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void init(XmlMapper mapper, XMLStreamReader reader) throws IOException {
    }
    protected void write(XmlMapper mapper, XMLStreamWriter writer) throws IOException {
    }
}
