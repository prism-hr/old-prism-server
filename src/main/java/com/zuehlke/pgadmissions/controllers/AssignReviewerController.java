package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ReviewerService;

@Controller
@RequestMapping(value={"application/assignReviewer"})
public class AssignReviewerController {

	private static final String REVIEWER_VIEW_NAME = "assignReviewer";
	
	private ReviewerService reviewerService;

	@Autowired
	public AssignReviewerController(ReviewerService reviewerService) {
		this.reviewerService = reviewerService;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public String assignReviewerView(ModelMap modelMap) {	
		return REVIEWER_VIEW_NAME;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public String submitReviewer(String username, Integer appId,
									ModelMap model) {
		ApplicationForm application = reviewerService.saveReviewer(username, appId);
		model.addAttribute("application", application);
		return "reviewerAssigned";
	}
}
