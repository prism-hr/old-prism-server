package com.zuehlke.pgadmissions.mapping;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;

@Service
@Transactional
public class DocumentMapper {

    public FileRepresentation getDocumentRepresentation(Document document) {
        return new FileRepresentation().withId(document.getId()).withFileName(document.getFileName());
    }

}
