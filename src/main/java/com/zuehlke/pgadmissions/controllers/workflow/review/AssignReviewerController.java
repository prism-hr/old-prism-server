package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public abstract class AssignReviewerController {
	protected static final String ASSIGN_REVIEWERS_TO_APPLICATION_VIEW = "private/staff/admin/assign_reviewers_to_appl_page";
	protected static final String ASSIGN_REVIEWERS_TO_APPLICATION_SECTION = "private/staff/admin/assign_reviewers_to_appl_section";
	protected static final String NEXT_VIEW = "redirect:/applications";

	protected final ApplicationsService applicationService;
	protected final ReviewService reviewService;
	protected final UserService userService;
	protected final MessageSource messageSource;

	protected final NewUserByAdminValidator userValidator;

	AssignReviewerController() {
		this(null, null, null, null, null);
	}


	public AssignReviewerController(ApplicationsService applicationServiceMock, ReviewService reviewService,//
			UserService userService, NewUserByAdminValidator validator, MessageSource msgSource) {
		this.applicationService = applicationServiceMock;
		this.reviewService = reviewService;
		this.userService = userService;
		this.userValidator = validator;
		messageSource = msgSource;
	}



	@InitBinder(value = "uiReviewer")
	public void registerValidators(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	

	@ModelAttribute("uiReviewer")
	public RegisteredUser getUiReviewer() {
		return new RegisteredUser();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		return application;
	}

	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@RequestParam Integer applicationId) {
		return getApplicationForm(applicationId).getProgram();
	}

	@ModelAttribute("availableReviewers")
	public List<RegisteredUser> getAvailableReviewers(@RequestParam Integer applicationId, String unsavedReviewersRaw) {

		ApplicationForm application = getApplicationForm(applicationId);
		Program program = application.getProgram();
		List<RegisteredUser> availableReviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeReviewers = program.getProgramReviewers();
		for (RegisteredUser registeredUser : programmeReviewers) {
			if (!registeredUser.isReviewerInLatestReviewRoundOfApplicationForm(application)) {
				availableReviewers.add(registeredUser);
			}
		}
		List<RegisteredUser> unsavedReviewers = unsavedReviewers(unsavedReviewersRaw);
		if (unsavedReviewers != null) {
			availableReviewers.removeAll(unsavedReviewers);
		}
		return availableReviewers;
	}

	@ModelAttribute("applicationReviewers")
	public Set<RegisteredUser> getApplicationReviewers(@RequestParam Integer applicationId) {

		ApplicationForm application = getApplicationForm(applicationId);

		Set<RegisteredUser> existingReviewers = new HashSet<RegisteredUser>();
		ReviewRound latestReviewRound = application.getLatestReviewRound();
		if (latestReviewRound == null) {
			return existingReviewers;
		}
		for (Reviewer reviewer : latestReviewRound.getReviewers()) {
			existingReviewers.add(reviewer.getUser());
		}
		return existingReviewers;
	}

	@ModelAttribute("unsavedReviewers")
	public List<RegisteredUser> unsavedReviewers(String unsavedReviewersRaw) {
		List<RegisteredUser> retval = new ArrayList<RegisteredUser>();
		if (unsavedReviewersRaw == null || unsavedReviewersRaw.isEmpty()) {
			return retval;
		}
		String[] tokens = unsavedReviewersRaw.split("\\|");
		for (String idStr : tokens) {
			retval.add(userService.getUser(Integer.parseInt(idStr)));
		}
		return retval;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	public abstract ReviewRound getReviewRound(Integer applicationId);

	protected RegisteredUser getCurrentUser() {
		return userService.getCurrentUser(); 
	}

	protected void checkPermissionForApplication(ApplicationForm application) {
		if (application == null || !getCurrentUser().canSee(application)) {

			throw new ResourceNotFoundException();
		}
	}

	protected void checkAdminPermission(Program programme) {
		RegisteredUser currentUser = getCurrentUser();
		if (!(programme.getAdministrators().contains(currentUser) || 
				currentUser.isInRole(Authority.SUPERADMINISTRATOR) || 
		programme.getProgramReviewers().contains(currentUser))) {

			throw new ResourceNotFoundException();
		}
	}

	protected void checkApplicationStatus(ApplicationForm application) {
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
	}

	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}
}
