package com.zuehlke.pgadmissions.lifecycle;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.importers.AdvertCategoryImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;

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
    private FullTextSearchService fullTextSearchService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;
    
    @Autowired
    private AdvertCategoryImportService advertCategoryImportService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            systemService.initialiseSystem();
        }
        
        if (BooleanUtils.isTrue(buildIndex)) {
            initialiseHibernateSearchIndexes();
        }
        
        if (BooleanUtils.isTrue(importInstitutionDomicile)) {
            institutionDomicileImportService.importEntities();
        }
        
        if (BooleanUtils.isTrue(importAdvertCategory)) {
            advertCategoryImportService.importEntities();
        }
    }

    @Transactional
    private void initialiseHibernateSearchIndexes() {
        fullTextSearchService.initialiseSearchIndexes();
    }

}
