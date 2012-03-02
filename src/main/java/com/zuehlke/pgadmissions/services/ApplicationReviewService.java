package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationReviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;


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

	@Transactional
	public ApplicationReview getReviewById(int id) {
		return applicationReviewDAO.get(id);
	}

	@Transactional
	public List<ApplicationReview> getVisibleComments(
			ApplicationForm application, RegisteredUser user) {
		List<ApplicationReview> visibleComments = new ArrayList<ApplicationReview>();
		List<ApplicationReview> allReviewsForApplication = getApplicationReviewsByApplication(application);
		for (ApplicationReview comment : allReviewsForApplication){
			if (comment.getUser().isInRole(Authority.REVIEWER) && (!comment.getUser().equals(user))){
				continue;
			}
			else{
				visibleComments.add(comment);
			}
		}
		return visibleComments;
	}

	@Transactional
	public void saveQualification(Qualification qualification) {
		applicationReviewDAO.saveQualification(qualification);
		
	}

	@Transactional
	public void saveUser(RegisteredUser currentuser) {
		applicationReviewDAO.saveUser(currentuser);
	}


}
