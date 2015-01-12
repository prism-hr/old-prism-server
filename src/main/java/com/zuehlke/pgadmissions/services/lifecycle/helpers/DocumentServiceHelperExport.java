package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.SystemService;

public class DocumentServiceHelperExport extends AbstractServiceHelper {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private SystemService systemService;

    @Override
    public void execute() throws IOException {
        System system = systemService.getSystem();
        if (system.isDocumentExportEnabled()) {
            List<Integer> documentIds = documentService.getDocumentsForExport();
            for (Integer documentId : documentIds) {
                documentService.exportDocument(documentId);
            }
        }
    }

}
