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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class RejectService {
	
	private final Logger log = LoggerFactory.getLogger(RejectService.class);

	private final ApplicationFormDAO applicationDao;
	
	private final RejectReasonDAO rejectDao;
	
	private final EventFactory eventFactory;
	
	private final PorticoQueueService porticoQueueService;
	
	private final MailSendingService mailService;

	public RejectService() {
		this(null, null, null, null, null);
	}

	@Autowired
    public RejectService(ApplicationFormDAO applicationDAO, RejectReasonDAO rejectDao, EventFactory eventFactory,
    		PorticoQueueService rejectedSenderService, MailSendingService mailService) {
		this.applicationDao = applicationDAO;
		this.rejectDao = rejectDao;
		this.eventFactory = eventFactory;
		this.porticoQueueService = rejectedSenderService;
		this.mailService = mailService;
	}

	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}

	public RejectReason getRejectReasonById(Integer id) {
		return rejectDao.getRejectReasonById(id);
	}

	public void moveApplicationToReject(final ApplicationForm form, final RegisteredUser approver, final Rejection rejection) {
		if (rejection == null) {
			throw new IllegalArgumentException("rejection must be provided!");
		}
		if (approver == null) {
			throw new IllegalArgumentException("approver must not be null!");
		}
		if (!(form.getProgram().isApprover(approver) || approver.hasAdminRightsOnApplication(form))) {
			throw new IllegalArgumentException("approver is not an approver or administrator in the program of the application!");
		}
        form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.ALL_USERS, new Date()));
		form.setApprover(approver);
		form.setStatus(ApplicationFormStatus.REJECTED);		
		form.setRejection(rejection);
		form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.REJECTED));
		
		sendRejectNotificationToApplicant(form);
		applicationDao.save(form);
		
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
