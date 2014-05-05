package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

public class NotificationTemplateDAOTest extends AutomaticRollbackTestCase {

	private NotificationTemplateDAO dao;

	@Before
	public void prepare() {
		dao = new NotificationTemplateDAO(sessionFactory);
		for (NotificationTemplate template : dao.getAll()) {
		    dao.remove(template);
		}
	}

	@Test
	public void shouldPersistEmailTemplate() {
		NotificationTemplate template = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();

		dao.save(template);

		flushAndClearSession();
		assertNotNull(template.getId());
	}

	@Test
	public void shouldRemoveTemplate() {
		NotificationTemplate template = new EmailTemplateBuilder().subject("subject").subject("subject").name(NotificationTemplateId.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);
		flushAndClearSession();
		Long id = template.getId();

		dao.remove(template);

		assertNull(dao.getById(id));
	}

	@Test
	public void defaultEmailTemnplateShouldHaveNullVersion() {
		NotificationTemplate template = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);

		NotificationTemplate result = dao.getByName(NotificationTemplateId.REFEREE_NOTIFICATION).get(0);
		assertNotNull(result);
		assertNull(result.getVersion());
	}

	@Test
	public void shouldReturnListAllEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_REMINDER)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<NotificationTemplate> result = dao.getAll();

		assertNotNull(result);
		contains(result, template1);
		contains(result, template2);
	}

	private NotificationTemplate contains(final List<NotificationTemplate> result, final NotificationTemplate expected) {
		for (NotificationTemplate inTheList : result) {
			if (inTheList.getId().equals(expected.getId())) {
				return inTheList;
			}
		}
		fail("Template with id:"+expected.getId()+" was not found in the list");
		return null;
	}

	@Test
	public void shouldReturnEmailTemplateById() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_REMINDER)
				.content("You have been approved!").version(version.toDate()).build();
		save(template1);
		flushAndClearSession();

		NotificationTemplate result = dao.getById(template1.getId());

		assertNotNull(result);
		compareEmailTemplates(template1, result);
	}

	@Test
	public void shouldReturnListOfTwoEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.REFEREE_REMINDER)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		List<NotificationTemplate> result = dao.getByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		NotificationTemplate actualTemplate2 = contains(result, template2);
		compareEmailTemplates(template2, actualTemplate2);
		NotificationTemplate actualTemplate3 = contains(result, template3);
		compareEmailTemplates(template3, actualTemplate3);
	}

	@Test
	public void shouldReturnLatestEmailTemplate() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.plusDays(1).toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		NotificationTemplate result = dao.getLatestByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareEmailTemplates(template3, result);
	}

	@Test
	public void shouldReturnDefaultEmailTemplate() {
		DateTime version = new DateTime(Calendar.getInstance().getTimeInMillis());
		version = version.plusDays(1);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		NotificationTemplate result = dao.getDefaultByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		assertNull(result.getVersion());
	}

	//This test assumes the DB to me empty
	@Ignore
	@Test
	public void shouldReturnActiveEmailTemplate() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		NotificationTemplate result = dao.getActiveByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	@Test
	public void shouldReturnListOfThreeDates() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareDates(version.toDate(), result.get(template1.getId()));
		compareDates(version.plusDays(1).toDate(), result.get(template2.getId()));
		compareDates(version.plusDays(2).toDate(), result.get(template3.getId()));
	}

	@Test
	public void shouldReturnListOfDatesWithFirstPositionNull() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		assertNull(result.get(template1.getId()));
		compareDates(version.plusDays(1).toDate(), result.get(template2.getId()));
		compareDates(version.plusDays(2).toDate(), result.get(template3.getId()));
	}

	private void compareDates(Date expected, Date actual) {
		assertEquals(0, expected.compareTo(actual));
	}

	@Test
	public void shouldReturnUniqueEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		NotificationTemplate template3 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		NotificationTemplate result = dao.getByNameAndVersion(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION, version.toDate());

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	
	@Test(expected = ConstraintViolationException.class)
	public void shouldFailToSaveTwoEmailTemplatesWithSameNameAndVersion() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.DIGEST_TASK_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();

		dao.save(template1);
		dao.save(template2);
		flushAndClearSession();
	}

	@Test
	public void shouldReturnListEmailTemplateByName() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		NotificationTemplate template1 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		NotificationTemplate template2 = new EmailTemplateBuilder().subject("subject").name(NotificationTemplateId.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<NotificationTemplate> result = dao.getByName(NotificationTemplateId.DIGEST_TASK_NOTIFICATION);

		assertNotNull(result);
		NotificationTemplate actualTemplate1 = contains(result, template1);
		compareEmailTemplates(template1, actualTemplate1);
	}

	private void compareEmailTemplates(NotificationTemplate expected, NotificationTemplate actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}
