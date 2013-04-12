package com.zuehlke.pgadmissions.mail.refactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
public class AdminMailSender {

    private final ConfigurationService configurationService;
    
    private final EmailTemplateAwareMailSender mailSender;
    
    private class AdminPrismEmailMessageBuilder extends PrismEmailMessageBuilder {
        public AdminPrismEmailMessageBuilder copyOf(AdminPrismEmailMessageBuilder builder) {
            AdminPrismEmailMessageBuilder newCopy = new AdminPrismEmailMessageBuilder();
            newCopy.attachments = new ArrayList<PdfAttachmentInputSource>(builder.attachments);
            newCopy.bcc = new HashMap<Integer, RegisteredUser>(builder.bcc);
            newCopy.cc = new HashMap<Integer, RegisteredUser>(builder.cc);
            newCopy.form = builder.form;
            newCopy.fromAddress = String.valueOf(builder.fromAddress);
            newCopy.model = new HashMap<String, Object>(builder.model);
            newCopy.subjectArgs = new ArrayList<Object>(builder.subjectArgs);
            newCopy.subjectCode = String.valueOf(builder.subjectCode);
            newCopy.templateName = builder.templateName;
            newCopy.replyToAddress = String.valueOf(builder.replyToAddress);
            newCopy.to = new HashMap<Integer, RegisteredUser>(builder.to);
            return newCopy;
        }
        
        public List<PrismEmailMessage> buildIndividualMessages(final boolean ccApplicationAdmin) {
            List<PrismEmailMessage> returnList = new ArrayList<PrismEmailMessage>(this.to.size());
            
            RegisteredUser applicationAdmin = form.getApplicationAdministrator();
            if (applicationAdmin == null || !applicationAdmin.isEnabled() || !ccApplicationAdmin) {
                for (RegisteredUser admin : to.values()) {
                    AdminPrismEmailMessageBuilder builder = copyOf(this);
                    builder.to = new HashMap<Integer, RegisteredUser>();
                    builder.to.put(admin.getId(), admin);
                    builder.model.put("admin", admin);
                    returnList.add(builder.build());
                }
            } else { 
                // send one email to application administrator, CC to program administrators
                AdminPrismEmailMessageBuilder builder = copyOf(this);
                builder.cc = new HashMap<Integer, RegisteredUser>();
                for (RegisteredUser admin : to.values()) {
                    builder.cc.put(admin.getId(), admin);
                }
                builder.to = new HashMap<Integer, RegisteredUser>();
                builder.to.put(applicationAdmin.getId(), applicationAdmin);
                model.put("admin", applicationAdmin);
            }
            return returnList;
        }
    };
    
    public AdminMailSender() {
        this(null, null);
    }
    
    @Autowired
    public AdminMailSender(final ConfigurationService configurationService, final EmailTemplateAwareMailSender mailSender) {
        this.configurationService = configurationService;
        this.mailSender = mailSender;
    }
    
    public void sendAdministratorAndSupervisorApprovedNotification(final ApplicationForm form) {
        List<List<RegisteredUser>> uniqueRecipients = getUniqueRecipients(form);
        sendAdministratorApprovedNotification(form, uniqueRecipients.get(0));
        sendSupervisorApprovedNotification(form, uniqueRecipients.get(1));
    }
    
