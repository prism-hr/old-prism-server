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
import com.zuehlke.pgadmissions.exceptions.CannotViewCommentsException;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentController {

	private static final String COMMENT_FORM_VIEW_NAME = "comments/commentForm";
	private static final String SHOW_COMMENTS_VIEW_NAME = "comments/comments";
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
	
	@RequestMapping(value = { "/addComment" }, method = RequestMethod.GET)
	@Transactional
	public ModelAndView getCommentPage(@RequestParam Integer id) {
		CommentModel commentModel = new CommentModel();
		commentModel.setApplication(applicationService.getApplicationById(id));
		return new ModelAndView(COMMENT_FORM_VIEW_NAME, "model", commentModel);
	}

	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	@Transactional
	public ModelAndView getCommentedApplicationPage(@RequestParam Integer id,
			@RequestParam String comment) {
		CommentModel commentModel = new CommentModel();
		ApplicationReview applicationReview = new ApplicationReview();
		ApplicationForm application = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT))
		{
			commentModel.setMessage("You are not authorized to comment on the application");
		}
		else if (application.getApprovalStatus() != null)
		{
			commentModel.setMessage("You cannot comment on a completed application");
		}
		else if(application.getSubmissionStatus().equals(SubmissionStatus.UNSUBMITTED)){
			commentModel.setMessage("You cannot comment on a non submitted application");
		}
		else 
		{
			applicationReview.setApplication(application);
			applicationReview.setComment(comment);
			applicationReview.setUser(user);
			applicationReviewService.save(applicationReview);
			commentModel.setMessage("Your comment is submitted successful");
			commentModel.setComment(comment);
			commentModel.setApplication(application);
		}
		return new ModelAndView("redirect:/application", "id", application.getId());
	}

	@RequestMapping(value = { "/showAll" }, method = RequestMethod.GET)
	@Transactional
	public ModelAndView getAllCommentsForApplication(@RequestParam Integer id) {
		CommentModel commentModel = new CommentModel();
		ApplicationForm application = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT)){
			throw new CannotViewCommentsException();
		}
		else if (user.isInRole(Authority.ADMINISTRATOR) || user.isInRole(Authority.APPROVER)){
			commentModel.setMessage("Comments: ");
			commentModel.setApplication(application);
			commentModel.setComments(applicationReviewService.getApplicationReviewsByApplication(application));
		}
		else if (user.isInRole(Authority.REVIEWER)){
			commentModel.setMessage("Comments: ");
			commentModel.setApplication(application);
			commentModel.setComments(applicationReviewService.getVisibleComments(application, user));
		}
		
		return new ModelAndView(SHOW_COMMENTS_VIEW_NAME, "model", commentModel);
	}

	

}
