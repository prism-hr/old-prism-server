package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.Environment;

public class NewUserMailSender extends MailSender {

	public NewUserMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);

	}

	public Map<String, Object> createModel(RegisteredUser user) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("newUser", user);
		model.put("admin", user.getPendingRoleNotifications().get(0).getAddedByUser());
		model.put("program", user.getPendingRoleNotifications().get(0).getProgram());
		String rolesString = constructRolesString(user);
		model.put("newRoles", rolesString);
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	private String constructRolesString(RegisteredUser user) {
		StringBuilder sb = new StringBuilder();
		String programTitle = null;
		for (int i = 0; i < user.getPendingRoleNotifications().size(); i++) {
			if (i > 0 && i < user.getPendingRoleNotifications().size() - 1) {
				sb.append(", ");
			}
			if (user.getPendingRoleNotifications().size() > 1 && i == (user.getPendingRoleNotifications().size() - 1)) {
				sb.append(" and ");
			}
			PendingRoleNotification pendingRoleNotification = user.getPendingRoleNotifications().get(i);
			Authority authority = pendingRoleNotification.getRole().getAuthorityEnum();
			if (authority != Authority.SUPERADMINISTRATOR && programTitle == null) {
				programTitle = pendingRoleNotification.getProgram().getTitle();
			}
			if (authority == Authority.REVIEWER || authority == Authority.INTERVIEWER || authority == Authority.SUPERVISOR) {

				sb.append("Default ");
			}
			sb.append(StringUtils.capitalize(authority.toString().toLowerCase()));

		}
		if(programTitle != null){
			sb.append(" for " + programTitle);
		}
		return sb.toString();
	}

	public void sendNewUserNotification(RegisteredUser user) {
		InternetAddress toAddress = createAddress(user);

		Map<String, Object> model = createModel(user);
		String subject = null;
		if (model.get("program") != null) {
			subject = resolveMessage("registration.invitation");
		} else {
			subject = resolveMessage("registration.invitation.superadmin", model.get("newRoles"));
		}

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
				"private/staff/mail/new_user_suggestion.ftl", model, null));
	}
}
