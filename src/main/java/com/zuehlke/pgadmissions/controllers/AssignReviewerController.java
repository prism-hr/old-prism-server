package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/assignReviewers")
public class AssignReviewerController {
	private static final String ASSIGN_REVIEWERS_TO_APPLICATION_VIEW = "private/staff/admin/assign_reviewers_to_appl_page";
	private static final String NEW_REVIEWER_JSON = "private/staff/admin/reviewer_as_JSON";

	private final ApplicationsService applicationService;
	private final ReviewService reviewService;
	private final UserService userService;

	AssignReviewerController() {
		this(null, null, null);
	}

	@Autowired
	public AssignReviewerController(ApplicationsService applicationServiceMock, ReviewService reviewService, UserService userService) {
		this.applicationService = applicationServiceMock;
		this.reviewService = reviewService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getAssignReviewerPage() {
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@RequestMapping(value = "/moveApplicationToReview", method = RequestMethod.POST)
	public String moveApplicationToReviewState(@ModelAttribute ApplicationForm application, // 
			@RequestParam("reviewerIds[]") Integer[] reviewerIds) {

		checkApplicationStatus(application);
		checkAdminPermission(application.getProgram());
		if (reviewerIds == null || reviewerIds.length == 0) {
			throw new ResourceNotFoundException();
		}
		RegisteredUser[] users = new RegisteredUser[reviewerIds.length];
		for (int i = 0; i < reviewerIds.length; i++) {
			users[i] = userService.getUser(reviewerIds[i]);
		}
		try {
			reviewService.moveApplicationToReview(application, users);
		} catch (Exception e) {
			throw new ResourceNotFoundException(e.getMessage());
		}
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public String createReviewer(@ModelAttribute("programme") Program programme, @Valid RegisteredUser uiReviewer, ModelMap modelMap) {
		checkAdminPermission(programme);

		RegisteredUser reviewer = userService.getUserByEmail(uiReviewer.getEmail());
		if (programme.getReviewers().contains(reviewer)) {
			modelMap.put("message", String.format("User '%s' (e-mail: %s) is already a reviewer for this programme.",// 
					reviewer.getUsername(), reviewer.getEmail()));
			return NEW_REVIEWER_JSON;
		}
		if (reviewer == null) {
			reviewer = reviewService.createNewReviewerForProgramme(programme,// 
					uiReviewer.getFirstName(), uiReviewer.getLastName(), uiReviewer.getEmail());
			modelMap.put("message", String.format("Created user '%s' (e-mail: %s) and added as a reviewer for this programme.",//
					reviewer.getUsername(), reviewer.getEmail()));
		} else {
			reviewService.addUserToProgramme(programme, reviewer);
			modelMap.put("message", String.format("User '%s' (e-mail: %s) added as reviewer for this programme.",//
					reviewer.getUsername(), reviewer.getEmail()));
		}

		modelMap.put("newReviewer", reviewer);
		return NEW_REVIEWER_JSON;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		return application;
	}

	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@ModelAttribute("applicationForm") ApplicationForm application) {
		checkPermissionForApplication(application);
		return application.getProgram();
	}

	@ModelAttribute("availableReviewers")
	public List<RegisteredUser> getAvailableReviewers(//
			@ModelAttribute("programme") Program program,//
			@ModelAttribute("applicationForm") ApplicationForm application) {

		checkPermissionForApplication(application);
		List<RegisteredUser> programmeReviewers = program.getReviewers();
		programmeReviewers.removeAll(application.getReviewers());
		return programmeReviewers;
	}

	@ModelAttribute("applicationReviewers")
	public List<RegisteredUser> getApplicationReviewers(@ModelAttribute("applicationForm") ApplicationForm application) {
		checkPermissionForApplication(application);
		return application.getReviewers();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	private void checkPermissionForApplication(ApplicationForm application) {
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
	}

	private void checkAdminPermission(Program programme) {
		RegisteredUser currentUser = getCurrentUser();
		if (!(programme.getAdministrators().contains(currentUser) || //
				currentUser.isInRole(Authority.SUPERADMINISTRATOR) || //
		programme.getReviewers().contains(currentUser))) {
			throw new ResourceNotFoundException();
		}
	}

	private void checkApplicationStatus(ApplicationForm application) {
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
	}
}
