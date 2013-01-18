package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.mail.NewUserMailSender;

public class NewUserNotificationTask extends TimerTask {
    
	private final Logger log = Logger.getLogger(NewUserNotificationTask.class);
	
	private final SessionFactory sessionFactory;
	private final NewUserMailSender newUserMailSender;
	private final UserDAO userDAO;

	public NewUserNotificationTask(SessionFactory sessionFactory, NewUserMailSender newUserMailSender, UserDAO userDAO) {
		this.sessionFactory = sessionFactory;
		this.newUserMailSender = newUserMailSender;
		this.userDAO = userDAO;
	}

	@Override
	public void run() {
	    log.info("New User Notification Task Running");
	    try {
    		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
    		transaction.commit();
    		for (RegisteredUser user : users) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(user);
    			
    			for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
    			    if (notification.getNotificationDate() == null) {
    			        notification.setNotificationDate(new Date());
    			    }
    			}
    			
    	        try {
    		        newUserMailSender.sendNewUserNotification(user);
    		        userDAO.save(user);
    		        transaction.commit();
                    log.info("Notification sent to new user " + user.getEmail());
    		    } catch (Throwable e) {
    		        transaction.rollback();
                    log.warn("Error while sending notification to new user " + user.getEmail(), e);        
    		    }
    	    }
	    } catch (Throwable e) {
	        log.warn("Error in executing New User Notification Task", e);
	    }
		log.info("New User Notification Task Complete");
	}
}
