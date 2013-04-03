package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;

public class ApplicationsFilterDAOTest extends AutomaticRollbackTestCase {

	private ApplicationsFilterDAO filterDao;

	@Test
	public void shouldGetApplicationsFilterById() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).build();
		ApplicationsFilter applicationsFilter = new ApplicationsFilterBuilder().user(user)
				.searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("Claudio").build();
		save(user, applicationsFilter);

		assertNotNull(applicationsFilter.getId());
		flushAndClearSession();
		assertEquals(applicationsFilter.getId(), filterDao.getApplicationsFilterById(applicationsFilter.getId())
				.getId());

	}

	@Test
	public void shouldGetListOfFiltersForUser() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).build();
		ApplicationsFilter applicationsFilter = new ApplicationsFilterBuilder().user(user)
				.searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("Claudio").build();
		user.setApplicationsFilters(Arrays.asList(applicationsFilter));
		save(user, applicationsFilter);

		assertNotNull(applicationsFilter.getId());
		flushAndClearSession();
		List<ApplicationsFilter> result = user.getApplicationsFilters();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		ApplicationsFilter applicationFilterRetrieved = result.get(0);
		filtersAreEqual(applicationsFilter, applicationFilterRetrieved);
	}

	@Test
	public void shouldGetUniqueFilterForUser() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).id(69).build();
		ApplicationsFilter applicationsFilter = new ApplicationsFilterBuilder().user(user)
				.searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("Claudio").build();
		save(user, applicationsFilter);

		assertNotNull(applicationsFilter.getId());
		flushAndClearSession();
		ApplicationsFilter result = filterDao.getApplicationsFilterByUser(user);
		assertNotNull(result);
		filtersAreEqual(applicationsFilter, result);
	}
	
	@Test
	public void shouldRemoveFilter() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).id(69).build();
		ApplicationsFilter applicationsFilter = new ApplicationsFilterBuilder().user(user)
				.searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("Claudio").build();
		save(user, applicationsFilter);
		
		assertNotNull(applicationsFilter.getId());
		flushAndClearSession();
		filterDao.removeFilter(applicationsFilter);
		
		assertNull(filterDao.getApplicationsFilterById(applicationsFilter.getId()));
	}

	private void filtersAreEqual(ApplicationsFilter expected, ApplicationsFilter actual) {
		assertEquals(expected.getSearchCategory(), actual.getSearchCategory());
		assertEquals(expected.getSearchTerm(), actual.getSearchTerm());
	}

	@Before
	public void setup() {
		filterDao = new ApplicationsFilterDAO(sessionFactory);
	}

}
