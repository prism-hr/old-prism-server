package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.APPLICATION_MOVED_TO_REJECT_NOTIFICATION;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class RejectService {
	
	private final Logger log = LoggerFactory.getLogger(RejectService.class);

	@Autowired
	private ApplicationFormDAO applicationDao;
	
	@Autowired
	private RejectReasonDAO rejectDao;
	
	@Autowired
	private EventFactory eventFactory;
	
	@Autowired
	private PorticoQueueService porticoQueueService;
	
	@Autowired
	private MailSendingService mailService;
	
	@Autowired
	private ApplicationFormUserRoleService applicationFormUserRoleService;

	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}

	public RejectReason getRejectReasonById(Integer id) {
		return rejectDao.getRejectReasonById(id);
	}

	public void moveApplicationToReject(final ApplicationForm form, final Rejection rejection) {

	    form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.ALL_USERS, new Date()));
		form.setStatus(ApplicationFormStatus.REJECTED);		
		form.setRejection(rejection);
		form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.REJECTED));
		
		sendRejectNotificationToApplicant(form);
		applicationDao.save(form);
		applicationFormUserRoleService.moveToApprovedOrRejectedOrWithdrawn(form);
	}
	
	private void sendRejectNotificationToApplicant(ApplicationForm form) {
		try {
			mailService.sendRejectionConfirmationToApplicant(form);
			NotificationRecord notificationRecord = form.getNotificationForType(APPLICATION_MOVED_TO_REJECT_NOTIFICATION);
			if (notificationRecord == null) {
				notificationRecord = new NotificationRecord(APPLICATION_MOVED_TO_REJECT_NOTIFICATION);
				form.addNotificationRecord(notificationRecord);
			}
			notificationRecord.setDate(new Date());
		}
		catch (Exception e) {
    		log.warn("{}", e);
		}
	}
	
	public void sendToPortico(ApplicationForm form) {
	    porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);	    
	}
}
