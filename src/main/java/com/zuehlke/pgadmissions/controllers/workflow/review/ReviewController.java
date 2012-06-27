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
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public abstract class ReviewController {
	protected static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/admin/assign_reviewers_to_appl_page";
	protected static final String REVIEWERS_SECTION_NAME = "/private/staff/admin/assign_reviewers_section";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;
	protected final NewUserByAdminValidator reviewerValidator;
	protected final ReviewService reviewService;
	protected final MessageSource messageSource;
	protected final ReviewerPropertyEditor reviewerPropertyEditor;
	protected final ReviewRoundValidator reviewRoundValidator;
	protected final EncryptionHelper encryptionHelper;

	ReviewController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ReviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator reviewerValidator,
			ReviewRoundValidator reviewRoundValidator, ReviewService reviewService, MessageSource messageSource, ReviewerPropertyEditor reviewerPropertyEditor,
			EncryptionHelper encryptionHelper) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.reviewerValidator = reviewerValidator;
		this.reviewRoundValidator = reviewRoundValidator;

		this.reviewService = reviewService;
		this.messageSource = messageSource;
		this.reviewerPropertyEditor = reviewerPropertyEditor;
		this.encryptionHelper = encryptionHelper;

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
	public List<RegisteredUser> getProgrammeReviewers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingReviewer) {
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
	public Set<RegisteredUser> getApplicationReviewersAsUsers(@RequestParam String applicationId) {
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
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null
				|| (!userService.getCurrentUser().hasAdminRightsOnApplication(application) && !userService.getCurrentUser()
						.isReviewerInLatestReviewRoundOfApplicationForm(application))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	public abstract ReviewRound getReviewRound(@RequestParam Object id);

	@ModelAttribute("pendingReviewers")
	public List<RegisteredUser> getPendingReviewers(@RequestParam(value="pendingReviewer",required = false) List<String> encryptedPendingReviewerIds, @RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> newUsers = new ArrayList<RegisteredUser>();
		if (encryptedPendingReviewerIds != null) {
			for (String encryptedId : encryptedPendingReviewerIds) {
				RegisteredUser user = userService.getUser(encryptionHelper.decryptToInteger(encryptedId));
				if (!user.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)) {
					newUsers.add(user);
				}
			}
		}

		return newUsers;
	}

	@ModelAttribute("previousReviewers")
	public List<RegisteredUser> getPreviousReviewers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingReviewer) {
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
