package uk.co.alumeni.prism.mapping;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;

@Service
@Transactional
public class DocumentMapper {

    public DocumentRepresentation getDocumentRepresentation(Integer document) {
        return document != null ? new DocumentRepresentation().withId(document) : null;
    }

    public DocumentRepresentation getDocumentRepresentation(Integer document, String fileName) {
        return document != null ? new DocumentRepresentation().withId(document).withFileName(fileName) : null;
    }

    public DocumentRepresentation getDocumentRepresentation(Document document) {
        return document != null ? new DocumentRepresentation().withId(document.getId()).withFileName(document.getFileName()) : null;
    }

}
