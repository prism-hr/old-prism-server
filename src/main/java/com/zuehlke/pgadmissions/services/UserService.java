package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service("userService")
public class UserService {

	private final UserDAO userDAO;
	
	UserService() {
		this(null);
	}

	@Autowired
	public UserService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Transactional
	public List<RegisteredUser> getReviewersForApplication(ApplicationForm application){
		List<RegisteredUser> users = userDAO.getAllUsers();
		List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
		for (RegisteredUser user : users) {
			if (user.isInRole(Authority.REVIEWER) && !application.getReviewers().contains(user)) {
				reviewers.add(user);
			}
		}
		
		return reviewers;
	}

	@Transactional
	public RegisteredUser getUser(Integer reviewerId) {
		return userDAO.get(reviewerId);
	}
}
