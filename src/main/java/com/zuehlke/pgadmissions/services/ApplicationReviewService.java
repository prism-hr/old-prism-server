package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationReviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;


@Service("applicationReviewService")
public class ApplicationReviewService {

	private final ApplicationReviewDAO applicationReviewDAO;

	ApplicationReviewService(){
		this(null);
	}

	
	@Autowired
	public ApplicationReviewService(ApplicationReviewDAO applicationReviewDAO) {
		this.applicationReviewDAO = applicationReviewDAO;
	}
	
	@Transactional
	public List<ApplicationReview> getApplicationReviewsByApplication(ApplicationForm application){
		return applicationReviewDAO.getReviewsByApplication(application);
	}

	@Transactional
	public void save(ApplicationReview applicationReview) {
		applicationReviewDAO.save(applicationReview);
	}

	public ApplicationReview getReviewById(int id) {
		return applicationReviewDAO.get(id);
	}
	
}
