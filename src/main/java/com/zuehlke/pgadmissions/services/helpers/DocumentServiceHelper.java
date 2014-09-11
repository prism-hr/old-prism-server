package com.zuehlke.pgadmissions.services.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class DocumentServiceHelper extends AbstractServiceHelper {

    @Autowired
    private DocumentService documentService;
    
    @Override
    public void execute() throws DeduplicationException {
        documentService.deleteOrphanDocuments();
    }
    
}
