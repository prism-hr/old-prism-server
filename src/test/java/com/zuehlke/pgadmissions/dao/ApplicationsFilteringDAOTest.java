package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

public class ApplicationsFilteringDAOTest extends AutomaticRollbackTestCase {

    private User user;

    @Test
    public void shouldMergeFiltering() {
        ApplicationsFilter filter1 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchPredicate(SearchPredicate.CONTAINING).searchTerm("aa").build();
        ApplicationsFiltering filtering = new ApplicationsFilteringBuilder().filters(filter1).build();

        save(filtering);
        flushAndClearSession();
        
        ApplicationsFilter filter2 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchPredicate(SearchPredicate.ON_DATE).searchTerm("bb").build();
        filtering.setFilters(Lists.newArrayList(filter2));
        ApplicationsFilteringDAO dao = new ApplicationsFilteringDAO(sessionFactory);
        dao.merge(filtering);

        flushAndClearSession();
        ApplicationsFiltering returnedFiltering = (ApplicationsFiltering) sessionFactory.getCurrentSession().get(ApplicationsFiltering.class, filtering.getId());
        assertEquals(returnedFiltering.getId(), returnedFiltering.getId());
    }

    @Before
    public void prepare() {
        user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .enabled(false).build();
        save(user);
        flushAndClearSession();
    }
    
}
