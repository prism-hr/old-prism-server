package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {

	private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final FeedbackCommentValidator reviewFeedbackValidator;
	private final CommentService commentService;

	ReviewCommentController() {
		this(null, null, null, null);
	}

	@Autowired
	public ReviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			FeedbackCommentValidator reviewFeedbackValidator) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.reviewFeedbackValidator = reviewFeedbackValidator;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null  || !currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm) || !currentUser.canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getReviewFeedbackPage() {
		return REVIEW_FEEDBACK_PAGE;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("comment")
	public ReviewComment getComment(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setApplication(applicationForm);
		RegisteredUser currentUser = getUser();
		reviewComment.setUser(currentUser);
		reviewComment.setComment("");
		reviewComment.setType(CommentType.REVIEW);
		reviewComment.setReviewer(currentUser.getReviewersForApplicationForm(applicationForm).get(0));
		return reviewComment;
	}

	@InitBinder(value = "comment")
	public void registerBinders(WebDataBinder binder) {
		binder.setValidator(reviewFeedbackValidator);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String addComment(@Valid @ModelAttribute("comment") ReviewComment comment, BindingResult result) {
		if(comment.getApplication().isDecided()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return REVIEW_FEEDBACK_PAGE;
		}
		commentService.save(comment);		
		return "redirect:/review/assignReviewers?applicationId=" + comment.getApplication().getApplicationNumber();
	}
}
