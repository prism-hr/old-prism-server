package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.importers.AdvertCategoryImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;

@Service
public class SystemDataImportHelper {
    
    @Autowired
    private AdvertCategoryImportService advertCategoryImportService;
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    public void verifyInstitutionDomicileImport() throws Exception {
        institutionDomicileImportService.importEntities();
    }
    
    public void verifyOpportunityCategoryImport() throws DataImportException {
        advertCategoryImportService.importEntities();
    }
    
}
