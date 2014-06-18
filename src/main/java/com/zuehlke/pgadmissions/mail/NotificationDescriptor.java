package com.zuehlke.pgadmissions.mail;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;

public class NotificationDescriptor {

    private User recipient;

    private PrismResourceDynamic resource;

    private PrismNotificationTemplate notificationTemplate;
    
    private Comment comment;

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public PrismResourceDynamic getResource() {
        return resource;
    }

    public void setResource(PrismResourceDynamic resource) {
        this.resource = resource;
    }

    public PrismNotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(PrismNotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}
