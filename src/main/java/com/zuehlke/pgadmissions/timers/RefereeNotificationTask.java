package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.mail.RefereeMailSender;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeNotificationTask extends TimerTask {
    
    private final Logger log = LoggerFactory.getLogger(RefereeNotificationTask.class);
    
	private final SessionFactory sessionFactory;
	
	private final RefereeMailSender refereeMailService;
	
	private final RefereeDAO refereeDAO;
	
	private final RefereeService refereeService;

    public RefereeNotificationTask(SessionFactory sessionFactory, RefereeMailSender refereeMailService,
            RefereeDAO refereeDAO, RefereeService refereeService) {
		this.sessionFactory = sessionFactory;
		this.refereeMailService = refereeMailService;
		this.refereeDAO = refereeDAO;
		this.refereeService = refereeService;
	}

	@Override
	public void run() {
	    log.info("Referee Notification Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<Referee> refereesDueNotification = refereeDAO.getRefereesDueNotification();
    		refereeService.processRefereesRoles(refereesDueNotification);
    		transaction.commit();
    		for (Referee referee : refereesDueNotification) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(referee);
    			try {				
    				refereeMailService.sendRefereeNotification(referee);
    				referee.setLastNotified(new Date());
    				refereeDAO.save(referee);
    				transaction.commit();				
    				log.info("Notification sent to referee " +  referee.getEmail());
    			} catch (Exception e) {
    			    log.warn("Error while sending notification to referee " + referee.getEmail(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Referee Notification Task", e);
	        transaction.rollback();
	    }
		log.info("Referee Notification Task Complete"); 
	}
}
