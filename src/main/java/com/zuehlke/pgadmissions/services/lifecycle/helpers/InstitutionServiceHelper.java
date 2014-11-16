package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;

@Component
public class InstitutionServiceHelper extends AbstractServiceHelper {

    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private ImportedEntityService importedEntityService;
    
    @Override
    public void execute() throws DeduplicationException {
        List<Integer> institutionIds = institutionService.getInstitutionsToActivate();
        for (Integer institutionId : institutionIds) {
            List<Integer> pendingImports = importedEntityService.getPendingImportEntityFeeds(institutionId);
            if (pendingImports.isEmpty()) {
                institutionService.initializeInstitution(institutionId);
            }
        }
    }

}
