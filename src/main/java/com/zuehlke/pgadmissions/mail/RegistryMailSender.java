package com.zuehlke.pgadmissions.mail;

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

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSourceFactory;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

@Component
public class RegistryMailSender extends MailSender {

	private final RegistryUserDAO registryUserDAO;
	private final UserService userService;
	private final PdfDocumentBuilder pdfDocumentBuilder;
	private final PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory;

	RegistryMailSender() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public RegistryMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, RegistryUserDAO registryUserDAO,
			UserService userService, MessageSource msgSource, PdfDocumentBuilder pdfDocumentBuilder,
			PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
		this.registryUserDAO = registryUserDAO;
		this.userService = userService;
		this.pdfDocumentBuilder = pdfDocumentBuilder;
		this.pdfAttachmentInputSourceFactory = pdfAttachmentInputSourceFactory;
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

		MimeMessagePreparator mimeMessagePreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(
				toAddresses,
				new InternetAddress[] { ccAdminAddres },
				subject,
				templatename,
				createModel(applicationForm, currentUser),
				ccAdminAddres,
				pdfAttachmentInputSourceFactory.getAttachmentDataSource(applicationForm.getApplicationNumber() + ".pdf",
						pdfDocumentBuilder.buildPdf(applicationForm)));
		javaMailSender.send(mimeMessagePreparator);

	}

	public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("sender", currentAdminUser);
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;

	}

}
