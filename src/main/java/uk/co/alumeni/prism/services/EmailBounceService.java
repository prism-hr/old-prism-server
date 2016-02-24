package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.exceptions.IntegrationException;

import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
@Transactional
public class EmailBounceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailBounceService.class);

    @Value("${integration.amazon.email.bounce.queue}")
    private String bounceQueueUrl;

    @Inject
    private SystemService systemService;

	@Inject
	private UserService userService;

	public void processEmailBounces() throws IntegrationException {
        if (!Strings.isNullOrEmpty(bounceQueueUrl)) {
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
                 deleteRequestEntries.add(new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()));
             }

             if (!deleteRequestEntries.isEmpty()) {
                 DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest(bounceQueueUrl, deleteRequestEntries);
                 client.deleteMessageBatch(deleteMessageBatchRequest);
             }
        }
    }

}
