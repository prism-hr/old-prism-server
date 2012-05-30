package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SearchCategories;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = "/search")
public class SearchController {

	private final ApplicationsService applicationsService;
	private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";
	private final UserService userService;
	
	
	SearchController(){
		this(null, null);
	}
	
	@Autowired
	public SearchController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getApplicationsContainingTermInCategory() {
		return APPLICATION_LIST_SECTION_VIEW_NAME;
	}
	
	@ModelAttribute("applications")
	public List<ApplicationForm> getApplications(@RequestParam String searchTerm, @RequestParam SearchCategories searchCategory) {
		return applicationsService.getAllVisibleAndMatchedApplications(searchTerm, searchCategory, getUser());
	}
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
	
	
}
