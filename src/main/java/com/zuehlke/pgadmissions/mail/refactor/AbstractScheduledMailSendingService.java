package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;

public abstract class AbstractScheduledMailSendingService {

    private final AbstractMailSender mailSender;
    
    protected final UserService userService;
    
    protected final ApplicationFormDAO applicationFormDAO;
    
    public AbstractScheduledMailSendingService(
            final AbstractMailSender mailSender, 
            final UserService userService,
            final ApplicationFormDAO formDAO) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.applicationFormDAO = formDAO;
    }
    
    public void sendEmail(final PrismEmailMessage emailMessage) {
        mailSender.sendEmail(Arrays.asList(emailMessage));
    }
    
    public void sendEmail(final Collection<PrismEmailMessage> emailMessages) {
        mailSender.sendEmail(emailMessages);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected NotificationRecord createNotificationRecordIfNotExists(final ApplicationForm form, final NotificationType type) {
        NotificationRecord notificationRecord = form.getNotificationForType(type);
        if (notificationRecord == null) {
            notificationRecord = new NotificationRecord(type);
            form.addNotificationRecord(notificationRecord);
        }
        notificationRecord.setDate(new Date());
        applicationFormDAO.save(form);
        return notificationRecord;
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getSupervisorsFromLatestApprovalRound(final ApplicationForm form) {
        if (form.getLatestApprovalRound() != null) {
            return CollectionUtils.collect(form.getLatestApprovalRound().getSupervisors(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Supervisor) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    protected Collection<RegisteredUser> getProgramAdministrators(final ApplicationForm form) {
        return form.getProgram().getAdministrators();
    }
}
