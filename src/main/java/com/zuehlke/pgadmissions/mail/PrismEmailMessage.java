package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

public final class PrismEmailMessage {

    private Application form;

    private String replyToAddress;

    private String fromAddress;

    private List<User> to = new ArrayList<User>();

    private List<User> cc = new ArrayList<User>();

    private List<User> bcc = new ArrayList<User>();

    private Map<String, Object> model;

    private NotificationTemplateVersion template;

    private List<PdfAttachmentInputSource> attachments;

    private static final String SPACE = " ";

    private static Transformer convertToInternetAddresses = new Transformer() {
        @Override
        public Object transform(final Object input) {
            User target = (User) input;
            try {
                StringBuilder stringBuilder = new StringBuilder(target.getFirstName());
                if (!StringUtils.isEmpty(target.getFirstName2())) {
                    stringBuilder.append(SPACE).append(target.getFirstName2());
                }
                if (!StringUtils.isEmpty(target.getFirstName3())) {
                    stringBuilder.append(SPACE).append(target.getFirstName3());
                }
                stringBuilder.append(SPACE).append(target.getLastName());
                return new InternetAddress(target.getEmail(), stringBuilder.toString());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };

    public PrismEmailMessage() {
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(final String from) {
        this.fromAddress = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(final List<User> to) {
        this.to = to;
    }

    public List<User> getCc() {
        return cc;
    }

    public void setCc(final List<User> cc) {
        this.cc = cc;
    }

    public List<User> getBcc() {
        return bcc;
    }

    public void setBcc(final List<User> bcc) {
        this.bcc = bcc;
    }

    public void setModel(final Map<String, Object> model) {
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public NotificationTemplateVersion getTemplate() {
        return template;
    }

    public void setTemplate(NotificationTemplateVersion template) {
        this.template = template;
    }

    public List<PdfAttachmentInputSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<PdfAttachmentInputSource> attachments) {
        this.attachments = attachments;
    }

    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(final String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    public Application getApplicationForm() {
        return form;
    }

    public void setApplicationForm(final Application form) {
        this.form = form;
    }

    public Address getFromAddressAsInternetAddress() {
        try {
            return new InternetAddress(fromAddress);
        } catch (AddressException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
