package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
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
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/assignReviewers")
public class AssignReviewerController {
	private static final String ASSIGN_REVIEWERS_TO_APPLICATION_VIEW = "/private/staff/admin/assign_reviewers_to_appl_page";
	private static final String NEW_REVIEWER_JSON = "/private/staff/admin/reviewer_as_JSON";

	private final ApplicationsService applicationService;
	private final ReviewService reviewService;
	private final UserService userService;

	private final Logger logger = Logger.getLogger(AssignReviewerController.class);

	AssignReviewerController() {
		this(null, null, null);
	}

	@Autowired
	public AssignReviewerController(ApplicationsService applicationServiceMock, ReviewService reviewService, UserService userService) {
		this.applicationService = applicationServiceMock;
		this.reviewService = reviewService;
		this.userService = userService;
	}

	@RequestMapping(value = "/showPage", method = RequestMethod.GET)
	public String getAssignReviewerPage() {
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@RequestMapping(value = "/moveApplicationToReview", method = RequestMethod.POST)
	public String moveApplicationToReviewState(@ModelAttribute ApplicationForm application, // 
			@RequestParam("reviewerIds[]") Integer[] reviewerIds) {

		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public String createReviewer(@ModelAttribute Program programme, @Valid RegisteredUser uiReviewer, ModelMap modelMap) {
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
	public ApplicationForm getApplicationForm(Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
		return application;
	}

	@ModelAttribute("programme")
	public Program getProgrammeForApplication(Integer applicationId) {
		return getProgramme(applicationId);
	}

	@ModelAttribute("availableReviewers")
	public List<RegisteredUser> getAvailableReviewers(Integer applicationId) {
		return getProgramme(applicationId).getReviewers();
	}

	@ModelAttribute("applicationReviewers")
	public List<RegisteredUser> getApplicationReviewers(Integer applicationId) {
		return getApplicationForm(applicationId).getReviewers();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	private Program getProgramme(Integer applicationId) {
		return getApplicationForm(applicationId).getProgram();
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
