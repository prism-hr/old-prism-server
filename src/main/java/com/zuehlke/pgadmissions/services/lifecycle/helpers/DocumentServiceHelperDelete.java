package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class DocumentServiceHelperDelete extends AbstractServiceHelper {

    @Autowired
    private DocumentService documentService;
    
    @Override
    public void execute() throws DeduplicationException, IOException {
        documentService.deleteOrphanDocuments();
    }
    
}
