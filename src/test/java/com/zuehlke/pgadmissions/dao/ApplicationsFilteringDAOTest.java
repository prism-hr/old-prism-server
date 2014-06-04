package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

public class ApplicationsFilteringDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldMergeFiltering() {
        FilterConstraint filter1 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchPredicate(SearchPredicate.CONTAINING)
                .searchTerm("aa").build();
        Filter group = new ApplicationsFilteringBuilder().filters(filter1).build();

        save(group);
        flushAndClearSession();

        FilterConstraint filter2 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchPredicate(SearchPredicate.ON_DATE)
                .searchTerm("bb").build();
        group.getFilters().addAll(Lists.newArrayList(filter2));
        ApplicationsFilteringDAO dao = new ApplicationsFilteringDAO(sessionFactory);
        dao.merge(group);

        flushAndClearSession();
        Filter returnedFiltering = (Filter) sessionFactory.getCurrentSession()
                .get(Filter.class, group.getId());
        assertEquals(returnedFiltering.getId(), returnedFiltering.getId());
    }

}
