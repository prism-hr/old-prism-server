package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class DocumentServiceHelperExport implements PrismServiceHelper {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;

    @Inject
    private DocumentService documentService;

    @Inject
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
