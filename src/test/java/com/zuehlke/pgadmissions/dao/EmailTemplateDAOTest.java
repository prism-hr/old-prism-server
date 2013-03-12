package com.zuehlke.pgadmissions.dao;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public class EmailTemplateDAOTest extends AutomaticRollbackTestCase {
	
	private EmailTemplateDAO dao;
	
	@Before
	public void setUp() {
		super.setUp();
		dao = new EmailTemplateDAO(sessionFactory);
	}
	
	@Test
	public void shouldPersistEmailTemplate() {
		EmailTemplate template = new EmailTemplateBuilder()
				.name(EmailTemplateName.APPROVAL_NOTIFICATION)
				.content("You have been approved!").build();
		
		dao.save(template);
		
		flushAndClearSession();
		assertNotNull(template.getId());
	}

	@Test
	public void shouldReturnListOfTwoEmailTemplates() {
		EmailTemplate template1 = new EmailTemplateBuilder()
		.name(EmailTemplateName.APPROVAL_NOTIFICATION)
		.content("You have been approved!").build();
		EmailTemplate template2 = new EmailTemplateBuilder()
		.name(EmailTemplateName.INTERVIEWER_REMINDER_FIRST)
		.content("You have been rejected!").build();
		save(template1, template2);
		flushAndClearSession();
		
		List<EmailTemplate> result = dao.getAll();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		compareEmailTemplates(template1, result.get(0));
		compareEmailTemplates(template2, result.get(1));
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void shouldFailToSaveTwoEmailTemplatesWithSameName() {
		EmailTemplate template1 = new EmailTemplateBuilder()
		.name(EmailTemplateName.APPROVAL_NOTIFICATION)
		.content("You have been approved!").build();
		EmailTemplate template2 = new EmailTemplateBuilder()
		.name(EmailTemplateName.APPROVAL_NOTIFICATION)
		.content("You have been rejected!").build();
		
		dao.save(template1);
		dao.save(template2);
		flushAndClearSession();
	}
	
	@Test
	public void shouldReturnListEmailTemplateByName() {
		EmailTemplate template1 = new EmailTemplateBuilder()
		.name(EmailTemplateName.APPROVAL_NOTIFICATION)
		.content("You have been approved!").build();
		EmailTemplate template2 = new EmailTemplateBuilder()
		.name(EmailTemplateName.INTERVIEWER_REMINDER)
		.content("You have been rejected!").build();
		save(template1, template2);
		flushAndClearSession();
		
		EmailTemplate result = dao.getByName(EmailTemplateName.APPROVAL_NOTIFICATION);

		assertNotNull(result);
		compareEmailTemplates(template1, result);
	}

	private void compareEmailTemplates(EmailTemplate expected, EmailTemplate actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
	}
}
