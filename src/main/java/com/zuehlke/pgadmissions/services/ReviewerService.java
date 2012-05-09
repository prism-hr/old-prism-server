package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;

@Service
public class ReviewerService {
	
	private final ReviewerDAO reviewerDAO;
	
	
	ReviewerService() {
		this(null);
	}
	
	@Autowired
	public ReviewerService(ReviewerDAO reviewerDAO){
		this.reviewerDAO = reviewerDAO;
	}
	

	@Transactional
	public Reviewer getReviewerById(Integer id) {
		return reviewerDAO.getReviewerById(id);
	}

	@Transactional
	public void save(Reviewer reviewer) {
		reviewerDAO.save(reviewer);
	}

	@Transactional
	public Reviewer getReviewerByUser(RegisteredUser user) {
		return reviewerDAO.getReviewerByUser(user);
	}

}
