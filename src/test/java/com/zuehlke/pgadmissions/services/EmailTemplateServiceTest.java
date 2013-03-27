package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER_FIRST;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.EmailTemplateDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;

public class EmailTemplateServiceTest {

	private EmailTemplateDAO daoMock;
	private EmailTemplateService service;

	@Before
	public void setup() {
		daoMock = createMock(EmailTemplateDAO.class);
		service = new EmailTemplateService(daoMock);
	}

	@Test
	public void shouldReturnEmailTemplates() {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().id(1L).content("Template content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		EmailTemplate template2 = new EmailTemplateBuilder().id(2L).content("Template content 2")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).build();
		expect(daoMock.getByName(INTERVIEWER_REMINDER_FIRST)).andReturn(asList(template1, template2));
		replay(daoMock);
		
		List<EmailTemplate> result = service.getEmailTemplates(INTERVIEWER_REMINDER_FIRST);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		areEqual(template1, result.get(0));
		areEqual(template2, result.get(1));
		
		verify(daoMock);
	}

	private void areEqual(EmailTemplate expected, EmailTemplate actual) {
		assertNotNull(actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getContent(), actual.getContent());
		assertEquals(expected.getVersion(), actual.getVersion());
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
	public void shouldReturnEmailTemplateVersions() {
		DateTime version1 = new DateTime(2013, 4, 25, 00, 00);
		DateTime version2 = new DateTime(2013, 10, 27, 00, 00);
		Map<Long, Date> mapReturnedByMock = new HashMap<Long, Date>();
		mapReturnedByMock.put(1L, null);
		mapReturnedByMock.put(2L, version1.toDate());
		mapReturnedByMock.put(3L, version2.toDate());
		expect(daoMock.getVersionsByName(INTERVIEWER_REMINDER_FIRST)).andReturn(mapReturnedByMock);
		replay(daoMock);
		
		Map<Long, String> result = service.getEmailTemplateVersions(INTERVIEWER_REMINDER_FIRST);
		
		assertEquals("original template", result.get(1L));
		assertEquals("2013/4/25 - 00:00:00", result.get(2L));
		assertEquals("2013/10/27 - 00:00:00", result.get(3L));
		verify(daoMock);
	}
	
	@Test
	public void shouldActivateEmailTemplateAndDisableTheOldOne() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate oldTtemplate = new EmailTemplateBuilder().id(1L).content("Template1 content")
				.name(INTERVIEWER_REMINDER_FIRST).active(true).build();
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		expect(daoMock.getActiveByName(INTERVIEWER_REMINDER_FIRST)).andReturn(oldTtemplate);
		daoMock.save(oldTtemplate);
		daoMock.save(toActivate);
		replay(daoMock);
		
		service.activateEmailTemplate(toActivate);
		
		verify(daoMock);
	}
	
	@Test
	public void shouldNotActivateEmailIfAlreadyActive() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).active(true).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		replay(daoMock);
		
		service.activateEmailTemplate(toActivate);
		
		verify(daoMock);
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotActivateEmailTemplateBecauseOfNoTemplateFoundException() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).active(true).build();
		expect(daoMock.getById(2L)).andReturn(null);
		replay(daoMock);
		
		service.activateEmailTemplate(toActivate);
		
		verify(daoMock);
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotActivateEmailTemplateBecauseOfNoActiveTemplateFound() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		expect(daoMock.getActiveByName(INTERVIEWER_REMINDER_FIRST)).andReturn(null);
		replay(daoMock);
		
		service.activateEmailTemplate(toActivate);
		
		verify(daoMock);
	}
	
	@Test
	public void shouldReturnDefaultTemplate() {
		EmailTemplate defaultTemplate = new EmailTemplateBuilder().id(1L).content("Template1 content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		expect(daoMock.getDefaultByName(INTERVIEWER_REMINDER_FIRST)).andReturn(defaultTemplate);
		replay(daoMock);
		
		service.getDefaultEmailTemplate(INTERVIEWER_REMINDER_FIRST);
		
		verify(daoMock);
	}
	
	@Test
	public void shouldDeleteTemplate() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		daoMock.remove(toDelete);
		replay(daoMock);
		
		service.deleteTemplateVersion(toDelete.getId());
		
		verify(daoMock);
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotDeletActiveTemplate() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).active(true).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		daoMock.remove(toDelete);
		replay(daoMock);
		
		service.deleteTemplateVersion(toDelete.getId());
		
		verify(daoMock);
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotDeletDefaultTemplate() throws Exception {
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		daoMock.remove(toDelete);
		replay(daoMock);
		
		service.deleteTemplateVersion(toDelete.getId());
		
		verify(daoMock);
	}
	
	
	@Test
	public void shouldSaveNewUpdatedEmailTemplate() {
		EmailTemplate updatedTemplate = new EmailTemplateBuilder().id(1L).content("Changed Content!!")
				.name(INTERVIEWER_REMINDER_FIRST).build();
		daoMock.save(updatedTemplate);
		replay(daoMock);
		
		service.saveNewEmailTemplate(updatedTemplate);

		assertNotNull(updatedTemplate.getVersion());
		verify(daoMock);
	}
	
	@Test
	public void shouldProduceTemplatePreview() {
		String ftlCode = "href=${host}/pgadmissions/register?activationCode=${approver.activationCode}";
		String expected = "href=host/pgadmissions/register?activationCode=approver.activationCode";
		assertEquals(expected , service.processTemplateContent(ftlCode));
		ftlCode = "href=${host}/pgadmissions/register?activationCode=${approver.activationCode?html}";
		assertEquals(expected , service.processTemplateContent(ftlCode));
	}

}
