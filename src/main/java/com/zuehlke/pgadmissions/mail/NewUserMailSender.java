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
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i< user.getPendingRoleNotifications().size();i++ ){
			if (i > 0 && i < user.getPendingRoleNotifications().size( ) - 1 ) {
				sb.append(", ");
			}
			if ( user.getPendingRoleNotifications().size( )> 1 && i ==  (user.getPendingRoleNotifications().size( ) - 1)) {
				sb.append(" and ");
			}
			sb.append(  StringUtils.capitalize(user.getPendingRoleNotifications().get(i).getRole().getAuthorityEnum().toString().toLowerCase()));
		}
		
		model.put("newRoles", sb.toString());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendNewUserNotification(RegisteredUser user) {
		InternetAddress toAddress = createAddress(user);

		Map<String, Object> model = createModel(user);
		String subject = null;
		if(model.get("program") != null){
			subject = resolveMessage("registration.invitation", model.get("newRoles"), ((Program)model.get("program")).getTitle());
		}else{
			subject = resolveMessage("registration.invitation.superadmin", model.get("newRoles"));
		}

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,// 
				"private/staff/mail/new_user_suggestion.ftl", model, null));
	}
}
