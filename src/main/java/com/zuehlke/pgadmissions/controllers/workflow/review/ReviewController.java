package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

public abstract class ReviewController {
	protected static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
	protected static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;
	protected final ReviewService reviewService;

	ReviewController() {
		this(null, null, null);
	}

	@Autowired
	public ReviewController(ApplicationsService applicationsService, UserService userService, ReviewService reviewService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.reviewService = reviewService;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null) {
			throw new MissingApplicationFormException(applicationId);
		}
		if (!userService.getCurrentUser().hasAdminRightsOnApplication(application)) {
			throw new InsufficientApplicationFormPrivilegesException(applicationId);
		}
		return application;
	}

	@ModelAttribute("actionsDefinition")
	public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
		ApplicationForm application = getApplicationForm(applicationId);
		return applicationsService.calculateActions(getUser(), application);
	}

	public abstract ReviewRound getReviewRound(@RequestParam Object id);

	@ModelAttribute("nominatedSupervisors")
	public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
		List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (applicationForm.getLatestReviewRound() == null) {
			nominatedSupervisors.addAll(getOrCreateRegisteredUsersForForm(applicationForm));
		}
		return nominatedSupervisors;
	}

	@ModelAttribute("programmeReviewers")
	public List<RegisteredUser> getProgrammeReviewers(@RequestParam String applicationId) {
		List<RegisteredUser> programReviewers = getApplicationForm(applicationId).getProgram().getProgramReviewers();
		programReviewers.removeAll(getNominatedSupervisors(applicationId));
		return programReviewers;
	}

	@ModelAttribute("previousReviewers")
	public List<RegisteredUser> getPreviousReviewers(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousReviewersOfProgram = userService.getAllPreviousReviewersOfProgram(applicationForm.getProgram());
		previousReviewersOfProgram.removeAll(getNominatedSupervisors(applicationId));
		previousReviewersOfProgram.removeAll(getProgrammeReviewers(applicationId));
		return previousReviewersOfProgram;
	}

	protected List<RegisteredUser> getOrCreateRegisteredUsersForForm(ApplicationForm applicationForm) {
		List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
		List<SuggestedSupervisor> suggestedSupervisors = applicationForm.getProgrammeDetails().getSuggestedSupervisors();
		for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
			nominatedSupervisors.add(findOrCreateRegisterUserFromSuggestedSupervisorForForm(suggestedSupervisor, applicationForm));
		}
		return nominatedSupervisors;
	}

	private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
		String supervisorEmail = suggestedSupervisor.getEmail();
		RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
		if (possibleUser == null) {
			possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail,
			                null, applicationForm, Authority.REVIEWER);
		}
		return possibleUser;
	}

	private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
		for (RegisteredUser entry : users) {
			if (entry.getId().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}
}
