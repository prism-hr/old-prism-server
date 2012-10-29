package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class RefereeService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(RefereeService.class);
	private final RefereeDAO refereeDAO;
	private final UserService userService;
	private final RoleDAO roleDAO;
	private final MessageSource messageSource;
	private final EventFactory eventFactory;
	private final ApplicationFormDAO applicationFormDAO;
	private final EncryptionUtils encryptionUtils;

	RefereeService() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public RefereeService(RefereeDAO refereeDAO, EncryptionUtils encryptionUtils, MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, UserService userService,
			RoleDAO roleDAO, MessageSource messageSource, EventFactory eventFactory, ApplicationFormDAO applicationFormDAO) {
		this.refereeDAO = refereeDAO;
		this.encryptionUtils = encryptionUtils;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.userService = userService;
		this.roleDAO = roleDAO;
		this.messageSource = messageSource;
		this.eventFactory = eventFactory;
		this.applicationFormDAO = applicationFormDAO;

	}

	@Transactional
	public Referee getRefereeById(Integer id) {
		return refereeDAO.getRefereeById(id);
	}

	@Transactional
	public void save(Referee referee) {
		refereeDAO.save(referee);
	}
	
	@Transactional
	public List<Referee> getRefereesWhoHaveNotProvidedReference(ApplicationForm form) {
		return refereeDAO.getRefereesWhoDidntProvideReferenceYet(form);
	}

	@Transactional
	public void saveReferenceAndSendMailNotifications(Referee referee) {
//		save(referee);		
		addReferenceEventToApplication(referee);
		sendMailToApplicant(referee);
		sendMailToAdministrators(referee);

	}

	private void sendMailToAdministrators(Referee referee) {
		ApplicationForm form = referee.getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String subject = resolveMessage("reference.provided.admin", form);

		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("referee", referee);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());

				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,
						"private/staff/admin/mail/reference_submit_confirmation.ftl", model, null));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
		}

	}

	private void sendMailToApplicant(Referee referee) {
		try {
			ApplicationForm form = referee.getApplication();
			RegisteredUser applicant = form.getApplicant();
			List<RegisteredUser> administrators = form.getProgram().getAdministrators();
			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("adminsEmails", adminsEmails);
			model.put("referee", referee);
			model.put("application", form);
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = new InternetAddress(applicant.getEmail(), applicant.getFirstName() + " " + applicant.getLastName());
			
			String subject = resolveMessage("reference.provided.applicant", form);
			
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
					"private/pgStudents/mail/reference_respond_confirmation.ftl", model, null));
		  } catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

	private String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (RegisteredUser admin : administrators) {
			adminsMails.append(admin.getEmail());
			adminsMails.append(", ");
		}
		String result = adminsMails.toString();
		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public RegisteredUser getRefereeIfAlreadyRegistered(Referee referee) {
		return userService.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
	}

	@Transactional
	public void processRefereesRoles(List<Referee> referees) {
		for (Referee referee : referees) {
			processRefereeAndGetAsUser(referee);
		}

	}

	public RegisteredUser processRefereeAndGetAsUser(Referee referee) {
		RegisteredUser user = userService.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
		Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
		if (userExists(user) && !isUserReferee(user)) {
			user.getRoles().add(refereeRole);
		}
		if (!userExists(user)) {
			user = createAndSaveNewUserWithRefereeRole(referee, refereeRole);
		}
		referee.setUser(user);
		save(referee);
		return user;
	}

	private boolean userExists(RegisteredUser user) {
		return user != null;
	}

	private boolean isUserReferee(RegisteredUser user) {
		return user.isInRole(Authority.REFEREE);
	}

	private RegisteredUser createAndSaveNewUserWithRefereeRole(Referee referee, Role refereeRole) {
		RegisteredUser user;
		user = newRegisteredUser();
		user.setEmail(referee.getEmail());
		user.setFirstName(referee.getFirstname());
		user.setLastName(referee.getLastname());
		user.setUsername(referee.getEmail());
		user.getRoles().add(refereeRole);
		user.setActivationCode(encryptionUtils.generateUUID());
		user.setEnabled(false);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setDirectToUrl(DirectURLsEnum.ADD_REFERENCE.displayValue() + referee.getApplication().getApplicationNumber());
		userService.save(user);
		return user;
	}

	RegisteredUser newRegisteredUser() {
		return new RegisteredUser();
	}

	@Transactional
	public void delete(Referee referee) {
		if(referee.getUser()!=null){
			referee.getUser().getReferees().remove(referee);
		}
		refereeDAO.delete(referee);

	}

	@Transactional
	public Referee getRefereeByUserAndApplication(RegisteredUser user, ApplicationForm form) {
		Referee matchedReferee = null;
		List<Referee> referees = user.getReferees();
		for (Referee referee : referees) {
			if (referee.getApplication() != null && referee.getApplication().equals(form)) {
				matchedReferee = referee;
			}
		}
		return matchedReferee;
	}

	@Transactional
	public void declineToActAsRefereeAndSendNotification(Referee referee) {
		referee.setDeclined(true);
		refereeDAO.save(referee);
		addReferenceEventToApplication(referee);
		sendMailToApplicant(referee);		
		sendMailToAdministrators(referee);
	}

	private void addReferenceEventToApplication(Referee referee) {
		ApplicationForm application = referee.getApplication();
		application.getEvents().add(eventFactory.createEvent(referee));
		applicationFormDAO.save(application);
	}

	public void sendRefereeMailNotification(Referee referee) {
		sendMailToReferee(referee);
	}

	private void sendMailToReferee(Referee referee) {
		try {
			ApplicationForm form = referee.getApplication();
			List<RegisteredUser> administrators = form.getProgram().getAdministrators();
			Map<String, Object> model = new HashMap<String, Object>();
			String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
			model.put("referee", referee);
			model.put("adminsEmails", adminsEmails);
			model.put("applicant", form.getApplicant());
			model.put("application", form);
			model.put("programme", form.getProgrammeDetails());
			model.put("host", Environment.getInstance().getApplicationHostName());
			InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
			
			String subject = resolveMessage("reference.request", form);
			if (referee.getUser() != null && referee.getUser().isEnabled()) {
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,
						"private/referees/mail/existing_user_referee_notification_email.ftl", model, null));
			} else {
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,
						"private/referees/mail/referee_notification_email.ftl", model, null));
			}
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

	private String resolveMessage(String code, ApplicationForm form) {
		RegisteredUser applicant = form.getApplicant();
		Object[] args;
		if (applicant == null) {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle() };
		} else {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(),//
					applicant.getFirstName(), applicant.getLastName() };
		}
		return messageSource.getMessage(code, args, null);
	}
}
