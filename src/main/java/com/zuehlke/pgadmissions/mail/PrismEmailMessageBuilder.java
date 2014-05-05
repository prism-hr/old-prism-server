package com.zuehlke.pgadmissions.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

public class PrismEmailMessageBuilder {

    protected ApplicationForm form;
    
    protected String fromAddress;
    
    protected String replyToAddress;
    
    protected String subject;
    
    protected HashMap<Integer, User> to = new HashMap<Integer, User>();
    
    protected HashMap<Integer, User> cc = new HashMap<Integer, User>();

    protected HashMap<Integer, User> bcc = new HashMap<Integer, User>();
    
    protected Map<String, Object> model = new HashMap<String, Object>();
    
    protected EmailTemplateName templateName;
    
    protected List<PdfAttachmentInputSource> attachments = new ArrayList<PdfAttachmentInputSource>();
    
    private class CategoriseUsersClosure implements Closure {
        private final Map<Integer, User> targetMap;

        public CategoriseUsersClosure(final Map<Integer, User> targetMap) {
            this.targetMap = targetMap;
        }
        
        @Override
        public void execute(final Object object) {
            User user = (User) object;
            if (isNotDuplicate(user)) {
                targetMap.put(user.getId(), user);
            }
        }
    } 
    
    public PrismEmailMessageBuilder() {
    }
    
    public PrismEmailMessageBuilder to(final User... users) {
        to(Arrays.asList(users));
        return this;
    }
    
    public PrismEmailMessageBuilder cc(final User... users) {
        cc(Arrays.asList(users));
        return this;
    }
    
    public PrismEmailMessageBuilder bcc(final User... users) {
        bcc(Arrays.asList(users));
        return this;
    }

    public PrismEmailMessageBuilder to(final Collection<User> users) {
        CollectionUtils.forAllDo(users, new CategoriseUsersClosure(to));
        return this;
    }
    
    public PrismEmailMessageBuilder cc(final Collection<User> users) {
        CollectionUtils.forAllDo(users, new CategoriseUsersClosure(cc));
        return this;
    }
    
    public PrismEmailMessageBuilder bcc(final Collection<User> users) {
        CollectionUtils.forAllDo(users, new CategoriseUsersClosure(bcc));
        return this;
    }
    
    public PrismEmailMessageBuilder to(final EmailRecipientsBuilder builder) {
        to(builder.build());
        return this;
    }
    
    public PrismEmailMessageBuilder cc(final EmailRecipientsBuilder builder) {
        cc(builder.build());
        return this;
    }
    
    public PrismEmailMessageBuilder bcc(final EmailRecipientsBuilder builder) {
        bcc(builder.build());
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public PrismEmailMessageBuilder to(final Collection<?> users, final Transformer transformer) {
        to(CollectionUtils.collect(users, transformer));
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public PrismEmailMessageBuilder cc(final Collection<?> users, final Transformer transformer) {
        cc(CollectionUtils.collect(users, transformer));
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public PrismEmailMessageBuilder bcc(final Collection<?> users, final Transformer transformer) {
        bcc(CollectionUtils.collect(users, transformer));
        return this;
    }
    
    
    public PrismEmailMessageBuilder subject(final String subjectCode) {
        this.subject = subjectCode;
        return this;
    }
    
    public PrismEmailMessageBuilder from(final String from) {
        this.fromAddress = from;
        return this;
    }
    
    public PrismEmailMessageBuilder model(final EmailModelBuilder modelBuilder) {
        this.model = modelBuilder.build();
        return this;
    }
    
    public PrismEmailMessageBuilder model(final Map<String, Object> model) {
        this.model = model;
        return this;
    }
    
    public PrismEmailMessageBuilder addToModel(final String key, Object value) {
        this.model.put(key, value);
        return this;
    }
    
    public PrismEmailMessageBuilder emailTemplate(final EmailTemplateName templateName) {
        this.templateName = templateName;
        return this;
    }
    
    public PrismEmailMessageBuilder emailTemplate(final String templateName) {
        this.templateName = EmailTemplateName.valueOf(templateName);
        return this;
    }
    
    public PrismEmailMessageBuilder attachments(final PdfAttachmentInputSource... sources) {
        for (PdfAttachmentInputSource source : sources) {
            this.attachments.add(source);
        }
        return this;
    }
    
    public PrismEmailMessageBuilder replyToAddress(final String address) {
        this.replyToAddress = address;
        return this;
    }
    
    public PrismEmailMessageBuilder applicationForm(final ApplicationForm form) {
        this.form = form;
        return this;
    }
    
    public PrismEmailMessage build() {
        PrismEmailMessage msg = new PrismEmailMessage();
        msg.setBcc(new ArrayList<User>(bcc.values()));
        msg.setCc(new ArrayList<User>(cc.values()));
        msg.setFromAddress(fromAddress);
        msg.setSubjectCode(subject);
        msg.setTo(new ArrayList<User>(to.values()));
        msg.setModel(model);
        msg.setTemplateName(templateName);
        msg.setAttachments(new ArrayList<PdfAttachmentInputSource>(attachments));
        msg.setReplyToAddress(replyToAddress);
        msg.setApplicationForm(form);
        return msg;
    }
    
    public static PrismEmailMessageBuilder copyOf(final PrismEmailMessageBuilder builder) {
        PrismEmailMessageBuilder newCopy = new PrismEmailMessageBuilder();
        newCopy.attachments = new ArrayList<PdfAttachmentInputSource>(builder.attachments);
        newCopy.bcc = new HashMap<Integer, User>(builder.bcc);
        newCopy.cc = new HashMap<Integer, User>(builder.cc);
        newCopy.form = builder.form;
        newCopy.fromAddress = String.valueOf(builder.fromAddress);
        newCopy.model = new HashMap<String, Object>(builder.model);
        newCopy.subject = String.valueOf(builder.subject);
        newCopy.templateName = builder.templateName;
        newCopy.replyToAddress = String.valueOf(builder.replyToAddress);
        newCopy.to = new HashMap<Integer, User>(builder.to);
        return newCopy;
    }
    
    protected boolean isNotDuplicate(final User user) {
        if (to.containsKey(user.getId()) || cc.containsKey(user.getId()) || bcc.containsKey(user.getId())) {
            return false;
        }
        return true;
    }
}
