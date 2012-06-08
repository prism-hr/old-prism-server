package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

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

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSourceFactory;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

public class RegistryMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private RegistryMailSender registryMailSender;
	private PersonDAO registryUserDAOMock;
	private UserService userServiceMock;
	private MessageSource msgSourceMock;
	private PdfDocumentBuilder pdfDocumentBuilderMock;
	private PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactoryMock;

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

	@Test
	public void shoulSendMailToRegistryContacts() throws MalformedURLException, DocumentException, IOException {
		final Map<String, Object> model = new HashMap<String, Object>();

		registryMailSender = new RegistryMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, registryUserDAOMock, userServiceMock, msgSourceMock,pdfDocumentBuilderMock, pdfAttachmentInputSourceFactoryMock) {

			@Override
			public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser) {
				return model;
			}

		};

		Person registryUser1 = new PersonBuilder().id(2).firstname("Bob").lastname("Jones").email("jones@test.com").toPerson();
		Person registryUser2 = new PersonBuilder().id(3).firstname("Karla").lastname("Peters").email("peters@test.com").toPerson();
		EasyMock.expect(registryUserDAOMock.getAllPersons()).andReturn(Arrays.asList(registryUser1, registryUser2));
		EasyMock.replay(registryUserDAOMock);

		RegisteredUser currentAdminUser = new RegisteredUserBuilder().id(1).firstName("Hanna").lastName("Hobnop").email("hobnob@test.com").toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAdminUser);
		EasyMock.replay(userServiceMock);

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().title("program name").toProgram()).applicationNumber("application number")
				.toApplicationForm();

		InternetAddress toAddress1 = new InternetAddress("jones@test.com", "Bob Jones");
		InternetAddress toAddress2 = new InternetAddress("peters@test.com", "Karla Peters");
		InternetAddress toAddress3 = new InternetAddress("hobnob@test.com", "Hanna Hobnop");
		
		byte[] pdf = "pdf".getBytes();
		EasyMock.expect(pdfDocumentBuilderMock.buildPdf(applicationForm)).andReturn(pdf);		
		PdfAttachmentInputSource attachmentInputSource = EasyMock.createMock(PdfAttachmentInputSource.class);
		EasyMock.expect(pdfAttachmentInputSourceFactoryMock.getAttachmentDataSource("application number.pdf", pdf)).andReturn(attachmentInputSource);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("validation.request.registry.contacts"),//
				EasyMock.aryEq(new Object[] { "application number", "program name" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.aryEq(new InternetAddress[] { toAddress1, toAddress2 }),
						EasyMock.aryEq(new InternetAddress[] { toAddress3 }), EasyMock.eq("resolved subject"),
						EasyMock.eq("private/staff/admin/mail/registry_validation_request.ftl"), EasyMock.eq(model), EasyMock.eq(toAddress3),
						EasyMock.eq(attachmentInputSource))).andReturn(preparatorMock);
		
		javaMailSenderMock.send(preparatorMock);
	
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock,  pdfDocumentBuilderMock,pdfAttachmentInputSourceFactoryMock , msgSourceMock);

		registryMailSender.sendApplicationToRegistryContacts(applicationForm);

		EasyMock.verify(mimeMessagePreparatorFactoryMock, javaMailSenderMock,  pdfDocumentBuilderMock,pdfAttachmentInputSourceFactoryMock , msgSourceMock);

	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		registryUserDAOMock = EasyMock.createMock(PersonDAO.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		pdfDocumentBuilderMock = EasyMock.createMock(PdfDocumentBuilder.class);
		pdfAttachmentInputSourceFactoryMock = EasyMock.createMock(PdfAttachmentInputSourceFactory.class);
		registryMailSender = new RegistryMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, registryUserDAOMock, userServiceMock, msgSourceMock,pdfDocumentBuilderMock, pdfAttachmentInputSourceFactoryMock);
	}
}
