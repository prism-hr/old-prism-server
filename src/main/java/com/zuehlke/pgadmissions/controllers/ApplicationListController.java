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
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "", "applications" })
public class ApplicationListController {

	private static final String APPLICATION_LIST_PAGE_VIEW_NAME = "private/my_applications_page";
	private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";
	private final ApplicationsService applicationsService;
	private final UserService userService;

	ApplicationListController() {
		this(null, null);
	}

	@Autowired
	public ApplicationListController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getApplicationListPage() {
		return APPLICATION_LIST_PAGE_VIEW_NAME;
	}

	@RequestMapping(value = "/section", method = RequestMethod.GET)
	public String getApplicationListSection() {
		return APPLICATION_LIST_SECTION_VIEW_NAME;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("searchCategories")
	public SearchCategory[] getSearchCategories() {
		return SearchCategory.values();
	}

	@ModelAttribute("applications")
	public List<ApplicationForm> getApplications(//
			@RequestParam(required = false) SearchCategory searchCategory, @RequestParam(required = false) String searchTerm, //
			@RequestParam(required = false) SortCategory sortCategory, @RequestParam(required = false) SortOrder order,//
			@RequestParam(required = false) Integer blockCount) {

		int blockIx = blockCount == null ? 1 : blockCount;
		return applicationsService.getAllVisibleAndMatchedApplications(getUser(), searchCategory, searchTerm, sortCategory, order, blockIx);
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required = false) boolean submissionSuccess, @RequestParam(required = false) String decision, @RequestParam(required = false) String message) {
		if (submissionSuccess) {
			return "Your application has been successfully submitted.";
		}
		if (decision != null) {
			return "The application was successfully " + decision + ".";
		}
		if (message != null) {
			return message;
		}
		return null;
	}
}
