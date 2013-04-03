package com.zuehlke.pgadmissions.controllers;

import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
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
    public String getApplicationListPage(Model model, HttpSession session) {
        Object alertDefinition = session.getAttribute("alertDefinition");
        if (alertDefinition != null) {
            model.addAttribute("alertDefinition", alertDefinition);
            session.removeAttribute("alertDefinition");
        }

        List<ApplicationsFilter> applicationsFilters = getUser().getApplicationsFilters();
        model.addAttribute("hasFilter", !applicationsFilters.isEmpty());
        if (!applicationsFilters.isEmpty()) {
            model.addAttribute("searchTerm", applicationsFilters.get(0).getSearchTerm());
            model.addAttribute("searchCategory", applicationsFilters.get(0).getSearchCategory());
        }
        return APPLICATION_LIST_PAGE_VIEW_NAME;
    }

	@RequestMapping(value = "/section", method = RequestMethod.GET)
	public String getApplicationListSection(@RequestParam(required = false) SearchCategory searchCategory,
			@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) SortCategory sortCategory, @RequestParam(required = false) SortOrder order,
			@RequestParam(required = false) Integer blockCount, @RequestParam(required = false) Boolean clear,
			Model model) {
		RegisteredUser user = getUser();
        List<ApplicationsFilter> applicationsFilters = user.getApplicationsFilters();
		if (isTrue(clear) && !applicationsFilters.isEmpty()) {
			userService.clearApplicationsFilter(applicationsFilters.get(0));
		} else {
			if (searchCategory != null && searchTerm != null && !searchTerm.isEmpty()) {
				createAndSaveFilter(searchCategory, searchTerm);
			} else if (!applicationsFilters.isEmpty()) {
				ApplicationsFilter filter = applicationsFilters.get(0);
				searchCategory = filter.getSearchCategory();
				searchTerm = filter.getSearchTerm();
			}
		}
		List<ApplicationForm> applications = getApplications(searchCategory, searchTerm, sortCategory, order,
				blockCount);
		Map<String, ApplicationActionsDefinition> actionDefinitions = new LinkedHashMap<String, ApplicationActionsDefinition>();
		for (ApplicationForm applicationForm : applications) {
            ApplicationActionsDefinition actionsDefinition = applicationsService.getActionsDefinition(user, applicationForm);
            actionDefinitions.put(applicationForm.getApplicationNumber(), actionsDefinition);
        }
		
		model.addAttribute("applications", applications);
		model.addAttribute("actionDefinitions", actionDefinitions);
		return APPLICATION_LIST_SECTION_VIEW_NAME;
	}

	private void createAndSaveFilter(SearchCategory searchCategory, String searchTerm) {
		ApplicationsFilter filter = new ApplicationsFilter();
		filter.setSearchCategory(searchCategory);
		filter.setSearchTerm(searchTerm);
		userService.saveFilter(getUser(), filter);
	}

	public List<ApplicationForm> getApplications(SearchCategory searchCategory, String searchTerm,
			SortCategory sortCategory, SortOrder sortOrder, Integer blockCount) {
		return applicationsService.getAllVisibleAndMatchedApplications(getUser(), searchCategory,
				StringUtils.trim(searchTerm), sortCategory, sortOrder, blockCount);
	}

	public List<ApplicationForm> getApplications(ApplicationsFilter filter) {
		return getApplications(filter.getSearchCategory(), filter.getSearchTerm(), null, null, null);
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
	public List<ApplicationForm> getApplications() {
		return java.util.Collections.emptyList();
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required = false) boolean submissionSuccess,
			@RequestParam(required = false) String decision, @RequestParam(required = false) String message) {
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

	@ModelAttribute("messageApplication")
	public ApplicationForm getApplicationForm(@RequestParam(required = false) String application) {
		return applicationsService.getApplicationByApplicationNumber(application);
	}
}
