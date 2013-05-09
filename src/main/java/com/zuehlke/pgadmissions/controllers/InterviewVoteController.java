package com.zuehlke.pgadmissions.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/interviewVote" })
public class InterviewVoteController {

	private static final String INTERVIEW_VOTE_PAGE = "private/staff/interviewers/interview_vote";
	private final ApplicationsService applicationsService;
	private final UserService userService;

	public InterviewVoteController() {
		this(null, null);
	}

	@Autowired
	public InterviewVoteController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null) {
			throw new MissingApplicationFormException(applicationId);
		}
		Interview interview = applicationForm.getLatestInterview();
		if (!interview.isParticipant(currentUser) || !currentUser.canSee(applicationForm)) {
			throw new InsufficientApplicationFormPrivilegesException(applicationId);
		}
		if (applicationForm.isDecided() || applicationForm.isWithdrawn()) {
			throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
		}
		return applicationForm;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("actionsDefinition")
	public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId) {
		ApplicationForm application = getApplicationForm(applicationId);
		return applicationsService.getActionsDefinition(userService.getCurrentUser(), application);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getInterviewVotePage() {
		return INTERVIEW_VOTE_PAGE;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitInterviewVotes(@ModelAttribute ApplicationForm applicationForm, @RequestParam List<Integer> acceptedTimeslotIds) {

		List<InterviewParticipant> participants = applicationForm.getLatestInterview().getParticipants();
		RegisteredUser currentUser = getUser();
		InterviewParticipant currentParticipant = null;
		for (InterviewParticipant interviewParticipant : participants) {
			if (interviewParticipant.getUser().getId().equals(currentUser.getId())) {
				currentParticipant = interviewParticipant;
			}
		}

		Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();
		List<InterviewTimeslot> timeslots = applicationForm.getLatestInterview().getTimeslots();
		for (InterviewTimeslot interviewTimeslot : timeslots) {
			if (acceptedTimeslotIds.contains(interviewTimeslot.getId())) {
				acceptedTimeslots.add(interviewTimeslot);
			}
		}

		if (currentParticipant != null) {
			currentParticipant.setAcceptedTimeslots(acceptedTimeslots);
		} else {
			throw new RuntimeException("Unable to find interview participant corresponding to current user: " + currentUser.getUsername());
		}

		return "redirect:/applications?messageCode=interview.vote.feedback&application=" + applicationForm.getApplicationNumber();
	}

}
