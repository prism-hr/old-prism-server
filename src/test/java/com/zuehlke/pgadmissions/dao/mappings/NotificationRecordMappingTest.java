package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class NotificationRecordMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadNotificationRecord() throws ParseException{	
		
		NotificationType validationReminder = NotificationType.VALIDATION_REMINDER;
		Date notificationDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		NotificationRecord notificationRecord = new NotificationRecordBuilder().notificationType(validationReminder).notificationDate(notificationDate).build();
		sessionFactory.getCurrentSession().saveOrUpdate(notificationRecord);
		assertNotNull(notificationRecord.getId());
		NotificationRecord reloadedNotificationRecord = (NotificationRecord) sessionFactory.getCurrentSession().get(NotificationRecord.class, notificationRecord.getId());
		assertSame(notificationRecord, reloadedNotificationRecord);
		
		flushAndClearSession();
		reloadedNotificationRecord = (NotificationRecord) sessionFactory.getCurrentSession().get(NotificationRecord.class, notificationRecord.getId());
		assertNotSame(notificationRecord, reloadedNotificationRecord);
		assertEquals(notificationRecord.getId(), reloadedNotificationRecord.getId());
		
		assertEquals(notificationDate.getTime(), reloadedNotificationRecord.getDate().getTime());
		assertEquals(validationReminder, reloadedNotificationRecord.getNotificationType());
	}
	
	@Test
	public void shouldLoadApplicationFormForNotificationRecord() throws ParseException{	
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").build();

		save(applicant, program);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		NotificationRecord notificationRecord = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("01 12 2011 14:09:26")).notificationType(NotificationType.UPDATED_NOTIFICATION).build();		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).notificationRecords(notificationRecord).build();
		
		save(application);
		flushAndClearSession();
		NotificationRecord reloadedRecord = (NotificationRecord ) sessionFactory.getCurrentSession().get(NotificationRecord.class, notificationRecord.getId());
		
		assertEquals(application.getId(), reloadedRecord.getApplication().getId());
	}
	
	@Test
	public void shouldLoadUserForNotificationRecord() throws ParseException{	

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		NotificationRecord notificationRecord = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("01 12 2011 14:09:26")).notificationType(NotificationType.UPDATED_NOTIFICATION).build();		
		
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).notificationRecords(notificationRecord).build();
		save(user);
		flushAndClearSession();
		NotificationRecord reloadedRecord = (NotificationRecord ) sessionFactory.getCurrentSession().get(NotificationRecord.class, notificationRecord.getId());
		
		assertEquals(user.getId(), reloadedRecord.getUser().getId());
	}
}
