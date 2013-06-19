package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationSummaryService;
import com.zuehlke.pgadmissions.services.ApplicationsFilteringService;
import com.zuehlke.pgadmissions.services.ApplicationsReportService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "", "applications" })
@SessionAttributes("filtering")
public class ApplicationListController {

    private static final String APPLICATION_LIST_PAGE_VIEW_NAME = "private/my_applications_page";
    private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";

    private final ApplicationsService applicationsService;
    
    private final ApplicationsReportService applicationsReportService;

    private final UserService userService;

    private final ApplicationsFiltersPropertyEditor filtersPropertyEditor;

    private final ApplicationSummaryService applicationSummaryService;

    private final ApplicationsFilteringService filteringService;
    
    private final ApplicationFormAccessService accessService;
    
    private final ActionsProvider actionsProvider;

    public ApplicationListController() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ApplicationListController(ApplicationsService applicationsService, ApplicationsReportService applicationsReportService, UserService userService,
            ApplicationsFiltersPropertyEditor filtersPropertyEditor, final ApplicationSummaryService applicationSummaryService,
            ApplicationsFilteringService filteringService, final ApplicationFormAccessService accessService, final ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.applicationsReportService = applicationsReportService;
        this.userService = userService;
        this.filtersPropertyEditor = filtersPropertyEditor;
        this.applicationSummaryService = applicationSummaryService;
        this.filteringService = filteringService;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
    }

    @InitBinder(value = "filtering")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "filters", filtersPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getApplicationListPage(@RequestParam(required = false) String applyFilters, ModelMap model, HttpSession session) {
        Object alertDefinition = session.getAttribute("alertDefinition");
        if (alertDefinition != null) {
            model.addAttribute("alertDefinition", alertDefinition);
            session.removeAttribute("alertDefinition");
        }

        ApplicationsFiltering filtering = (ApplicationsFiltering) model.get("filtering");

        if (applyFilters != null) { // custom apply filters action
            if ("reload".equals(applyFilters)) {
                filtering = filteringService.getStoredOrDefaultFiltering(getUser());
            } else if ("my".equals(applyFilters)) {
                filtering = new ApplicationsFiltering();
                filtering.setPreFilter(ApplicationsPreFilter.MY);
            } else if ("urgent".equals(applyFilters)) {
                filtering = new ApplicationsFiltering();
                filtering.setPreFilter(ApplicationsPreFilter.URGENT);
            } 
        }
        
        if (filtering == null) {
            filtering = filteringService.getStoredOrDefaultFiltering(getUser());
        }
        
        model.addAttribute("filtering", filtering);
        return APPLICATION_LIST_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public String getApplicationListSection(final @ModelAttribute("filtering") ApplicationsFiltering filtering, @RequestParam Boolean useDisjunction, final ModelMap model) {
        RegisteredUser user = getUser();
        filtering.setUseDisjunction(useDisjunction);
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, filtering);
        Map<String, Boolean> updatedApplications = new HashMap<String, Boolean>();
        Map<String, ActionsDefinitions> actionDefinitions = new LinkedHashMap<String, ActionsDefinitions>();
        for (ApplicationForm applicationForm : applications) {
            updatedApplications.put(applicationForm.getApplicationNumber(), accessService.userNeedsToSeeApplicationUpdates(applicationForm, getUser()));
            ActionsDefinitions actionsDefinition = actionsProvider.calculateActions(user, applicationForm);
            actionDefinitions.put(applicationForm.getApplicationNumber(), actionsDefinition);
        }
        model.addAttribute("updateApplications", updatedApplications);
        model.addAttribute("applications", applications);
        model.addAttribute("actionDefinitions", actionDefinitions);
        model.addAttribute("latestConsideredFlagIndex", filtering.getLatestConsideredFlagIndex());
        return APPLICATION_LIST_SECTION_VIEW_NAME;
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public void getApplicationsReport(@ModelAttribute("filtering") ApplicationsFiltering filtering, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        DataTable reportTable = applicationsReportService.getApplicationsReport(getUser(), filtering);
        DataSourceRequest dsRequest;
        try {
            dsRequest = new DataSourceRequest(req);
            DataSourceHelper.setServletResponse(reportTable, dsRequest, resp);
        } catch (DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/saveFilters", method = RequestMethod.POST)
    @ResponseBody
    public String saveFiltersAsDefault(@ModelAttribute("filtering") ApplicationsFiltering filtering, @RequestParam Boolean useDisjunction) {
        filtering.setUseDisjunction(useDisjunction);
        userService.setFiltering(getUser(), filtering);
        return "OK";
    }

    @RequestMapping(value = "/getApplicationDetails", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getApplicationDetails(@RequestParam String applicationId) {
        return applicationSummaryService.getSummary(applicationId);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("searchCategories")
    public SearchCategory[] getSearchCategories() {
        return SearchCategory.values();
    }

    @ModelAttribute("searchPredicatesMap")
    public String getSearchPredicatesMap() {
        Map<SearchCategory, List<Map<String, String>>> predicatesMap = Maps.newLinkedHashMap();
        for (SearchCategory searchCategory : SearchCategory.values()) {
            List<Map<String, String>> availablePredicates = Lists.newLinkedList();
            for (SearchPredicate predicate : searchCategory.getAvailablePredicates()) {
                Map<String, String> availablePredicate = Maps.newLinkedHashMap();
                availablePredicate.put("name", predicate.name());
                availablePredicate.put("displayName", predicate.displayValue());
                availablePredicates.add(availablePredicate);
            }
            predicatesMap.put(searchCategory, availablePredicates);
        }
        return new Gson().toJson(predicatesMap);
    }

    @ModelAttribute("applicationStatusValues")
    public List<ApplicationFormStatus> getApplicationStatusValues() {
        List<ApplicationFormStatus> statuses = Lists.newArrayListWithCapacity(ApplicationFormStatus.values().length);
        for (ApplicationFormStatus status : ApplicationFormStatus.values()) {
            if (status != ApplicationFormStatus.UNSUBMITTED && status != ApplicationFormStatus.REQUEST_RESTART_APPROVAL) {
                statuses.add(status);
            }
        }
        return statuses;
    }
    
    @ModelAttribute("applications")
    public List<ApplicationForm> getApplications() {
        return java.util.Collections.emptyList();
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) boolean submissionSuccess, @RequestParam(required = false) String decision,
            @RequestParam(required = false) String message) {
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
