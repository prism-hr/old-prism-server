package com.zuehlke.pgadmissions.mapping;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

@Service
@Transactional
public class DocumentMapper {

    public DocumentRepresentation getDocumentRepresentation(Integer document) {
        return new DocumentRepresentation().withId(document);
    }

    public DocumentRepresentation getDocumentRepresentation(Document document) {
        return new DocumentRepresentation().withId(document.getId()).withFileName(document.getFileName());
    }

}
