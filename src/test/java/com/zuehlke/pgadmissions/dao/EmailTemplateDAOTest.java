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
		EmailTemplate template = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").build();

		dao.save(template);

		flushAndClearSession();
		assertNotNull(template.getId());
	}

	@Test
	public void shouldRemoveTemplate() {
		EmailTemplate template = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);
		flushAndClearSession();
		Long id = template.getId();

		dao.remove(template);

		assertNull(dao.getById(id));
	}

	@Test
	public void defaultEmailTemnplateShouldHaveNullVersion() {
		EmailTemplate template = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").build();
		save(template);

		EmailTemplate result = dao.getByName(EmailTemplateName.APPROVAL_NOTIFICATION).get(0);
		assertNotNull(result);
		assertNull(result.getVersion());
	}

	@Test
	public void shouldReturnListAllEmailTemplates() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getAll();

		assertNotNull(result);
		contains(result, template1);
		contains(result, template2);
	}

	private void contains(final List<EmailTemplate> result, final EmailTemplate expected) {
		for (EmailTemplate inTheList : result) {
			if (inTheList.getId().equals(expected.getId())) {
				return;
			}
		}
		fail("Template with id:"+expected.getId()+" was not found in the list");
	}

	@Test
	public void shouldReturnEmailTemplateById() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
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
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		assertEquals(2, result.size());
		compareEmailTemplates(template2, result.get(0));
		compareEmailTemplates(template3, result.get(1));
	}

	@Test
	public void shouldReturnLatestEmailTemplate() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected2!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getLatestByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		compareEmailTemplates(template3, result);
	}

	@Test
	public void shouldReturnDefaultEmailTemplate() {
		DateTime version = new DateTime(Calendar.getInstance().getTimeInMillis());
		version = version.plusDays(1);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected2!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getDefaultByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		compareEmailTemplates(template1, result);
	}

	@Test
	public void shouldReturnActiveEmailTemplate() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.active(true).content("You have been rejected2!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected3!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getActiveByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	@Test
	public void shouldReturnListOfThreeDates() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been approved1!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		assertEquals(3, result.size());
		compareDates(version.toDate(), result.get(template1.getId()));
		compareDates(version.plusDays(1).toDate(), result.get(template2.getId()));
		compareDates(version.plusDays(2).toDate(), result.get(template3.getId()));
	}

	@Test
	public void shouldReturnListOfDatesWithFirstPositionNull() {
		DateTime version = new DateTime(2013, 4, 23, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been approved1!").build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.active(true).content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected3!").version(version.plusDays(2).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		Map<Long, Date> result = dao.getVersionsByName(EmailTemplateName.INTERVIEWER_REMINDER_FIRST);

		assertNotNull(result);
		assertEquals(3, result.size());
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
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected!").version(version.toDate()).build();
		EmailTemplate template3 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
				.content("You have been rejected2!").version(version.plusDays(1).toDate()).build();
		save(template1, template2, template3);
		flushAndClearSession();

		EmailTemplate result = dao.getByNameAndVersion(EmailTemplateName.INTERVIEWER_REMINDER_FIRST, version.toDate());

		assertNotNull(result);
		compareEmailTemplates(template2, result);
	}

	@Test(expected = ConstraintViolationException.class)
	public void shouldFailToSaveTwoEmailTemplatesWithSameNameAndVersion() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been rejected!").version(version.toDate()).build();

		dao.save(template1);
		dao.save(template2);
		flushAndClearSession();
	}

	@Test
	public void shouldReturnListEmailTemplateByName() {
		DateTime version = new DateTime(2013, 3, 12, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").version(version.toDate()).build();
		EmailTemplate template2 = new EmailTemplateBuilder().name(EmailTemplateName.INTERVIEWER_REMINDER)
				.content("You have been rejected!").version(version.toDate()).build();
		save(template1, template2);
		flushAndClearSession();

		List<EmailTemplate> result = dao.getByName(EmailTemplateName.APPROVAL_NOTIFICATION);

		assertNotNull(result);
		assertEquals(1, result.size());
		compareEmailTemplates(template1, result.get(0));
	}

	private void compareEmailTemplates(EmailTemplate expected, EmailTemplate actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}
