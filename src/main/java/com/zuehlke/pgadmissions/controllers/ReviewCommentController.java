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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {

	private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final GenericCommentValidator genericCommentValidator;
	private final CommentService commentService;

	ReviewCommentController() {
		this(null, null, null, null);
	}

	@Autowired
	public ReviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			GenericCommentValidator genericCommentValidator) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.genericCommentValidator = genericCommentValidator;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if (applicationForm == null || currentUser.isInRole(Authority.APPLICANT) || currentUser.isRefereeOfApplicationForm(applicationForm) || !currentUser.canSee(applicationForm)){
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
	public Comment getComment(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setUser(userService.getCurrentUser());
		return comment;
	}

	@InitBinder(value = "comment")
	public void registerBinders(WebDataBinder binder) {
		binder.setValidator(genericCommentValidator);

	}

	@RequestMapping(method = RequestMethod.POST)
	public String addComment(@Valid @ModelAttribute("comment") Comment comment, BindingResult result) {
		if (result.hasErrors()) {
			return REVIEW_FEEDBACK_PAGE;
		}
		commentService.save(comment);
		return "redirect:/reviewFeedback?applicationId=" + comment.getApplication().getId();
	}
}
