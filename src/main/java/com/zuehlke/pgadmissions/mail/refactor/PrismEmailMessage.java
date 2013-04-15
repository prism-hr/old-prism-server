package com.zuehlke.pgadmissions.mail.refactor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

public final class PrismEmailMessage {
    
    private ApplicationForm form;
    
    private String replyToAddress;

    private String fromAddress;
    
    private List<Object> subjectArgs;
    
    private String subjectCode;
    
    private List<RegisteredUser> to = new ArrayList<RegisteredUser>();
    
    private List<RegisteredUser> cc = new ArrayList<RegisteredUser>();

    private List<RegisteredUser> bcc = new ArrayList<RegisteredUser>();
    
    private Map<String, Object> model;
    
    private EmailTemplateName templateName;
    
    private List<PdfAttachmentInputSource> attachments;
    
    private static final String SPACE = " ";

    private static Transformer convertToInternetAddresses = new Transformer() {
        @Override
        public Object transform(final Object input) {
            RegisteredUser target = (RegisteredUser) input;
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

    public List<RegisteredUser> getTo() {
        return to;
    }

    public void setTo(final List<RegisteredUser> to) {
        this.to = to;
    }

    public List<RegisteredUser> getCc() {
        return cc;
    }

    public void setCc(final List<RegisteredUser> cc) {
        this.cc = cc;
    }

    public List<RegisteredUser> getBcc() {
        return bcc;
    }

    public void setBcc(final List<RegisteredUser> bcc) {
        this.bcc = bcc;
    }
    
    public List<Object> getSubjectArgs() {
        return subjectArgs;
    }

    public void setSubjectArgs(final List<Object> subjectArgs) {
        this.subjectArgs = subjectArgs;
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

    public ApplicationForm getApplicationForm() {
        return form;
    }

    public void setApplicationForm(final ApplicationForm form) {
        this.form = form;
    }
    
    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(final String subjectCode) {
        this.subjectCode = subjectCode;
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
