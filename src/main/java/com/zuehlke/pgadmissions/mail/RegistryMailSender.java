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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSourceFactory;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

@Component
public class RegistryMailSender extends MailSender {
	
    private final PdfDocumentBuilder pdfDocumentBuilder;

	private final PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory;

	public RegistryMailSender() {
		this(null, null, null, null,  null);
	}

	@Autowired
    public RegistryMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,
            MessageSource msgSource, PdfDocumentBuilder pdfDocumentBuilder,
            PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactory) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
		this.pdfDocumentBuilder = pdfDocumentBuilder;
		this.pdfAttachmentInputSourceFactory = pdfAttachmentInputSourceFactory;
	}

    public void sendApplicationToRegistryContacts(ApplicationForm applicationForm, List<Person> registryContacts)
            throws MalformedURLException, DocumentException, IOException {
		InternetAddress[] toAddresses = new InternetAddress[registryContacts.size()];
		int counter = 0;
		for (Person registryUser : registryContacts) {
			toAddresses[counter++] = new InternetAddress(registryUser.getEmail(), registryUser.getFirstname() + " " + registryUser.getLastname());
		}
		RegisteredUser currentUser = applicationForm.getAdminRequestedRegistry();
		InternetAddress ccAdminAddres = createAddress(currentUser);

		String subject = resolveMessage("validation.request.registry.contacts", applicationForm);
		String templatename = "private/staff/admin/mail/registry_validation_request.ftl";

		MimeMessagePreparator mimeMessagePreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(
				toAddresses,
				new InternetAddress[] { ccAdminAddres },
				subject,
				templatename,
				createModel(applicationForm, currentUser, registryContacts),
				ccAdminAddres,
				pdfAttachmentInputSourceFactory.getAttachmentDataSource(applicationForm.getApplicationNumber() + ".pdf",
						pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), applicationForm)));
		javaMailSender.send(mimeMessagePreparator);

	}

	public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser, List<Person> registryContacts) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("sender", currentAdminUser);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("recipients", createRecipientString(registryContacts));
		model.put("admissionsValidationServiceLevel", Environment.getInstance().getAdmissionsValidationServiceLevel());
		return model;
	}

	private String createRecipientString(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Person person : registryContacts) {
			if( !first) {
				sb.append(", ");
			}
			sb.append(person.getFirstname());
			first = false;
		}
		return sb.toString();
	}
}
