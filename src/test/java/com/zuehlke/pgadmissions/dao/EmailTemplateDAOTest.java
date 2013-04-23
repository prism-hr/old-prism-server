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
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public class EmailTemplateDAOTest extends AutomaticRollbackTestCase {

	private EmailTemplateDAO dao;

	@Before
	public void setup() {
		dao = new EmailTemplateDAO(sessionFactory);
	}

	@Test
	public void shouldPersistEmailTemplate() {
		EmailTemplate template = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();

		dao.save(template);

		flushAndClearSession();
		assertNotNull(template.getId());
	}

	@Test
	public void shouldRemoveTemplate() {
		EmailTemplate template = new EmailTemplateBuilder().subject("subject").subject("subject").name(EmailTemplateName.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);
		flushAndClearSession();
		Long id = template.getId();

		dao.remove(template);

		assertNull(dao.getById(id));
	}

	@Test
	public void defaultEmailTemnplateShouldHaveNullVersion() {
		EmailTemplate template = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);

		EmailTemplate result = dao.getByName(EmailTemplateName.REFEREE_NOTIFICATION).get(0);
		assertNotNull(result);
		assertNull(result.getVersion());
	}

	@Test
	public void shouldReturnListAllEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_REMINDER)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getAll();

		assertNotNull(result);
		contains(result, template1);
		contains(result, template2);
	}

	private EmailTemplate contains(final List<EmailTemplate> result, final EmailTemplate expected) {
		for (EmailTemplate inTheList : result) {
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
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_REMINDER)
				.content("You have been approved!").version(version.toDate()).build();
		save(template1);
		flushAndClearSession();

		EmailTemplate result = dao.getById(template1.getId());

		assertNotNull(result);
		compareEmailTemplates(template1, result);
	}

	@Test
	public void shouldReturnListOfTwoEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.REFEREE_REMINDER)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		EmailTemplate actualTemplate2 = contains(result, template2);
		compareEmailTemplates(template2, actualTemplate2);
		EmailTemplate actualTemplate3 = contains(result, template3);
		compareEmailTemplates(template3, actualTemplate3);
	}

	@Test
	public void shouldReturnLatestEmailTemplate() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getLatestByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareEmailTemplates(template3, result);
	}

	@Test
	public void shouldReturnDefaultEmailTemplate() {
		DateTime version = new DateTime(Calendar.getInstance().getTimeInMillis());
		version = version.plusDays(1);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getDefaultByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		assertNull(result.getVersion());
	}

	//This test assumes the DB to me empty
	@Ignore
	@Test
	public void shouldReturnActiveEmailTemplate() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getActiveByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	@Test
	public void shouldReturnListOfThreeDates() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

		assertNotNull(result);
		compareDates(version.toDate(), result.get(template1.getId()));
		compareDates(version.plusDays(1).toDate(), result.get(template2.getId()));
		compareDates(version.plusDays(2).toDate(), result.get(template3.getId()));
	}

	@Test
	public void shouldReturnListOfDatesWithFirstPositionNull() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION);

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
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getByNameAndVersion(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION, version.toDate());

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	
	@Test(expected = ConstraintViolationException.class)
	public void shouldFailToSaveTwoEmailTemplatesWithSameNameAndVersion() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.DIGEST_TASK_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();

		dao.save(template1);
		dao.save(template2);
		flushAndClearSession();
	}

	@Test
	public void shouldReturnListEmailTemplateByName() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.DIGEST_TASK_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().subject("subject").name(EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getByName(EmailTemplateName.DIGEST_TASK_NOTIFICATION);

		assertNotNull(result);
		EmailTemplate actualTemplate1 = contains(result, template1);
		compareEmailTemplates(template1, actualTemplate1);
	}

	private void compareEmailTemplates(EmailTemplate expected, EmailTemplate actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}
