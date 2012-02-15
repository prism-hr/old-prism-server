package com.zuehlke.pgadmissions.services;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Component
public class ReviewerService {

	private final ApplicationFormDAO applicationDAO;
	private final UserDAO userDAO;

	ReviewerService(){
		this(null, null);
	}

	public ReviewerService(ApplicationFormDAO applicationDAO,
			UserDAO userDAO) {
		this.applicationDAO = applicationDAO;
		this.userDAO = userDAO;
	}

	public ApplicationForm saveReviewer(String username, Integer appId) {
		RegisteredUser reviewer = userDAO.getUserByUsername(username);
		
		ApplicationForm application = applicationDAO.get(appId);
		application.setReviewer(reviewer);
		applicationDAO.save(application);
		
		return application;
	}
}
