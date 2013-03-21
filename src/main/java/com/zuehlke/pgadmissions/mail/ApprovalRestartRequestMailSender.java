package com.zuehlke.pgadmissions.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApprovalRestartRequestMailSender extends MailSender {

    public ApprovalRestartRequestMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory,
            JavaMailSender mailSender, MessageSource messageSource) {
		super(mimeMessagePreparatorFactory, mailSender, messageSource);
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
		String template = "private/staff/admin/mail/restart_approval_request.ftl";
		Map<String, Object> model = createModel(form);

		if (applicationAdmin == null) { // send email to all program administrators
			for (RegisteredUser admin : adminRecipients) {
				InternetAddress toAddress = createAddress(admin);
				model.put("admin", admin);
				MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, null, subject, template, model, null);
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
			MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses.toArray(new InternetAddress[]{}), subject, template, model, null);
			javaMailSender.send(msgPreparator);
		}
	}

	public void sendRequestRestartApprovalReminder(ApplicationForm form) {
		Program program = form.getProgram();

		List<RegisteredUser> adminRecipients = program.getAdministrators();
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();

		String subject = resolveMessage("application.request.restart.approval.reminder", form.getApplicationNumber(), program.getTitle());
		String template = "private/staff/admin/mail/approval_restart_request_reminder.ftl";
		Map<String, Object> model = createModel(form);

		if (applicationAdmin == null) { // send email to all program administrators
			for (RegisteredUser admin : adminRecipients) {
				InternetAddress toAddress = createAddress(admin);
				model.put("admin", admin);
				MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, null, subject, template, model, null);
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
			MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses.toArray(new InternetAddress[]{}), subject, template, model, null);
			javaMailSender.send(msgPreparator);
		}
		
	}
}
