package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comment" })
public class GenericCommentController {

	private static final String GENERIC_COMMENT_PAGE = "private/staff/admin/comment/genericcomment";
	private final ApplicationsService applicationsService;
	private final UserService userService;

	GenericCommentController() {
		this(null, null);
	}

	@Autowired
	public GenericCommentController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer id) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationById(id);
		if (applicationForm == null || currentUser.isInRole(Authority.APPLICANT) || !currentUser.canSee(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	
	
	@RequestMapping(method = RequestMethod.GET)
	public String getGenericCommentPage() {
		return GENERIC_COMMENT_PAGE;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
}
