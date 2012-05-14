package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class NewUserMailSender extends MailSender {

	public NewUserMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);

	}

	public Map<String, Object> createModel(RegisteredUser user) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("newUser", user);
		model.put("admin", user.getPendingRoleNotifications().get(0).getAddedByUser());
		model.put("program", user.getPendingRoleNotifications().get(0).getProgram());
		StringBuilder sb = new StringBuilder();
		for (PendingRoleNotification pendingRoleNotification : user.getPendingRoleNotifications()) {
			if(sb.length() > 0){
				sb.append(", ");
			}
			sb.append(pendingRoleNotification.getRole().getAuthorityEnum());
		}
		model.put("newRoles", sb.toString());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendNewUserNotification(RegisteredUser user) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "UCL Portal Registration",
				"private/staff/mail/new_user_suggestion.ftl", createModel(user)));

	}

}
