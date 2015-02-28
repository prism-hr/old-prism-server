package com.zuehlke.pgadmissions.services;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.sqs.model.Message;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zuehlke.pgadmissions.domain.user.User;

@Service
@Transactional
public class EmailBounceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailBounceService.class);

	@Inject
	private UserService userService;

	public void processBounceMessage(Message message) {
		JsonParser parser = new JsonParser();
		JsonObject notificationRoot = parser.parse(message.getBody()).getAsJsonObject();
		String messageContent = notificationRoot.getAsJsonPrimitive("Message").getAsString();
		JsonObject messageRoot = parser.parse(messageContent).getAsJsonObject();
		String notificationType = messageRoot.getAsJsonPrimitive("notificationType").getAsString();
		if (NotificationType.Bounce.toString().equals(notificationType)) {
			JsonObject bounce = messageRoot.getAsJsonObject("bounce");
			LOGGER.warn("Message bounced: " + bounce);
			String bounceType = bounce.getAsJsonPrimitive("bounceType").getAsString();
			if ("Permanent".equals(bounceType)) {
				JsonArray bouncedRecipients = bounce.getAsJsonArray("bouncedRecipients");
				for (JsonElement recipient : bouncedRecipients) {
					JsonObject bouncedRecipient = recipient.getAsJsonObject();
					String emailAddress = bouncedRecipient.getAsJsonPrimitive("emailAddress").getAsString();
					User user = userService.getUserByEmail(emailAddress);
					if (user == null) {
						LOGGER.error("Could not find user for given bounced email address: " + emailAddress);
						continue;
					}

					JsonObject bouncedMessage = parser.parse(messageContent).getAsJsonObject();
					bouncedMessage.remove("bouncedRecipients");

					bouncedMessage.addProperty("emailAddress", bouncedRecipient.get("emailAddress").getAsString());

					JsonElement status = bouncedRecipient.get("status");
					if (status != null) {
						bouncedMessage.addProperty("status", status.getAsString());
					}

					JsonElement action = bouncedRecipient.get("action");
					if (action != null) {
						bouncedMessage.addProperty("action", action.getAsString());
					}

					JsonElement diagnosticCode = bouncedRecipient.get("diagnosticCode");
					if (diagnosticCode != null) {
						bouncedMessage.addProperty("diagnosticCode", diagnosticCode.getAsString());
					}

					user.setEmailBouncedMessage(bouncedMessage.toString());
				}
			}
		} else {
			LOGGER.error("Unexpected AWS SES notification type: " + notificationType + ". Message: " + message.getBody());
		}
	}

}
