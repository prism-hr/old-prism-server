package com.zuehlke.pgadmissions.lifecycle;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.services.FullTextSearchService;

@Service
public class PrismInitialisationService implements InitializingBean {

    private final FullTextSearchService fullTextSearchService;
    
    private final Boolean buildIndex;
    
    public PrismInitialisationService() {
        this(null, null);
    }
    
    @Autowired
    public PrismInitialisationService(final FullTextSearchService indexService, @Value("${hibernate.search.default.buildIndexOnStartup}") final String buildIndex) {
        this.fullTextSearchService = indexService;
        this.buildIndex = BooleanUtils.toBooleanObject(buildIndex);
    }
    
    @Override
    @Transactional
    public void afterPropertiesSet() throws Exception {
        if (BooleanUtils.isTrue(buildIndex)) {
            initialiseHibernateSearchIndexes();
        }
    }   
    
    private void initialiseHibernateSearchIndexes() {
        fullTextSearchService.initialiseSearchIndexes();
    }
}
