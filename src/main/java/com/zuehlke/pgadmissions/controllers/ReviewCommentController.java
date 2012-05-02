package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.ReviewFeedbackValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {

	private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final ReviewFeedbackValidator reviewFeedbackValidator;
	private final CommentService commentService;
	private final CommentFactory commentFactory;

	ReviewCommentController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ReviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			ReviewFeedbackValidator reviewFeedbackValidator, CommentFactory commentFactory) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.reviewFeedbackValidator = reviewFeedbackValidator;
		this.commentFactory = commentFactory;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		//!currentUser.isInRole(Authority.REVIEWER) add it back when everything works and unignored tests
		if (applicationForm == null ||  !currentUser.canSee(applicationForm)){
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
	public ReviewComment getComment(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setApplication(applicationForm);
		reviewComment.setUser(getUser());
		reviewComment.setComment("");
		reviewComment.setType(CommentType.REVIEW);
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
		return "redirect:/reviewFeedback?applicationId=" + comment.getApplication().getId();
	}
}
