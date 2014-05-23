package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
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
	private MailSendingService mailService;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private StateDAO stateDAO;

	public List<RejectReason> getAllRejectionReasons() {
		return rejectDao.getAllReasons();
	}

	public RejectReason getRejectReasonById(Integer id) {
		return rejectDao.getRejectReasonById(id);
	}

	public void moveApplicationToReject(final Application form, final Comment rejection) {

		form.setState(stateDAO.getById(PrismState.APPLICATION_REJECTED));		
		
		sendRejectNotificationToApplicant(form);
		applicationDao.save(form);
	}
	
	private void sendRejectNotificationToApplicant(Application form) {
		try {
			mailService.sendRejectionConfirmationToApplicant(form);
		}
		catch (Exception e) {
    		log.warn("{}", e);
		}
	}
	
	public void sendToPortico(Application form) {
	    if (form.getProgram().isImported()) {
//	        porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
	    }
	}
}
