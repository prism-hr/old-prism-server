package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSender extends StateChangeMailSender {


	
	private final ApplicationsService applicationsService;

	public ApplicantMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, ApplicationsService applicationsService) {
		super(mimeMessagePreparatorFactory, mailSender);
		this.applicationsService = applicationsService;
	}

	Map<String, Object> createModel(ApplicationForm form) {

		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);

		model.put("application", form);

		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());

		if (ApplicationFormStatus.REJECTED.equals(form.getStatus())) {
			model.put("reason", form.getRejection().getRejectionReason());
			if(form.getRejection().isIncludeProspectusLink()){
				model.put("prospectusLink", Environment.getInstance().getUCLProspectusLink());
			}
			
			model.put("stage", applicationsService.getStageComingFrom(form));
		}
		return model;
	}
	
	@Override
	public  void sendMailsForApplication(ApplicationForm form, String message, String templatename) throws UnsupportedEncodingException {
		
		InternetAddress toAddress = new InternetAddress(form.getApplicant().getEmail(), form.getApplicant().getFirstName() + " "
				+ form.getApplicant().getLastName());
	
		
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + form.getId() + " for "
				+ form.getProgram().getTitle() + " " + message, templatename, createModel(form)));
	
	}
}
