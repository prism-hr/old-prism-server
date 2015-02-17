package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class DocumentServiceHelperDelete extends AbstractServiceHelper {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private SystemService systemService;
    
    @Override
    public void execute() throws DeduplicationException, IOException, IntegrationException {
        DateTime baselineTime = new DateTime();
        documentService.deleteOrphanDocuments(baselineTime);
        if (amazonOn && systemService.getSystem().isDocumentExportEnabled()) {
            documentService.deleteAmazonDocuments(baselineTime);
        }
    }
    
}
