package com.zuehlke.pgadmissions.lifecycle;

import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrismInitialisationService implements InitializingBean {

    @Autowired
    private FullTextSearchService fullTextSearchService;

    @Autowired
    private SystemService systemService;

    @Value("${startup.hibernate.search.buildIndex}")
    private Boolean buildIndex;

    @Value("${startup.workflow.initialize}")
    private Boolean initializeWorkflow;

    @Value("${startup.isoCountries.import}")
    private Boolean importIsoCountries;

    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (BooleanUtils.isTrue(initializeWorkflow)) {
            initializeWorkflow();
        }
        if (BooleanUtils.isTrue(buildIndex)) {
            initialiseHibernateSearchIndexes();
        }
        if (BooleanUtils.isTrue(importIsoCountries)) {
            institutionDomicileImportService.importEntities("xml/iso/iso_country_codes.xml");
        }
    }

    @Transactional
    private void initializeWorkflow() {
        systemService.initialiseSystem();
    }

    @Transactional
    private void initialiseHibernateSearchIndexes() {
        fullTextSearchService.initialiseSearchIndexes();
    }

}
