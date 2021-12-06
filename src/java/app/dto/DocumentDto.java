package app.dto;

import app.model.Document;

import java.io.Serializable;
import java.util.Objects;

public class DocumentDto implements Serializable {
    private Long id;
    private String text;

    public DocumentDto() {
    }

    public DocumentDto(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentDto that = (DocumentDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }


    public static Document mapToEntity(DocumentDto documentDto) {
        return documentDto == null ? null :
                new Document(documentDto.getId(),
                        documentDto.getText());
    }

    public static DocumentDto mapToDto(Document document) {
        return document == null ? null :
                new DocumentDto(document.getId(),
                        document.getText());
    }
}
