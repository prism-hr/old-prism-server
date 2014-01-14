package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

public class ApplicationsFilteringDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;
    private Program program;

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
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, user, program);
        flushAndClearSession();
    }
}
