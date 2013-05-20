package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.INTERVIEW_ADMINISTRATION_REMINDER;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;

public class NotificationRecordDAOTest extends AutomaticRollbackTestCase {
	
	private NotificationRecordDAO dao;
	
	private NotificationRecord record1;
	private NotificationRecord record2;
	private NotificationRecord record3;
	
	@Before
	public void prepare() {
		dao = new NotificationRecordDAO(sessionFactory);
		DateTime notificationDate = new DateTime(2013, 4, 10, 00, 00);
		record1 = new NotificationRecordBuilder().notificationDate(notificationDate.toDate()).notificationType(INTERVIEW_ADMINISTRATION_REMINDER).build();
		record2 = new NotificationRecordBuilder().notificationDate(notificationDate.plusHours(5).toDate()).notificationType(INTERVIEW_ADMINISTRATION_REMINDER).build();
		record3 = new NotificationRecordBuilder().notificationDate(notificationDate.plusDays(2).toDate()).notificationType(INTERVIEW_ADMINISTRATION_REMINDER).build();

		save(record1, record2,record3);

		flushAndClearSession();
	}
	
	@Test
	public void getNotificationsWithTimeStampGreaterThanShouldReturnAlltheRecords() {
		DateTime timestamp = new DateTime(2013, 4, 9, 00, 00);
		
		List<NotificationRecord> result = dao.getNotificationsWithTimeStampGreaterThan(timestamp.toDate(), INTERVIEW_ADMINISTRATION_REMINDER);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		checkThatListContainsExpectedRecords(asList(record1, record2, record3), result);
	}
	
	@Test
	public void getNotificationsWithTimeStampGreaterThanShouldReturnTwoRecords() {
		DateTime timestamp = new DateTime(2013, 4, 10, 4, 00);
		
		List<NotificationRecord> result = dao.getNotificationsWithTimeStampGreaterThan(timestamp.toDate(), INTERVIEW_ADMINISTRATION_REMINDER);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		checkThatListContainsExpectedRecords(asList(record2, record3), result);
	}
	
	@Test
	public void getNotificationsWithTimeStampGreaterThanShouldNotReturnNonOfTheRecords() {
		DateTime timestamp = new DateTime(2013, 4, 11, 0, 00);
		
		List<NotificationRecord> result = dao.getNotificationsWithTimeStampGreaterThan(timestamp.toDate(), INTERVIEW_ADMINISTRATION_REMINDER);
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		checkThatListContainsExpectedRecords(asList(record3), result);
	}
	
	private void checkThatListContainsExpectedRecords(List<NotificationRecord> expected, List<NotificationRecord> actual) {
		for (NotificationRecord expectedRecord: expected) {
			boolean found =false;
			for (NotificationRecord actualRecord: actual) {
				if (expectedRecord.getId().equals(actualRecord.getId())) {
					found=true;
				}
			}
			if (!found) {
				fail("NotificationRecord: "+expectedRecord+" was not found in the result set");
			}
		}
	}
}
