package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.TimelineEntity;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentTimelineController {

	private static final String COMMENTS_VIEW = "private/staff/admin/comment/timeline";

	private final UserService userService;
	private final ApplicationsService applicationService;

	CommentTimelineController() {
		this(null, null);
	}

	@Autowired
	public CommentTimelineController(ApplicationsService applicationsService, UserService userService) {
		this.applicationService = applicationsService;
		this.userService = userService;

	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer id) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		if (applicationForm == null || currentUser.isInRole(Authority.APPLICANT) || !currentUser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}


	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String getCommentsView() {
		return COMMENTS_VIEW;
	}


	@ModelAttribute("timelineEntities")
	public List<TimelineEntity> getSortedTimelineList(@RequestParam Integer id) {
		List<TimelineEntity> timelineList = new ArrayList<TimelineEntity>();
		timelineList.addAll(getApplicationForm(id).getVisibleComments(userService.getCurrentUser()));
		timelineList.addAll(getApplicationForm(id).getEvents());
		Collections.sort(timelineList);
		return timelineList;
	}

}
