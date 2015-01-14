package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class DocumentServiceHelperDelete extends AbstractServiceHelper {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;
    
    @Autowired
    private DocumentService documentService;
    
    @Override
    public void execute() throws DeduplicationException, IOException {
        DateTime baselineTime = new DateTime();
        documentService.deleteOrphanDocuments(baselineTime);
        if (amazonOn) {
            documentService.deleteAmazonDocuments(baselineTime);
        }
    }
    
}
