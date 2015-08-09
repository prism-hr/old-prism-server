package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class DocumentServiceHelperDelete implements PrismServiceHelper {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;

    @Inject
    private DocumentService documentService;

    @Inject
    private SystemService systemService;

    @Override
    public void execute() throws Exception {
        DateTime baselineTime = new DateTime();
        documentService.deleteOrphanDocuments(baselineTime);
        if (amazonOn && systemService.getSystem().isDocumentExportEnabled()) {
            documentService.deleteAmazonDocuments(baselineTime);
        }
    }

    @Override
    public void shutdown() {
        return;
    }
    
}
