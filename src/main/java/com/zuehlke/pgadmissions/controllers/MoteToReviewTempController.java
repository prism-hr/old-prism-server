package com.zuehlke.pgadmissions.controllers;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/review")
public class MoteToReviewTempController {

	/*
	 * throwaway controller for moving to review until proper controller is
	 * ready
	 */
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final StageDurationDAO stageDurationDAO;

	MoteToReviewTempController() {
		this(null, null, null);
	}

	@Autowired
	public MoteToReviewTempController(ApplicationsService applicationsService, UserService userService, StageDurationDAO stageDurationDAO) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.stageDurationDAO = stageDurationDAO;

	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer application) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(application);
		if (applicationForm == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}

	@RequestMapping(method = RequestMethod.POST)
	public String moveToReview(@ModelAttribute ApplicationForm applicationForm) {
		applicationForm.setStatus(ApplicationFormStatus.REVIEW);
		//applicationForm.getReviewerUsers().addAll(applicationForm.getProgram().getReviewers());
		applicationForm.setDueDate(DateUtils.truncate(DateUtils.addDays(new Date(), stageDurationDAO.getByStatus(ApplicationFormStatus.REVIEW).getDurationInDays()), Calendar.DATE));
		applicationsService.save(applicationForm);
		return "redirect:/applications";

	}
}
