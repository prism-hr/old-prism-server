package com.zuehlke.pgadmissions.lifecycle;

import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.SystemService;
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

    @Value("${hibernate.search.default.buildIndexOnStartup}")
    private Boolean buildIndex;

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeWorkflow();
        if (BooleanUtils.isTrue(buildIndex)) {
            initialiseHibernateSearchIndexes();
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
