package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.expect;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.EmailTemplateDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmailTemplateServiceTest {
    
    @Mock  
    @InjectIntoByType
	private EmailTemplateDAO daoMock;
    
    @TestedObject
	private EmailTemplateService service;

	@Test
	public void shouldReturnEmailTemplates() {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate template1 = new EmailTemplateBuilder().id(1L).content("Template content")
				.name(REFEREE_NOTIFICATION).build();
		EmailTemplate template2 = new EmailTemplateBuilder().id(2L).content("Template content 2")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).build();
		expect(daoMock.getByName(REFEREE_NOTIFICATION)).andReturn(asList(template1, template2));
		
		
		replay();
		List<EmailTemplate> result = service.getEmailTemplates(REFEREE_NOTIFICATION);
		verify();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		areEqual(template1, result.get(0));
		areEqual(template2, result.get(1));
		
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
				.name(REFEREE_NOTIFICATION).build();
		EmailTemplate template2 = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(EmailTemplateName.NEW_PASSWORD_CONFIRMATION).build();
		expect(daoMock.getAll()).andReturn(asList(template1, template2));

		replay();
		List<EmailTemplate> result = service.getAllEmailTemplates();
		verify();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		areEqual(template1, result.get(0));
		areEqual(template2, result.get(1));
	}
	
	@Test
	public void shouldReturnEmailTemplateVersions() {
		DateTime version1 = new DateTime(2013, 4, 25, 00, 00);
		DateTime version2 = new DateTime(2013, 10, 27, 00, 00);
		Map<Long, Date> mapReturnedByMock = new HashMap<Long, Date>();
		mapReturnedByMock.put(1L, null);
		mapReturnedByMock.put(2L, version1.toDate());
		mapReturnedByMock.put(3L, version2.toDate());
		expect(daoMock.getVersionsByName(REFEREE_NOTIFICATION)).andReturn(mapReturnedByMock);
		
		replay();
		Map<Long, String> result = service.getEmailTemplateVersions(REFEREE_NOTIFICATION);
		verify();
		
		assertEquals("original template", result.get(1L));
		assertEquals("25 Apr 2013 - 00:00:00", result.get(2L));
		assertEquals("27 Oct 2013 - 00:00:00", result.get(3L));
	}
	
	@Test
	public void shouldActivateEmailTemplateAndDisableTheOldOne() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate oldTtemplate = new EmailTemplateBuilder().id(1L).content("Template1 content")
				.name(REFEREE_NOTIFICATION).active(true).build();
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		expect(daoMock.getActiveByName(REFEREE_NOTIFICATION)).andReturn(oldTtemplate);
		daoMock.save(oldTtemplate);
		daoMock.save(toActivate);
		
		replay();
		service.activateEmailTemplate(toActivate);
		verify();
	}
	
	@Test
	public void shouldNotActivateEmailIfAlreadyActive() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).active(true).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		
		replay();
		service.activateEmailTemplate(toActivate);
		verify();
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotActivateEmailTemplateBecauseOfNoTemplateFoundException() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).active(true).build();
		expect(daoMock.getById(2L)).andReturn(null);
		
		replay();
		service.activateEmailTemplate(toActivate);
		verify();
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotActivateEmailTemplateBecauseOfNoActiveTemplateFound() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toActivate = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toActivate);
		expect(daoMock.getActiveByName(REFEREE_NOTIFICATION)).andReturn(null);
		
		replay();
		service.activateEmailTemplate(toActivate);
		verify();
	}
	
	@Test
	public void shouldReturnDefaultTemplate() {
		EmailTemplate defaultTemplate = new EmailTemplateBuilder().id(1L).content("Template1 content")
				.name(REFEREE_NOTIFICATION).build();
		expect(daoMock.getDefaultByName(REFEREE_NOTIFICATION)).andReturn(defaultTemplate);
		
		replay();
		service.getDefaultEmailTemplate(REFEREE_NOTIFICATION);
		verify();
	}
	
	@Test
	public void shouldDeleteTemplate() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		daoMock.remove(toDelete);
		
		replay();
		service.deleteTemplateVersion(toDelete.getId());
		verify();
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotDeletActiveTemplate() throws Exception {
		DateTime version = new DateTime(2013, 4, 25, 00, 00);
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).active(true).version(version.toDate()).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		
		replay();
		service.deleteTemplateVersion(toDelete.getId());
		verify();
	}
	
	@Test(expected = EmailTemplateException.class)
	public void shouldNotDeletDefaultTemplate() throws Exception {
		EmailTemplate toDelete = new EmailTemplateBuilder().id(2L).content("Template2 content")
				.name(REFEREE_NOTIFICATION).build();
		expect(daoMock.getById(2L)).andReturn(toDelete);
		
		replay();
		service.deleteTemplateVersion(toDelete.getId());
		verify();
	}
	
	
	@Test
	public void shouldSaveNewUpdatedEmailTemplate() {
		EmailTemplate updatedTemplate = new EmailTemplateBuilder().id(1L).content("Changed Content!!")
				.name(REFEREE_NOTIFICATION).build();
		daoMock.save(updatedTemplate);
		
		replay();
		service.saveNewEmailTemplate(updatedTemplate);
		verify();

		assertNotNull(updatedTemplate.getVersion());
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
	
