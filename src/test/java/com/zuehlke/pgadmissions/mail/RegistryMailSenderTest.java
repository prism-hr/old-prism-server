package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegistryUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.PdfDocumentBuilder;

public class RegistryMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private RegistryMailSender registryMailSender;
	private RegistryUserDAO registryUserDAOMock;
	private UserService userServiceMock;
	private MessageSource msgSourceMock;

	@Test
	public void shouldReturnModelWithApplicationForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser currentAdminUser = new RegisteredUserBuilder().id(1).firstName("Hanna").lastName("Hobnop").email("hobnob@test.com").toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAdminUser);
		EasyMock.replay(userServiceMock);
		Map<String, Object> model = registryMailSender.createModel(applicationForm, currentAdminUser);
		assertEquals(applicationForm, model.get("application"));
		assertEquals(currentAdminUser, model.get("sender"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
	}
	
/*	@Test
	public void shoulSendMailToRegistryContacts() throws MalformedURLException, DocumentException, IOException {
		final Map<String, Object> model  = new HashMap<String, Object>();
		final PdfDocumentBuilder pdfDocumentBuilderMock = EasyMock.createMock(PdfDocumentBuilder.class);
		registryMailSender = new RegistryMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, registryUserDAOMock, userServiceMock, msgSourceMock){

			@Override
			public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser) {
				return model;
			}

			@Override
			PdfDocumentBuilder newPdfDocumentWriter(PdfWriter writer) {
				return pdfDocumentBuilderMock;
			}

				
		};
		
		RegistryUser registryUser1 = new RegistryUserBuilder().id(2).firstname("Bob").lastname("Jones").email("jones@test.com").toRegistryUser();
		RegistryUser registryUser2 = new RegistryUserBuilder().id(3).firstname("Karla").lastname("Peters").email("peters@test.com").toRegistryUser();
		EasyMock.expect(registryUserDAOMock.getAllRegistryUsers()).andReturn(Arrays.asList(registryUser1, registryUser2));
		EasyMock.replay(registryUserDAOMock);

		RegisteredUser currentAdminUser = new RegisteredUserBuilder().id(1).firstName("Hanna").lastName("Hobnop").email("hobnob@test.com").toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAdminUser);
		EasyMock.replay(userServiceMock);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().title("program name").toProgram()).toApplicationForm();

		InternetAddress toAddress1 = new InternetAddress("jones@test.com", "Bob Jones");
		InternetAddress toAddress2 = new InternetAddress("peters@test.com", "Karla Peters");
		InternetAddress toAddress3 = new InternetAddress("hobnob@test.com", "Hanna Hobnop");
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("validation.request.registry.contacts"),// 
				EasyMock.aryEq(new Object[] { 1, "program name" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.aryEq(new InternetAddress[] { toAddress1, toAddress2 }), EasyMock.aryEq(new InternetAddress[]{toAddress3}),
						EasyMock.eq("resolved subject"), EasyMock.eq("private/staff/admin/mail/registry_validation_request.ftl"), EasyMock.eq(model), EasyMock.eq(toAddress3), EasyMock.isA(File.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		
		pdfDocumentBuilderMock.buildDocument(EasyMock.eq(applicationForm), EasyMock.isA(Document.class));
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, pdfDocumentBuilderMock, msgSourceMock);
		
		registryMailSender.sendApplicationToRegistryContacts(applicationForm);
		
		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock, pdfDocumentBuilderMock, msgSourceMock);

	}
	*/

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		registryUserDAOMock = EasyMock.createMock(RegistryUserDAO.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		registryMailSender = new RegistryMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, registryUserDAOMock, userServiceMock, msgSourceMock);
	}
}
