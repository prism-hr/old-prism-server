package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;

public abstract class AbstractScheduledMailSendingService extends AbstractMailSendingService {

    protected final AbstractMailSender mailSender;
    
    protected final ApplicationFormDAO applicationDAO;
    
    public AbstractScheduledMailSendingService(
            final AbstractMailSender mailSender, 
            final UserService userService,
            final ApplicationFormDAO formDAO) {
        super(userService);
        this.mailSender = mailSender;
        this.applicationDAO = formDAO;
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
        applicationDAO.save(form);
        return notificationRecord;
    }
}
