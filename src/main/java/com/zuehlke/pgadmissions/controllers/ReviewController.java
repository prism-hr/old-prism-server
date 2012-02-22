package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewerAssignedModel;
import com.zuehlke.pgadmissions.domain.ReviewersListModel;

@Controller
@RequestMapping(value={"/reviewer"})
public class ReviewController {

	private static final String ADD_REVIEW_SUCCESS_VIEW_NAME = "reviewer/reviewerSuccess";
	private static final String ADD_REVIEWER_VIEW_NAME = "reviewer/reviewer";
	private final ApplicationFormDAO applicationFormDAO;
	private final UserDAO userDAO;
	
	ReviewController(){
		this(null, null);
	}

	@Autowired
	public ReviewController(ApplicationFormDAO applicationFormDAO, UserDAO userDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.userDAO = userDAO;
	}


	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public ModelAndView getReviewerPage(@RequestParam Integer id) {
		ApplicationForm applicationUnderReview = applicationFormDAO.get(id);
		
		ReviewersListModel model = new ReviewersListModel();
		model.setApplication(applicationUnderReview);
		model.setReviewers(userDAO.getReviewers());
		
		return new ModelAndView(ADD_REVIEWER_VIEW_NAME, "model", model);
	}
	
	@RequestMapping(value={"/reviewerSuccess"},method = RequestMethod.POST)
	@Transactional
	public ModelAndView addReviewer(@RequestParam Integer id, @RequestParam Integer reviewerId) {
		ApplicationForm application = applicationFormDAO.get(id);
		application.setReviewer(userDAO.get(reviewerId));
		
		applicationFormDAO.save(application);
		
		ReviewerAssignedModel model = new ReviewerAssignedModel();
		model.setApplication(application);
		
		return new ModelAndView(ADD_REVIEW_SUCCESS_VIEW_NAME, "model", model);
	}
}
