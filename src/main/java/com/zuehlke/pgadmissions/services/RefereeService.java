package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
public class RefereeService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(RefereeService.class);
	private final RefereeDAO refereeDAO;
	private final UserService userService;
	private final RoleDAO roleDAO;

	RefereeService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public RefereeService(RefereeDAO refereeDAO, MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, UserService userService,
			RoleDAO roleDAO) {
		this.refereeDAO = refereeDAO;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.userService = userService;
		this.roleDAO = roleDAO;

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
	public Referee getRefereeByActivationCode(String activationCode) {
		return refereeDAO.getRefereeByActivationCode(activationCode);
	}

	@Transactional
	public void saveReferenceAndSendMailNotifications(Referee referee) {
		save(referee);
		sendMailToApplicant(referee);
		sendMailToAdministrators(referee);

	}

	private void sendMailToAdministrators(Referee referee) {
		ApplicationForm form = referee.getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();

		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("referee", referee);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());

				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Applicant Reference Submitted",
						"private/staff/admin/mail/reference_submit_confirmation.ftl", model));
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
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Responded",
					"private/pgStudents/mail/reference_respond_confirmation.ftl", model));
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
	public void saveReferenceAndSendDeclineNotifications(Referee referee) {
		refereeDAO.save(referee);
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
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Responded",
					"private/pgStudents/mail/reference_respond_confirmation.ftl", model));
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}

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
			if (referee.getUser() != null && referee.getUser().isEnabled()) {
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Notification",
						"private/referees/mail/existing_user_referee_notification_email.ftl", model));
			} else {
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Notification",
						"private/referees/mail/referee_notification_email.ftl", model));
			}
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

}
