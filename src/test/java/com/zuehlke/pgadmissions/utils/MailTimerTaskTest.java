package com.zuehlke.pgadmissions.utils;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.timers.AdminValidationReminderTimerTask;

public class MailTimerTaskTest {
	
	private ApplicationsService applicationsService;
	private ApplicationFormDAO applicationFormDAOMock;
	private AdminValidationReminderTimerTask mailTimerTask;

	
	@Before
	public void setUp(){
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock);
		mailTimerTask = new AdminValidationReminderTimerTask();
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
