package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.services.EmailBounceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
public class EmailBounceServiceHelper implements AbstractServiceHelper {

    @Value("${integration.amazon.email.bounce.queue}")
    private String bounceQueueUrl;
	
    @Inject
    private EmailBounceService emailBounceService;
    
    @Inject
    private SystemService systemService;

    @Override
    public void execute() throws Exception {
        if (!Strings.isNullOrEmpty(bounceQueueUrl)) {    
	        AmazonSQSClient client = new AmazonSQSClient(systemService.getAmazonCredentials());
	        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(bounceQueueUrl);
	        receiveMessageRequest.setMaxNumberOfMessages(10);
	        ReceiveMessageResult result = client.receiveMessage(receiveMessageRequest);
	        List<Message> messages = result.getMessages();
	
	        List<DeleteMessageBatchRequestEntry> deleteRequestEntries = Lists.newArrayListWithCapacity(messages.size());
	
	        for (Message message : messages) {
	            emailBounceService.processBounceMessage(message);
	            deleteRequestEntries.add(new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()));
	        }
	
	        if (!deleteRequestEntries.isEmpty()) {
	            DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest(bounceQueueUrl, deleteRequestEntries);
	            client.deleteMessageBatch(deleteMessageBatchRequest);
	        }
        }
    }

}
