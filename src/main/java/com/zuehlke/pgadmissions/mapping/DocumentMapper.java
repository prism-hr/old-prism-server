package com.zuehlke.pgadmissions.mapping;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

@Service
@Transactional
public class DocumentMapper {

    public DocumentRepresentation getDocumentRepresentation(Integer document) {
        return document != null ? new DocumentRepresentation().withId(document) : null;
    }

    public DocumentRepresentation getDocumentRepresentation(Document document) {
        return document != null ? new DocumentRepresentation().withId(document.getId()).withFileName(document.getFileName()) : null;
    }

}
