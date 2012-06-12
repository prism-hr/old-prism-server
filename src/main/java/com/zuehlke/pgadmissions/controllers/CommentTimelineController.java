package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.TimelineObject;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.TimelineService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentTimelineController {

	private static final String COMMENTS_VIEW = "private/staff/admin/comment/timeline";

	private final UserService userService;
	private final ApplicationsService applicationService;
	private final TimelineService timelineService;

	CommentTimelineController() {
		this(null, null, null);
	}

	@Autowired
	public CommentTimelineController(ApplicationsService applicationsService, UserService userService, TimelineService timelineService) {
		this.applicationService = applicationsService;
		this.userService = userService;
		this.timelineService = timelineService;

	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String id) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(id);
		if (applicationForm == null || currentUser.isInRole(Authority.APPLICANT) || !currentUser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}


	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String getCommentsView() {
		return COMMENTS_VIEW;
	}
	
	@ModelAttribute("timelineObjects")
	public List<TimelineObject> getTimelineObjects(@RequestParam String id) {
		List<TimelineObject> timelineObjects = new ArrayList<TimelineObject>();
		timelineObjects.addAll(timelineService.getPhases(getApplicationForm(id)));		
		return timelineObjects;
	}

	@ModelAttribute("validationQuestionOptions")
	public ValidationQuestionOptions[] getValidationQuestionOptions() {
		return ValidationQuestionOptions.values();
	}
	
	@ModelAttribute("homeOrOverseasOptions")
	public HomeOrOverseas[] getHomeOrOverseasOptions() {
		return HomeOrOverseas.values();
	}




}
