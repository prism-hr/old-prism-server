package com.zuehlke.pgadmissions.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
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
	    List<String> rolesList = new ArrayList<String>();
	    String programTitle = null;

	    for (PendingRoleNotification roleNotification : user.getPendingRoleNotifications()) {
	        Authority authority = roleNotification.getRole().getAuthorityEnum();
	        String roleAsString = StringUtils.capitalize(authority.toString().toLowerCase());
	        
	        if (authority != Authority.SUPERADMINISTRATOR && StringUtils.isBlank(programTitle)) {
                programTitle = roleNotification.getProgram().getTitle();
            }
	        
	        switch (authority) {
            case INTERVIEWER:
            case REVIEWER:
            case SUPERVISOR:
                rolesList.add("Default " + roleAsString);
                break;
            default:
                rolesList.add(roleAsString);
                break;
            }
	    }
	    
	    StringBuilder messageBuilder = new StringBuilder(StringUtils.join(rolesList.toArray(new String[]{}), ", ", 0, rolesList.size() - 1));
	    messageBuilder.append(" and " ).append(rolesList.get(rolesList.size() - 1));
	    if (StringUtils.isNotBlank(programTitle)) {
            messageBuilder.append(" for ").append(programTitle);
        }
	    
	    return messageBuilder.toString();
	}

	public void sendNewUserNotificationAsReminder(RegisteredUser user) {
	    sendNewUserNotification(user, true);
	}
	
	public void sendNewUserNotification(RegisteredUser user) {
	    sendNewUserNotification(user, false);
	}
	
	private void sendNewUserNotification(RegisteredUser user, boolean resent) {
	    InternetAddress toAddress = createAddress(user);	    
	    Map<String, Object> model = createModel(user);
	    String subject = createSubject(model, resent);
	    javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, "private/staff/mail/new_user_suggestion.ftl", model, null));
	}
	
	private String createSubject(Map<String, Object> model, boolean reminder) {
	    String messageSource = null;
        
	    if (model.get("program") != null) {
            messageSource = "registration.invitation";
        } else {
            messageSource = "registration.invitation.superadmin";
        }
        
        if (reminder) {
            messageSource += ".resent";
        }
        
        if (model.get("program") != null) {
            return resolveMessage(messageSource);
        } else {
            return resolveMessage(messageSource, model.get("newRoles"));
        }
	}
}
