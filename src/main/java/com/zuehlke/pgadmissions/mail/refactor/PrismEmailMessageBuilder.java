package com.zuehlke.pgadmissions.mail.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamSource;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.EmailNotificationType;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public final class PrismEmailMessageBuilder {

    private RegisteredUser from;
    
    private RegisteredUser replyToAddress;
    
    private String subject;
    
    private HashMap<Integer, RegisteredUser> to = new HashMap<Integer, RegisteredUser>();
    
    private HashMap<Integer, RegisteredUser> cc = new HashMap<Integer, RegisteredUser>();

    private HashMap<Integer, RegisteredUser> bcc = new HashMap<Integer, RegisteredUser>();
    
    private Map<String, Object> model = new HashMap<String, Object>();
    
    private EmailTemplateName templateName;
    
    private Collection<InputStreamSource> attachments = new ArrayList<InputStreamSource>();
    
    public PrismEmailMessageBuilder() {
    }
    
    public PrismEmailMessageBuilder to(final RegisteredUser... users) {
        for (RegisteredUser user : users) {
            if (!excludeUserFromList(user)) {
                to.put(user.getId(), user);
            }
        }
        return this;
    }
    
    public PrismEmailMessageBuilder cc(final RegisteredUser... users) {
        for (RegisteredUser user : users) {
            if (!excludeUserFromList(user)) {
                cc.put(user.getId(), user);
            }
        }
        return this;
    }
    
    public PrismEmailMessageBuilder bcc(final RegisteredUser... users) {
        for (RegisteredUser user : users) {
            if (!excludeUserFromList(user)) {
                bcc.put(user.getId(), user);
            }
        }
        return this;
    }
    
    public PrismEmailMessageBuilder subject(final String subject) {
        this.subject = subject;
        return this;
    }
    
    public PrismEmailMessageBuilder from(final RegisteredUser from) {
        this.from = from;
        return this;
    }
    
    public PrismEmailMessageBuilder model(final BuildModelBuilder modelBuilder) {
        this.model = modelBuilder.buildModel();
        return this;
    }
    
    public PrismEmailMessageBuilder model(Map<String, Object> model) {
        this.model = model;
        return this;
    }
    
    public PrismEmailMessageBuilder emailTemplate(final EmailTemplateName templateName) {
        this.templateName = templateName;
        return this;
    }
    
    public PrismEmailMessageBuilder attachments(final InputStreamSource... sources) {
        for (InputStreamSource source : sources) {
            this.attachments.add(source);
        }
        return this;
    }
    
    public PrismEmailMessageBuilder replyToAddress(final RegisteredUser user) {
        this.replyToAddress = user;
        return this;
    }
    
    public PrismEmailMessage build() {
        PrismEmailMessage msg = new PrismEmailMessage();
        msg.setBcc(bcc.values());
        msg.setCc(cc.values());
        msg.setFrom(from);
        msg.setSubject(subject);
        msg.setTo(to.values());
        msg.setModel(model);
        msg.setTemplateName(templateName);
        msg.setAttachments(attachments);
        return msg;
    }
    
    private boolean excludeUserFromList(final RegisteredUser user) {
        if (user.getEmailNotificationType() == EmailNotificationType.DIGEST) {
            return true;
        }
        
        if (to.containsKey(user.getId()) || cc.containsKey(user.getId()) || bcc.containsKey(user.getId())) {
            return true;
        }
        
        return false;
    }
}
