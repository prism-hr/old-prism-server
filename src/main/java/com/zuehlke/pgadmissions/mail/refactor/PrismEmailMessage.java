package com.zuehlke.pgadmissions.mail.refactor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.InputStreamSource;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public final class PrismEmailMessage {
    
    private RegisteredUser replyToAddress;

    private RegisteredUser from;
    
    private String subject;
    
    private Collection<RegisteredUser> to = new ArrayList<RegisteredUser>();
    
    private Collection<RegisteredUser> cc = new ArrayList<RegisteredUser>();

    private Collection<RegisteredUser> bcc = new ArrayList<RegisteredUser>();
    
    private Map<String, Object> model;
    
    private EmailTemplateName templateName;
    
    private Collection<InputStreamSource> attachments;
    
    private static final String SPACE = " ";

    private static Transformer convertToInternetAddresses = new Transformer() {
        @Override
        public Object transform(final Object object) {
            RegisteredUser target = (RegisteredUser) object;
            try {
                StringBuilder stringBuilder = new StringBuilder(target.getFirstName());
                if (StringUtils.isEmpty(target.getFirstName2())) {
                    stringBuilder.append(SPACE).append(target.getFirstName2());
                }
                if (StringUtils.isEmpty(target.getFirstName3())) {
                    stringBuilder.append(SPACE).append(target.getFirstName3());
                }
                stringBuilder.append(SPACE).append(target.getLastName());
                return new InternetAddress(target.getEmail(), stringBuilder.toString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    PrismEmailMessage() {
    }
    
    @SuppressWarnings("unchecked")
    public Collection<InternetAddress> getToAsInternetAddresses() {
        return CollectionUtils.collect(to, convertToInternetAddresses);
    }
    
    @SuppressWarnings("unchecked")
    public Collection<InternetAddress> getCcAsInternetAddresses() {
        return CollectionUtils.collect(cc, convertToInternetAddresses);
    }
    
    @SuppressWarnings("unchecked")
    public Collection<InternetAddress> getBccAsInternetAddresses() {
        return CollectionUtils.collect(bcc, convertToInternetAddresses);
    }
    
    public RegisteredUser getFrom() {
        return from;
    }

    public void setFrom(final RegisteredUser from) {
        this.from = from;
    }

    public Collection<RegisteredUser> getTo() {
        return to;
    }

    public void setTo(final Collection<RegisteredUser> to) {
        this.to = to;
    }

    public Collection<RegisteredUser> getCc() {
        return cc;
    }

    public void setCc(final Collection<RegisteredUser> cc) {
        this.cc = cc;
    }

    public Collection<RegisteredUser> getBcc() {
        return bcc;
    }

    public void setBcc(final Collection<RegisteredUser> bcc) {
        this.bcc = bcc;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setModel(final Map<String, Object> model) {
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }
    
    public EmailTemplateName getTemplateName() {
        return templateName;
    }

    public void setTemplateName(final EmailTemplateName templateName) {
        this.templateName = templateName;
    }
    
    public Collection<InputStreamSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(final Collection<InputStreamSource> attachments) {
        this.attachments = attachments;
    }
    
    public RegisteredUser getReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(final RegisteredUser replyToAddress) {
        this.replyToAddress = replyToAddress;
    }
}
