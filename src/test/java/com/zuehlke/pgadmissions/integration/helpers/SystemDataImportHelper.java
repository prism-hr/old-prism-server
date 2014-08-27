package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.services.importers.AdvertCategoryImportService;
import com.zuehlke.pgadmissions.services.importers.CurrencyImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;

@Service
public class SystemDataImportHelper {
    
    @Autowired
    private AdvertCategoryImportService advertCategoryImportService;
    
    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    @Autowired
    private CurrencyImportService currencyImportService;

    public void verifyInstitutionDomicileImport() throws Exception {
        institutionDomicileImportService.importEntities();
//        currencyImportService.importEntities();
    }
    
    public void verifyOpportunityCategoryImport() throws DataImportException {
        advertCategoryImportService.importEntities();
    }

}
