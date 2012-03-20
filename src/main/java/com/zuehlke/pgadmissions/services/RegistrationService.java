package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

@Service
public class RegistrationService {

	private final EncryptionUtils encryptionUtils;
	private final RoleDAO roleDAO;
	private final UserDAO userDAO;

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(RegistrationService.class);

	RegistrationService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public RegistrationService(EncryptionUtils encryptionUtils, RoleDAO roleDAO, UserDAO userDAO, MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender) {
		this.encryptionUtils = encryptionUtils;
		this.roleDAO = roleDAO;
		this.userDAO = userDAO;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
	
		this.mailsender = mailsender;

	}

	public RegisteredUser createNewUser(RegistrationDTO record) {
		RegisteredUser user = new RegisteredUser();
		user.setActivationCode(encryptionUtils.generateUUID());
		user.setUsername(record.getEmail());
		user.setFirstName(record.getFirstname());
		user.setLastName(record.getLastname());
		user.setEmail(record.getEmail());
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setPassword(encryptionUtils.getMD5Hash(record.getPassword()));
		user.setEnabled(false);
		user.setCredentialsNonExpired(true);
		user.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));
		return user;
	}

	@Transactional
	public void generateAndSaveNewUser(RegistrationDTO recordDTO) {

		RegisteredUser newUser = createNewUser(recordDTO);
		userDAO.save(newUser);
	
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("user", newUser);			
			model.put("host", Environment.getInstance().getApplicationHostName());
			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(recordDTO.getEmail(), "Registration confirmation", "private/pgStudents/mail/registration_confirmation.ftl", model));
		} catch (Throwable e) {
			log.warn("error while sending email",e);
		}
	}



}
