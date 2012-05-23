package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public abstract class ReviewController {
	protected static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/admin/assign_reviewers_to_appl_page";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;
	protected final NewUserByAdminValidator reviewerValidator;
	protected final ReviewService reviewService;
	protected final MessageSource messageSource;
	protected final ReviewerPropertyEditor reviewerPropertyEditor;
	protected final ReviewRoundValidator reviewRoundValidator;
	
	ReviewController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public ReviewController(ApplicationsService applicationsService, UserService userService,NewUserByAdminValidator reviewerValidator, ReviewRoundValidator reviewRoundValidator, ReviewService reviewService, MessageSource messageSource, ReviewerPropertyEditor reviewerPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.reviewerValidator = reviewerValidator;
		this.reviewRoundValidator = reviewRoundValidator;
		
		this.reviewService = reviewService;
		this.messageSource = messageSource;
		this.reviewerPropertyEditor = reviewerPropertyEditor;
		
	}

	@InitBinder(value = "reviewer")
	public void registerReviewerValidators(WebDataBinder binder) {
		binder.setValidator(reviewerValidator);
		
	}
	
	@InitBinder(value = "reviewRound")
	public void registerReviewRoundValidator(WebDataBinder binder) {
		binder.setValidator(reviewRoundValidator);
		binder.registerCustomEditor(Reviewer.class, reviewerPropertyEditor);
	}

	@ModelAttribute("reviewer")
	public RegisteredUser getReviewer() {
		return new RegisteredUser();
	}

	@ModelAttribute("programmeReviewers")
	public List<RegisteredUser> getProgrammeReviewers(@RequestParam Integer applicationId, @RequestParam(required = false) List<Integer> pendingReviewer) {
		ApplicationForm application = getApplicationForm(applicationId);
		Program program = application.getProgram();
		List<RegisteredUser> availableReviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeReviewers = program.getProgramReviewers();
		for (RegisteredUser registeredUser : programmeReviewers) {
			if (!registeredUser.isReviewerInLatestReviewRoundOfApplicationForm(application)) {
				availableReviewers.add(registeredUser);
			}
		}
		for (RegisteredUser registeredUser : getPendingReviewers(pendingReviewer, applicationId)) {
			if (availableReviewers.contains(registeredUser)) {
				availableReviewers.remove(registeredUser);
			}
		}

		return availableReviewers;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("applicationReviewers")
	public Set<RegisteredUser> getApplicationReviewersAsUsers(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Set<RegisteredUser> existingReviewers = new HashSet<RegisteredUser>();
		ReviewRound latestReviewRound = applicationForm.getLatestReviewRound();
		if (latestReviewRound != null) {
			for (Reviewer reviewer : latestReviewRound.getReviewers()) {
				existingReviewers.add(reviewer.getUser());
			}
		}
		return existingReviewers;
	}

	protected String getCreateReviewerMessage(String code, RegisteredUser user) {
		return getMessage(code, new Object[] { user.getFirstName() + " " + user.getLastName(), user.getEmail() });
	}

	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {

		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null
				|| (!userService.getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram()) && !userService.getCurrentUser()
						.isReviewerInLatestReviewRoundOfApplicationForm(application))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	public abstract ReviewRound getReviewRound(@RequestParam Integer applicationId);

	@ModelAttribute("pendingReviewers")
	public List<RegisteredUser> getPendingReviewers(@RequestParam(required = false) List<Integer> pendingReviewer, @RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> newUsers = new ArrayList<RegisteredUser>();
		if (pendingReviewer != null) {
			for (Integer id : pendingReviewer) {
				RegisteredUser user = userService.getUser(id);
				if (!user.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)) {
					newUsers.add(user);
				}
			}
		}

		return newUsers;
	}

	@ModelAttribute("previousReviewers")
	public List<RegisteredUser> getPreviousReviewers(@RequestParam Integer applicationId, @RequestParam(required = false) List<Integer> pendingReviewer) {
		List<RegisteredUser> availablePreviousReviewers = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousReviewersOfProgram = userService.getAllPreviousReviewersOfProgram(applicationForm.getProgram());

		List<RegisteredUser> pendingReviewers = getPendingReviewers(pendingReviewer, applicationId);

		for (RegisteredUser registeredUser : previousReviewersOfProgram) {
			if (!registeredUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm) && !pendingReviewers.contains(registeredUser)
					&& !applicationForm.getProgram().getProgramReviewers().contains(registeredUser)) {
				availablePreviousReviewers.add(registeredUser);
			}
		}

		return availablePreviousReviewers;
	}

	



}
