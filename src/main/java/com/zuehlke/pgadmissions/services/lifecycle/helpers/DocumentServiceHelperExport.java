package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class DocumentServiceHelperExport implements AbstractServiceHelper {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SystemService systemService;

    @Override
    public void execute() throws IOException, IntegrationException {
        if (amazonOn && systemService.getSystem().isDocumentExportEnabled()) {
            List<Integer> documentIds = documentService.getExportDocuments();
            for (Integer documentId : documentIds) {
                documentService.exportDocumentToAmazon(documentId);
            }
        }
    }

}