    private void sendAdministratorApprovedNotification(final ApplicationForm form, final List<RegisteredUser> administrators) {
        PrismEmailMessageBuilder emailMessageBuilder = new AdminPrismEmailMessageBuilder()
            .applicationForm(form)
            .to(administrators)
            .emailTemplate(EmailTemplateName.APPROVED_NOTIFICATION)
            .subjectCode("approved.notification")
            .subjectArgs(new EmailSubjectBuilder() {
                @Override
                public List<Object> build() {
                    return Arrays.asList((Object) form, (Object) form.getOutcomeOfStage());
                }
            })
            .model(new EmailModelBuilder() {
                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("application", form);
                    model.put("host", Environment.getInstance().getApplicationHostName());
                    model.put("applicant", form.getApplicant());
                    model.put("admissionOfferServiceLevel", Environment.getInstance().getAdmissionsOfferServiceLevel());
                    model.put("approver", form.getApprover());
                    model.put("previousStage", form.getOutcomeOfStage());
                    model.put("registryContacts", configurationService.getAllRegistryUsers());
                    return model;
                }
            });
        mailSender.sendEmail(((AdminPrismEmailMessageBuilder) emailMessageBuilder).buildIndividualMessages(true));
    }
    
    private void sendSupervisorApprovedNotification(final ApplicationForm form, final List<RegisteredUser> supervisors) {
        PrismEmailMessageBuilder emailMessageBuilder = new AdminPrismEmailMessageBuilder()
            .applicationForm(form)
            .to(supervisors)
            .emailTemplate(EmailTemplateName.APPROVED_NOTIFICATION)
            .subjectCode("approved.notification")
            .subjectArgs(new EmailSubjectBuilder() {
                @Override
                public List<Object> build() {
                    return Arrays.asList((Object) form, (Object) form.getOutcomeOfStage());
                }
            })
            .model(new EmailModelBuilder() {
                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("application", form);
                    model.put("host", Environment.getInstance().getApplicationHostName());
                    model.put("applicant", form.getApplicant());
                    model.put("admissionOfferServiceLevel", Environment.getInstance().getAdmissionsOfferServiceLevel());
                    model.put("approver", form.getApprover());
                    model.put("previousStage", form.getOutcomeOfStage());
                    model.put("registryContacts", configurationService.getAllRegistryUsers());
                    return model;
                }
            });
        mailSender.sendEmail(((AdminPrismEmailMessageBuilder) emailMessageBuilder).buildIndividualMessages(false));
    }
    
    @SuppressWarnings("unchecked")
    private List<List<RegisteredUser>> getUniqueRecipients(final ApplicationForm form) {
        final List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(form.getProgram().getAdministrators());
        administrators.remove(form.getApprover());
        
        List<Supervisor> supervisors = new ArrayList<Supervisor>(); 
        if (form.getLatestApprovalRound() != null) {
            supervisors = new ArrayList<Supervisor>(form.getLatestApprovalRound().getSupervisors());
        }
        
        CollectionUtils.filter(supervisors, new Predicate() {
            @Override
            public boolean evaluate(final Object input) {
                Supervisor supervisor = (Supervisor) input;
                return !administrators.contains(supervisor.getUser());
            }
        });
        
        List<RegisteredUser> transformedSupervisors = new ArrayList<RegisteredUser>(CollectionUtils.collect(supervisors, new Transformer() {
            @Override
            public Object transform(final Object input) {
                return ((Supervisor) input).getUser();
            }
        }));
        
        return Arrays.asList(administrators, transformedSupervisors);
    }
    
    public void sendAdminInterviewNotification(final ApplicationForm form, final RegisteredUser interviewer) {
        PrismEmailMessageBuilder emailMessageBuilder = new AdminPrismEmailMessageBuilder()
            .applicationForm(form)
            .to(form.getProgram().getAdministrators())
            .emailTemplate(EmailTemplateName.INTERVIEW_SUBMISSION_NOTIFICATION)
            .subjectCode("interview.feedback.notification")
            .subjectArgs(new EmailSubjectBuilder() {
                @Override
                public List<Object> build() {
                    return Arrays.asList((Object) form, (Object) form.getOutcomeOfStage());
                }
            })
            .model(new EmailModelBuilder() {
                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("application", form);
                    model.put("host", Environment.getInstance().getApplicationHostName());
                    model.put("applicant", form.getApplicant());
                    model.put("admissionOfferServiceLevel", Environment.getInstance().getAdmissionsOfferServiceLevel());
                    model.put("approver", form.getApprover());
                    model.put("previousStage", form.getOutcomeOfStage());
                    model.put("registryContacts", configurationService.getAllRegistryUsers());
                    model.put("interviewer", interviewer);
                    return model;
                }
            });
        mailSender.sendEmail(((AdminPrismEmailMessageBuilder) emailMessageBuilder).buildIndividualMessages(false));
    }
    
    public void sendAdminRejectNotification(final ApplicationForm form) {
        PrismEmailMessageBuilder emailMessageBuilder = new AdminPrismEmailMessageBuilder()
            .applicationForm(form)
            .to(form.getProgram().getAdministrators())
            .emailTemplate(EmailTemplateName.REJECTED_NOTIFICATION_ADMIN)
            .subjectCode("interview.feedback.notification")
            .subjectArgs(new EmailSubjectBuilder() {
                @Override
                public List<Object> build() {
                    return Arrays.asList((Object) form, (Object) form.getOutcomeOfStage());
                }
            })
            .model(new EmailModelBuilder() {
                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("application", form);
                    model.put("host", Environment.getInstance().getApplicationHostName());
                    model.put("applicant", form.getApplicant());
                    model.put("admissionOfferServiceLevel", Environment.getInstance().getAdmissionsOfferServiceLevel());
                    model.put("approver", form.getApprover());
                    model.put("previousStage", form.getOutcomeOfStage());
                    model.put("registryContacts", configurationService.getAllRegistryUsers());
                    model.put("reason", form.getRejection().getRejectionReason());
                    return model;
                }
            });
        mailSender.sendEmail(((AdminPrismEmailMessageBuilder) emailMessageBuilder).buildIndividualMessages(false));
        
    }
}
