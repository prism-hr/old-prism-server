package com.zuehlke.pgadmissions.services.helpers.persisters;

import com.zuehlke.pgadmissions.domain.document.Document;

public interface ImageDocumentPersister {

    public void persist(Integer entityId, Document image) throws Exception;

}
