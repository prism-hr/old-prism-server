package com.zuehlke.pgadmissions.mapping;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class DocumentMapper {

    public DocumentRepresentation getDocumentRepresentation(Integer document) {
        return new DocumentRepresentation().withId(document);
    }

    public DocumentRepresentation getDocumentRepresentation(Document document) {
        return document != null ? new DocumentRepresentation().withId(document.getId()).withFileName(document.getFileName()) : null;
    }

}
