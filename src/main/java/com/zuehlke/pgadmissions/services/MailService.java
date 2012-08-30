package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.Environment;

public class MailService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final ApplicationsService applicationsService;

	private final Logger log = Logger.getLogger(MailService.class);
	private final MessageSource msgSource;

	public MailService() {
		this(null, null, null, null);
	}

	@Autowired
	public MailService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender,//
			ApplicationsService applicationsService, MessageSource msgSource) {

		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationsService = applicationsService;
		this.msgSource = msgSource;
	}

	@Transactional
	private void createOrUpdateUpdateNotificationRecord(ApplicationForm form) {
		NotificationRecord notificationRecord = form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION);
		if (notificationRecord == null) {
			notificationRecord = new NotificationRecord();
			notificationRecord.setNotificationType(NotificationType.UPDATED_NOTIFICATION);
			form.getNotificationRecords().add(notificationRecord);
		}
		notificationRecord.setDate(new Date());
		applicationsService.save(form);
	}

	@Transactional
	public void sendApplicationUpdatedMailToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();

		String mailSubject = resolveMessage("application.update", form.getApplicationNumber(), form.getProgram().getTitle(),
				form.getApplicant().getFirstName(), form.getApplicant().getLastName());
		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = createModel(form);
				model.put("admin", admin);
				InternetAddress toAddress = createAddress(admin);

				delegateToMailSender(toAddress, null, mailSubject, "private/staff/admin/mail/application_updated_confirmation.ftl", model);
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("error while sending email", e);
			}
		}
		if(ApplicationFormStatus.INTERVIEW == form.getStatus()){
			if(form.getLatestInterview() != null){
				for (Interviewer interviewer : form.getLatestInterview().getInterviewers()) {
					try {
						Map<String, Object> model = createModel(form);
						model.put("admin", interviewer.getUser());
						InternetAddress toAddress = createAddress(interviewer.getUser());

						delegateToMailSender(toAddress, null, mailSubject, "private/staff/admin/mail/application_updated_confirmation.ftl", model);
					} catch (Throwable e) {
						e.printStackTrace();
						log.warn("error while sending email", e);
					}
				}
			}
		}
		if(ApplicationFormStatus.APPROVAL == form.getStatus()){
			for (RegisteredUser approver : form.getProgram().getApprovers()) {
				try {
					Map<String, Object> model = createModel(form);
					model.put("admin", approver);
					InternetAddress toAddress = createAddress(approver);

					delegateToMailSender(toAddress, null, mailSubject, "private/staff/admin/mail/application_updated_confirmation.ftl", model);
				} catch (Throwable e) {
					e.printStackTrace();
					log.warn("error while sending email", e);
				}
			}
			
		}
		createOrUpdateUpdateNotificationRecord(form);
	}

	@Transactional
	public void sendWithdrawMailToReferees(List<Referee> referees) {
		for (Referee referee : referees) {
			RegisteredUser user = referee.getUser();
			if(user != null){
				internalSendWithdraw(user, referee.getApplication());
			}
		}
	}

	@Transactional
	public void sendWithdrawToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		for (RegisteredUser admin : administrators) {
			internalSendWithdraw(admin, form);
		}
		if (form.getApplicationAdministrator() != null) {
			internalSendWithdraw(form.getApplicationAdministrator(), form);
		}
	}

	@Transactional
	public void sendWithdrawToReviewers(ApplicationForm form) {
		List<Reviewer> reviewers = form.getLatestReviewRound().getReviewers();
		for (Reviewer reviewer : reviewers) {
			if (reviewer.getReview() == null) {
				internalSendWithdraw(reviewer.getUser(), form);
			}
		}
	}
	
	public void sendWithdrawToInterviewers(ApplicationForm form) {
		List<Interviewer> interviewers = form.getLatestInterview().getInterviewers();
		for (Interviewer interviewer : interviewers) {
			if (interviewer.getInterviewComment() == null) {
				internalSendWithdraw(interviewer.getUser(), form);
			}
		}
		
	}
	
	public void sendWithdrawToSupervisors(ApplicationForm form) {
		for (Supervisor supervisor : form.getLatestApprovalRound().getSupervisors()) {			
				internalSendWithdraw(supervisor.getUser(), form);
			
		}
		
	}

	private void internalSendWithdraw(RegisteredUser recipient, ApplicationForm application) {
		try {
			Map<String, Object> model = createModel(application);
			model.put("user", recipient);

			InternetAddress toAddress = createAddress(recipient);
			String mailSubject = resolveMessage("application.withdrawal", application.getApplicationNumber(), application.getProgram().getTitle(), application
					.getApplicant().getFirstName(), application.getApplicant().getLastName());

			delegateToMailSender(toAddress, null, mailSubject, "private/staff/mail/application_withdrawn_notification.ftl", model);
		} catch (Throwable e) {

			log.warn("error while sending email", e);
		}
	}

	protected Map<String, Object> createModel(ApplicationForm application) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", application);
		model.put("applicant", application.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	private String resolveMessage(String code, Object... args) {
		return msgSource.getMessage(code, args, null);
	}

	private InternetAddress createAddress(RegisteredUser user) {
		try {
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			throw new RuntimeException(uee);
		}
	}

	private void delegateToMailSender(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, String template, Map<String, Object> model) {
		MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, template, model, null);
		mailsender.send(msgPreparator);
	}

	
	
}
