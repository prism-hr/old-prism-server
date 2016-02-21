package uk.co.alumeni.prism.services.helpers.persisters;

import uk.co.alumeni.prism.domain.document.Document;

public interface ImageDocumentPersister {

    void persist(Integer entityId, Document image);

}
