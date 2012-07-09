package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSender extends StateChangeMailSender {

	private final ApplicationsService applicationsService;
	private final ConfigurationService personService;

	public ApplicantMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,// 
			ApplicationsService applicationsService, MessageSource msgSource, ConfigurationService personService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
		this.applicationsService = applicationsService;
		this.personService = personService;
	}

	Map<String, Object> createModel(ApplicationForm form) {

		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);

		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", personService.getAllRegistryUsers());
		
		model.put("host", Environment.getInstance().getApplicationHostName());

		if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
			model.put("reason", form.getRejection().getRejectionReason());
			if (form.getRejection().isIncludeProspectusLink()) {
				model.put("prospectusLink", Environment.getInstance().getUCLProspectusLink());
			}

		}
		model.put("previousStage",form.getOutcomeOfStage());
		return model;
	}

	@Override
	public void sendMailsForApplication(ApplicationForm form, String messageCode, String templatename, NotificationType notificationType) {
		InternetAddress toAddress = createAddress(form.getApplicant());
		String subject = resolveSubject(form, messageCode);
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, templatename, createModel(form), null));
	}

	private String resolveSubject(ApplicationForm form, String messageCode) {
		return resolveMessage(messageCode, form, form.getOutcomeOfStage());
	}
}
