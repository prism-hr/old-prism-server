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
import com.zuehlke.pgadmissions.domain.CommentModel;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "/comment" })
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
	
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public ModelAndView getCommentPage(@RequestParam Integer id, @RequestParam String cmtDecision) {
		CommentModel commentModel = new CommentModel();
		commentModel.setApplication(applicationService.getApplicationById(id));
		return new ModelAndView("commentForm", "model", commentModel);
	}

	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	@Transactional
	public ModelAndView getCommentedApplicationPage(@RequestParam Integer id,
			@RequestParam String comment) {
		CommentModel commentModel = new CommentModel();
		ApplicationReview applicationReview = new ApplicationReview();
		ApplicationForm application1 = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT))
		{
			commentModel.setMessage("You are not authorized to comment on the application");
			}
		else if (application1.getApproved() != null)
			{
			commentModel.setMessage("You cannot comment on a completed application");
			}
		else if(application1.getSubmissionStatus().equals(SubmissionStatus.UNSUBMITTED)){
			commentModel.setMessage("You cannot comment on a non submitted application");
		}
		else {
			applicationReview.setApplication(application1);
			applicationReview.setComment(comment);
			applicationReview.setUser(user);
			applicationReviewService.save(applicationReview);
			commentModel.setMessage("Your comment is submitted successful");
			commentModel.setComment(comment);
			commentModel.setApplication(application1);
		}
		return new ModelAndView("comment", "model", commentModel);
	}

	

}
