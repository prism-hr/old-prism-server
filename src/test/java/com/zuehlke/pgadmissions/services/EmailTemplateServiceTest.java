package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER_FIRST;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.EmailTemplateDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public class EmailTemplateServiceTest {

	private EmailTemplateDAO daoMock;
	private EmailTemplateService service;

	@Before
	public void setup() {
		daoMock = createMock(EmailTemplateDAO.class);
		service = new EmailTemplateService(daoMock);
	}

	@Test
	public void shouldReturnEmailTemplate() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).content("Template content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		expect(daoMock.getByName(INTERVIEWER_REMINDER_FIRST)).andReturn(template);
		replay(daoMock);
		
		EmailTemplate result = service.getEmailTemplateByName(INTERVIEWER_REMINDER_FIRST);
		
		areEqual(template, result);
		
		verify(daoMock);
	}

	private void areEqual(EmailTemplate expected, EmailTemplate actual) {
		assertNotNull(actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
	}
	
	@Test
	public void shouldReturnNoEmailTemplateList() {
		EmailTemplate template1 = new EmailTemplateBuilder().id(1L).content("Template1 content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		EmailTemplate template2 = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(EmailTemplateName.APPROVAL_NOTIFICATION).build();
		expect(daoMock.getAll()).andReturn(asList(template1, template2));
		replay(daoMock);
		
		List<EmailTemplate> result = service.getAllEmailTemplates();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		areEqual(template1, result.get(0));
		areEqual(template2, result.get(1));
		
		verify(daoMock);
	}
	
	@Test
	public void shouldUpdateEmailTemplate() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).content("Template content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		EmailTemplate updatedTemplate = new EmailTemplateBuilder().id(1L).content("Changed Content!!")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		expect(daoMock.getByName(INTERVIEWER_REMINDER_FIRST)).andReturn(template).times(2);
		daoMock.save(isA(EmailTemplate.class));
		expect(daoMock.getByName(INTERVIEWER_REMINDER_FIRST)).andReturn(updatedTemplate);
		replay(daoMock);
		
		EmailTemplate toUpdate = service.getEmailTemplateByName(INTERVIEWER_REMINDER_FIRST);
		toUpdate.setContent("Changed Content!!");
		service.updateEmailTemplate(toUpdate);
		EmailTemplate result = service.getEmailTemplateByName(INTERVIEWER_REMINDER_FIRST);
		areEqual(toUpdate, result);
		
		verify(daoMock);
	}

}
