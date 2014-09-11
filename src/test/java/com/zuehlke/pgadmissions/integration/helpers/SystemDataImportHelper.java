package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.helpers.ImportedEntityServiceHelperSystem;

@Service
public class SystemDataImportHelper {
    
    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelper;

    public void verifyInstitutionDomicileImport() throws Exception {
        importedEntityServiceHelper.importInstitutionDomiciles();
    }
    
    public void verifyAdvertCategoryImport() throws Exception {
        importedEntityServiceHelper.importAdvertCategories();
    }

}
