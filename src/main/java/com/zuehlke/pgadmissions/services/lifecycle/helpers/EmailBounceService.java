package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class EmailBounceService {

    private static final Logger log = LoggerFactory.getLogger(EmailBounceService.class);

    @Value("${integration.amazon.email.bounce.queue}")
    private String bounceQueueUrl;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    public void processEmailBounces() throws IntegrationException {
        if(Strings.isNullOrEmpty(bounceQueueUrl)){
            return;
        }
        AmazonSQSClient client = new AmazonSQSClient(systemService.getAmazonCredentials());
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(bounceQueueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        ReceiveMessageResult result = client.receiveMessage(receiveMessageRequest);
        List<Message> messages = result.getMessages();

        List<DeleteMessageBatchRequestEntry> deleteRequestEntries = Lists.newArrayListWithCapacity(messages.size());

        for (Message message : messages) {
            JsonParser parser = new JsonParser();
            JsonObject notificationRoot = parser.parse(message.getBody()).getAsJsonObject();
            String messageContent = notificationRoot.getAsJsonPrimitive("Message").getAsString();
            JsonObject messageRoot = parser.parse(messageContent).getAsJsonObject();
            String notificationType = messageRoot.getAsJsonPrimitive("notificationType").getAsString();
            if (NotificationType.Bounce.toString().equals(notificationType)) {
                log.warn("Message bounced: " + message.getBody());
                JsonObject bounce = messageRoot.getAsJsonObject("bounce");
                String bounceType = bounce.getAsJsonPrimitive("bounceType").getAsString();
                if ("Permanent".equals(bounceType)) {
                    JsonArray bouncedRecipients = bounce.getAsJsonArray("bouncedRecipients");
                    for (JsonElement recipient : bouncedRecipients) {
                        JsonObject bouncedRecipient = recipient.getAsJsonObject();
                        String emailAddress = bouncedRecipient.getAsJsonPrimitive("emailAddress").getAsString();
                        User user = userService.getUserByEmail(emailAddress);
                        if (user == null) {
                            log.error("Could not find user for given bounced email address: " + emailAddress);
                            continue;
                        }

                        JsonObject bouncedMessage = parser.parse(messageContent).getAsJsonObject();
                        bouncedMessage.remove("bouncedRecipients");
                        bouncedMessage.addProperty("status", bouncedRecipient.get("status").getAsString());
                        bouncedMessage.addProperty("action", bouncedRecipient.get("action").getAsString());
                        bouncedMessage.addProperty("diagnosticCode", bouncedRecipient.get("diagnosticCode").getAsString());
                        bouncedMessage.addProperty("emailAddress", bouncedRecipient.get("emailAddress").getAsString());

                        user.setEmailBouncedMessage(bouncedMessage.toString());
                    }
                }
            } else {
                log.error("Unexpected AWS SES notification type: " + notificationType + ". Message: " + message.getBody());
            }
            deleteRequestEntries.add(new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()));
        }

        if (!deleteRequestEntries.isEmpty()) {
            DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest(bounceQueueUrl, deleteRequestEntries);
            client.deleteMessageBatch(deleteMessageBatchRequest);
        }
    }

}
