package uk.co.alumeni.prism.domain.message;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.document.Document;

@Entity
@Table(name = "message_document")
public class MessageDocument {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public MessageDocument withMessage(Message message) {
        this.message = message;
        return this;
    }

    public MessageDocument withDocument(Document document) {
        this.document = document;
        return this;
    }

}
