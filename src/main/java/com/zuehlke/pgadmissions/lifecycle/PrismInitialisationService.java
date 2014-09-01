package com.zuehlke.pgadmissions.lifecycle;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.ImportedEntityServiceHelper;

@Service
public class PrismInitialisationService implements InitializingBean {

    @Value("${startup.hibernate.search.buildIndex}")
    private Boolean buildIndex;

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.institutionDomicile.import}")
    private Boolean importInstitutionDomicile;
    
    @Value("${startup.advertCategory.import}")
    private Boolean importAdvertCategory;

    @Autowired
    private ImportedEntityServiceHelper importedEntityServiceHelper;
    
    @Autowired
    private SystemService systemService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            systemService.initialiseSystem();
        }

        if (BooleanUtils.isTrue(importInstitutionDomicile)) {
            importedEntityServiceHelper.importInstitutionDomiciles();
        }

        if (BooleanUtils.isTrue(importAdvertCategory)) {
            importedEntityServiceHelper.importAdvertCategories();
        }

        if (BooleanUtils.isTrue(buildIndex)) {
            systemService.initializeSearchIndexes();
        }
    }

}
