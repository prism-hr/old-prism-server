package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

@Service
@Transactional
public class SystemDataImportHelper {
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelper;

    public void verifyImport() throws Exception {
        importedEntityServiceHelper.execute();
        systemService.getSystem().setLastDataImportDate(null);
        importedEntityServiceHelper.execute();
    }

}
