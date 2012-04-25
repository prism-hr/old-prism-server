package com.zuehlke.pgadmissions.utils;

import java.util.Calendar;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.timers.AdminValidationReminderTimerTask;

public class MailTimerTaskTest {
	
	private ApplicationsService applicationsService;
	private ApplicationFormDAO applicationFormDAOMock;
	private AdminValidationReminderTimerTask mailTimerTask;
	
	@Test
	public void shouldReturnTrueIfMoreThanTwoWeeksSinceTheLastEmailReminder(){
		Calendar calendar  = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -21);  
		Date threeWeeksAgo = calendar.getTime();
		ApplicationForm applicationForm = new ApplicationFormBuilder().lastEmailReminderDate(threeWeeksAgo).id(1).toApplicationForm();
		Assert.assertTrue(mailTimerTask.isLastMailSentTwoWeeksOld(applicationForm));
	}
	
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
