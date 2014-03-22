package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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
	private ExportQueueService porticoQueueService;
	
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

		form.setStatus(ApplicationFormStatus.REJECTED);		
		form.setRejection(rejection);
		form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.REJECTED));
		
		sendRejectNotificationToApplicant(form);
		applicationDao.save(form);
		applicationFormUserRoleService.deleteApplicationActions(form);
	}
	
	private void sendRejectNotificationToApplicant(ApplicationForm form) {
		try {
			mailService.sendRejectionConfirmationToApplicant(form);
		}
		catch (Exception e) {
    		log.warn("{}", e);
		}
	}
	
	public void sendToPortico(ApplicationForm form) {
	    if (form.getProgram().getProgramFeed() != null) {
	        porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
	    }
	}
}
