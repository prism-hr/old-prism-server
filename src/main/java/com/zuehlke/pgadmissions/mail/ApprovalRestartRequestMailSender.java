package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPROVAL_RESTART_REQUEST_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.RESTART_APPROVAL_REQUEST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApprovalRestartRequestMailSender extends MailSender {

    public ApprovalRestartRequestMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory,
            JavaMailSender mailSender, MessageSource messageSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, messageSource, emailTemplateService);
	}

	protected Map<String, Object> createModel(ApplicationForm application) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", application);
		model.put("applicant", application.getApplicant());
		model.put("requester", application.getApproverRequestedRestart());
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("comment", application.getLatestsRequestRestartComment());
		return model;

	}

	public void sendRequestRestartApproval(ApplicationForm form) {
		Program program = form.getProgram();

		List<RegisteredUser> adminRecipients = program.getAdministrators();
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();

		String subject = resolveMessage("application.request.restart.approval", form.getApplicationNumber(), program.getTitle());
		EmailTemplate template = getDefaultEmailtemplate(RESTART_APPROVAL_REQUEST);
		Map<String, Object> model = createModel(form);

		if (applicationAdmin == null) { // send email to all program administrators
			for (RegisteredUser admin : adminRecipients) {
				InternetAddress toAddress = createAddress(admin);
				model.put("admin", admin);
				MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, null, subject, RESTART_APPROVAL_REQUEST, template.getContent(), model, null);
				javaMailSender.send(msgPreparator);
			}
		} else { // send one email to application admin, CC to program admins
		    InternetAddress toAddress = createAddress(applicationAdmin);
		    ArrayList<InternetAddress> ccAddresses = new ArrayList<InternetAddress>();
            for (RegisteredUser admin : adminRecipients) {
                InternetAddress ccAddress = createAddress(admin);
                if (!ccAddress.equals(toAddress)) {
                    ccAddresses.add(ccAddress);
                }
            }
			model.put("admin", applicationAdmin);
			MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses.toArray(new InternetAddress[]{}), subject, RESTART_APPROVAL_REQUEST, template.getContent(), model, null);
			javaMailSender.send(msgPreparator);
		}
	}

	public void sendRequestRestartApprovalReminder(ApplicationForm form) {
		Program program = form.getProgram();

		List<RegisteredUser> adminRecipients = program.getAdministrators();
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();

		String subject = resolveMessage("application.request.restart.approval.reminder", form.getApplicationNumber(), program.getTitle());
		EmailTemplate template = getDefaultEmailtemplate(APPROVAL_RESTART_REQUEST_REMINDER);
		Map<String, Object> model = createModel(form);

		if (applicationAdmin == null) { // send email to all program administrators
			for (RegisteredUser admin : adminRecipients) {
				InternetAddress toAddress = createAddress(admin);
				model.put("admin", admin);
				MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, null, subject, APPROVAL_RESTART_REQUEST_REMINDER, template.getContent(), model, null);
				javaMailSender.send(msgPreparator);
			}
		} else { // send one email to application admin, CC to program admins
		    InternetAddress toAddress = createAddress(applicationAdmin);
		    ArrayList<InternetAddress> ccAddresses = new ArrayList<InternetAddress>();
		    for (RegisteredUser admin : adminRecipients) {
				InternetAddress ccAddress = createAddress(admin);
		        if (!ccAddress.equals(toAddress)) {
		            ccAddresses.add(ccAddress);
		        }
			}
			model.put("admin", applicationAdmin);
			MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses.toArray(new InternetAddress[]{}), subject, APPROVAL_RESTART_REQUEST_REMINDER, template.getContent(), model, null);
			javaMailSender.send(msgPreparator);
		}
		
	}
}
