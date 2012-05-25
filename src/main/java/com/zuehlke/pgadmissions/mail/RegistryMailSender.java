package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.services.UserService;

public class RegistryMailSender extends MailSender {

	private final RegistryUserDAO registryUserDAO;
	private final UserService userService;

	public RegistryMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, RegistryUserDAO registryUserDAO,
			UserService userService) {
		super(mimeMessagePreparatorFactory, mailSender);
		this.registryUserDAO = registryUserDAO;
		this.userService = userService;
	}

	public void sendApplicationToRegistryContacts(ApplicationForm applicationForm) throws UnsupportedEncodingException {
		List<RegistryUser> registryContacts = registryUserDAO.getAllRegistryUsers();
		InternetAddress[] toAddresses = new InternetAddress[registryContacts.size()];
		int counter = 0;
		for (RegistryUser registryUser : registryContacts) {
			toAddresses[counter++] = new InternetAddress(registryUser.getEmail(), registryUser.getFirstname() + " " + registryUser.getLastname());
		}
		RegisteredUser currentUser = userService.getCurrentUser();
		InternetAddress ccAdminAddres = new InternetAddress(currentUser.getEmail(), currentUser.getFirstName() + " " + currentUser.getLastName());
		String subject = "Application " + applicationForm.getId() + " for UCL " + applicationForm.getProgram().getTitle() + " - Validation Request";
		String templatename = "private/staff/admin/mail/registry_validation_request.ftl";

		MimeMessagePreparator mimeMessagePreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddresses,
				new InternetAddress[] { ccAdminAddres }, subject, templatename, createModel(applicationForm, currentUser));
		javaMailSender.send(mimeMessagePreparator);

	}

	public Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser currentAdminUser) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("sender", currentAdminUser);
		return model;

	}

}
