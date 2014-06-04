package com.zuehlke.pgadmissions.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.mail.NotificationService;

@Service
@Transactional
public class RejectService {
	
	private final Logger log = LoggerFactory.getLogger(RejectService.class);

	@Autowired
	private ApplicationDAO applicationDao;
	
	@Autowired
	private NotificationService mailService;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private StateDAO stateDAO;

	public void moveApplicationToReject(final Application form, final Comment rejection) {

		form.setState(stateDAO.getById(PrismState.APPLICATION_REJECTED));		
		
		applicationDao.save(form);
	}
	
	public void sendToPortico(Application form) {
	    if (form.getProgram().isImported()) {
//	        porticoQueueService.createOrReturnExistingApplicationFormTransfer(form);
	    }
	}
}
