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

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
public class RegistrationService {

	private final EncryptionUtils encryptionUtils;
	private final RoleDAO roleDAO;
	private final UserDAO userDAO;

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(RegistrationService.class);
	private final ProgramDAO programDAO;
	private final MessageSource msgSource;

	RegistrationService() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public RegistrationService(EncryptionUtils encryptionUtils, RoleDAO roleDAO, UserDAO userDAO, ProgramDAO programDAO,
			MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, MessageSource msgSource) {
		this.encryptionUtils = encryptionUtils;
		this.roleDAO = roleDAO;
		this.userDAO = userDAO;
		this.programDAO = programDAO;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;

		this.mailsender = mailsender;
		this.msgSource = msgSource;

	}

	public RegisteredUser createNewUser(RegisteredUser record, String queryString) {
		record.setActivationCode(encryptionUtils.generateUUID());
		record.setUsername(record.getEmail());
		record.setPassword(encryptionUtils.getMD5Hash(record.getPassword()));
		record.setAccountNonExpired(true);
		record.setAccountNonLocked(true);
		record.setEnabled(false);
		record.setCredentialsNonExpired(true);
		record.setOriginalApplicationQueryString(queryString);
		record.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));
		return record;
	}

	public RegisteredUser updateUser(RegisteredUser record, Integer userIdOfSuggestedUser) {
		RegisteredUser existingUser = userDAO.get(userIdOfSuggestedUser);
		existingUser.setActivationCode(encryptionUtils.generateUUID());
		existingUser.setPassword(encryptionUtils.getMD5Hash(record.getPassword()));
		existingUser.setUsername(record.getEmail());
		existingUser.setFirstName(record.getFirstName());
		existingUser.setLastName(record.getLastName());
		existingUser.setEmail(record.getEmail());
		return existingUser;
	}

	@Transactional
	public void generateAndSaveNewUser(RegisteredUser record, Integer userIdOfSuggestedUser, String queryString) {

		RegisteredUser newUser;
		if (userIdOfSuggestedUser != null) {
			newUser = updateUser(record, userIdOfSuggestedUser);
		} else {
			newUser = createNewUser(record, queryString);			
		}
		userDAO.save(newUser);

		sendConfirmationEmail(newUser);
	}

	@Transactional
	public void sendConfirmationEmail(RegisteredUser newUser) {
		try {
			Map<String, Object> model = modelMap();
			model.put("user", newUser);
			model.put("host", Environment.getInstance().getApplicationHostName());
			
			InternetAddress toAddress = new InternetAddress(newUser.getEmail(), newUser.getFirstName() + " " + newUser.getLastName());

			String subject = msgSource.getMessage("registration.confirmation", null, null);
			
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,
					"private/pgStudents/mail/registration_confirmation.ftl", model, null));
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

	public RegisteredUser findUserForActivationCode(String activationCode) {
		return userDAO.getUserByActivationCode(activationCode);
	}

	Map<String, Object> modelMap() {
		return new HashMap<String, Object>();
	}

	private String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (int i = 0; i < administrators.size(); i++) {
			if (i > 0) {
				adminsMails.append(", ");
			}

			adminsMails.append(administrators.get(i).getEmail());
		}
		return adminsMails.toString();
	}

}