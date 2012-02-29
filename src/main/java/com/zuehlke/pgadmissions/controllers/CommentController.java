package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotCommentException;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentController {

	private final ApplicationReviewService applicationReviewService;
	private final ApplicationsService applicationService;

	CommentController() {
		this(null, null);
	}

	@Autowired
	public CommentController(ApplicationReviewService applicationReviewService,
			ApplicationsService applicationService) {
		this.applicationReviewService = applicationReviewService;
		this.applicationService = applicationService;

	}
	
	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	public ModelAndView getCommentedApplicationPage(@RequestParam Integer id,
			@RequestParam String comment) {
		ApplicationReview applicationReview = new ApplicationReview();
		ApplicationForm application = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT) || (application.getApprovalStatus() != null)
				|| application.getSubmissionStatus().equals(SubmissionStatus.UNSUBMITTED))
		{
			throw new CannotCommentException();
		}
		else 
		{
			applicationReview.setApplication(application);
			applicationReview.setComment(comment);
			applicationReview.setUser(user);
			applicationReviewService.save(applicationReview);
		}
		return new  ModelAndView("redirect:/application","id", application.getId());
	}

	@RequestMapping(value = { "/showAll" }, method = RequestMethod.GET)
	@Transactional
	public ModelAndView getAllCommentsForApplication(@RequestParam Integer id) {
		ApplicationForm application = applicationService.getApplicationById(id);
		
		return new  ModelAndView("redirect:/application","id", application.getId());
	}

	

}
