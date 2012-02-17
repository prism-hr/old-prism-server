package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationFormMappingTest extends AutomaticRollbackTestCase{
	
	@Test
	public void shouldSaveAndLoadApplicationForm(){

	

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(user);
		flushAndClearSession();
		
		ApplicationForm application = new ApplicationForm();
		application.setCob("United Kingdom");
		application.setDob("1988/03/24");
		application.setGender("Female");
		application.setNat("British");
		application.setDescriptionOfResearch("I want to make a research on cancer");
		application.setTitle("Miss");
		application.setUser(user);
		
		assertNull(application.getId());
		
		sessionFactory.getCurrentSession().save(application);
		
		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm)sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);
		
		flushAndClearSession();

		reloadedApplication = (ApplicationForm)sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);
		
		assertEquals(application.getUser(), user);
		assertEquals(application.getDescriptionOfResearch(), reloadedApplication.getDescriptionOfResearch());
	}

}
