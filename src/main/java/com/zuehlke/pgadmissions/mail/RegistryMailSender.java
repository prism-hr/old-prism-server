package com.zuehlke.pgadmissions.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.PdfDocumentBuilder;

@Component
public class RegistryMailSender extends MailSender {

	private final RegistryUserDAO registryUserDAO;
	private final UserService userService;

	RegistryMailSender() {
		this(null, null, null, null, null);
	}

	@Autowired
	public RegistryMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, RegistryUserDAO registryUserDAO,
			UserService userService, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
		this.registryUserDAO = registryUserDAO;
		this.userService = userService;
	}

	public void sendApplicationToRegistryContacts(ApplicationForm applicationForm) throws MalformedURLException, DocumentException, IOException {
		List<RegistryUser> registryContacts = registryUserDAO.getAllRegistryUsers();
		InternetAddress[] toAddresses = new InternetAddress[registryContacts.size()];
		int counter = 0;
		for (RegistryUser registryUser : registryContacts) {
			toAddresses[counter++] = new InternetAddress(registryUser.getEmail(), registryUser.getFirstname() + " " + registryUser.getLastname());
		}
		RegisteredUser currentUser = userService.getCurrentUser();
		InternetAddress ccAdminAddres = createAddress(currentUser);
		
		String subject = resolveMessage("validation.request.registry.contacts", applicationForm);
		String templatename = "private/staff/admin/mail/registry_validation_request.ftl";

		MimeMessagePreparator mimeMessagePreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddresses,
				new InternetAddress[] { ccAdminAddres }, subject, templatename, createModel(applicationForm, currentUser), ccAdminAddres);
		javaMailSender.send(mimeMessagePreparator);
		System.err.println("send!");
	}

	public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("sender", currentAdminUser);
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;

	}

	/*/public File getAttachment(ApplicationForm applicationForm) throws DocumentException, MalformedURLException, IOException {

		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		File file = new File("Application " + applicationForm.getId() + ".pdf");
		file.deleteOnExit();
		FileOutputStream outputStream = null;
		PdfWriter writer = null;
		try {
			outputStream = new FileOutputStream(file);
			writer = PdfWriter.getInstance(document, outputStream);
			PdfDocumentBuilder builder = newPdfDocumentWriter(writer);
			document.open();
			builder.buildDocument(applicationForm, document);
		} finally {
			try {
				document.close();
			} catch (Exception ignore) {				
			}
			try {
				writer.flush();
			} catch (Exception ignore) {
			}
			try {
				writer.close();
			} catch (Exception ignore) {
			}
			try {
				outputStream.flush();
			} catch (Exception ignore) {
			}
			try {
				outputStream.close();
			} catch (Exception ignore) {
			}

		}
		System.err.println(file.getAbsolutePath());
		return file;

	}

	 PdfDocumentBuilder newPdfDocumentWriter(PdfWriter writer) {
		return new PdfDocumentBuilder(writer);
	}*/

}
