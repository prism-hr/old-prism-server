package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationSearchDTO;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "", "applications" })
public class ApplicationListController {

	private static final String APPLICATION_LIST_PAGE_VIEW_NAME = "private/my_applications_page";
	private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";
	
	private final ApplicationsService applicationsService;
	
	private final UserService userService;
	
	private final ApplicationsFiltersPropertyEditor filtersPropertyEditor;

	ApplicationListController() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationListController(ApplicationsService applicationsService, UserService userService, ApplicationsFiltersPropertyEditor filtersPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.filtersPropertyEditor = filtersPropertyEditor;
	}

    @RequestMapping(method = RequestMethod.GET)
    public String getApplicationListPage(Model model, HttpSession session) {
        Object alertDefinition = session.getAttribute("alertDefinition");
        if (alertDefinition != null) {
            model.addAttribute("alertDefinition", alertDefinition);
            session.removeAttribute("alertDefinition");
        }

        List<ApplicationsFilter> applicationsFilters = getUser().getApplicationsFilters();
        model.addAttribute("filters", applicationsFilters);
        return APPLICATION_LIST_PAGE_VIEW_NAME;
    }
    
    @InitBinder(value = "applicationSeachDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "filters", filtersPropertyEditor);
    }
    
	@RequestMapping(value = "/section", method = RequestMethod.GET)
	public String getApplicationListSection(@ModelAttribute("applicationSeachDTO") ApplicationSearchDTO dto, 
			Model model) {
	    
		List<ApplicationForm> applications = getApplications(dto.getFilters(), dto.getSortCategory(), dto.getOrder(),
				dto.getBlockCount());
		model.addAttribute("applications", applications);
		return APPLICATION_LIST_SECTION_VIEW_NAME;
	}
	
	@RequestMapping(value = "/saveFilters", method = RequestMethod.POST)
	@ResponseBody
	public String saveFiltersAsDefault(@ModelAttribute("applicationSeachDTO") ApplicationSearchDTO dto){
	    RegisteredUser user = getUser();
        userService.clearApplicationsFilters(user);
	    for(ApplicationsFilter filter : dto.getFilters()){
	        userService.addFilter(user, filter);
	    }
	    return "OK";
	}

	public List<ApplicationForm> getApplications(List<ApplicationsFilter> filters,
			SortCategory sortCategory, SortOrder sortOrder, Integer blockCount) {
		return applicationsService.getAllVisibleAndMatchedApplications(getUser(), filters, sortCategory, sortOrder, blockCount);
	}

	public List<ApplicationForm> getApplications(List<ApplicationsFilter> filters) {
		return getApplications(filters, null, null, null);
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
