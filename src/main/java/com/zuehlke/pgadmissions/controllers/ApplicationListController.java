package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.dto.ApplicationSearchDTO;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsReportService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "", "applications" })
@SessionAttributes("applicationSearchDTO")
public class ApplicationListController {

    private static final String APPLICATION_LIST_PAGE_VIEW_NAME = "private/my_applications_page";
    private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";

    private final ApplicationsService applicationsService;

    private final ApplicationsReportService applicationsReportService;

    private final UserService userService;

    private final ApplicationsFiltersPropertyEditor filtersPropertyEditor;

    ApplicationListController() {
        this(null, null, null, null);
    }

    @Autowired
    public ApplicationListController(ApplicationsService applicationsService, ApplicationsReportService applicationsReportService, UserService userService,
            ApplicationsFiltersPropertyEditor filtersPropertyEditor) {
        this.applicationsService = applicationsService;
        this.applicationsReportService = applicationsReportService;
        this.userService = userService;
        this.filtersPropertyEditor = filtersPropertyEditor;
    }

    @InitBinder(value = "applicationSearchDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "filters", filtersPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getApplicationListPage(boolean reloadFilters, Model model, HttpSession session) {
        Object alertDefinition = session.getAttribute("alertDefinition");
        if (alertDefinition != null) {
            model.addAttribute("alertDefinition", alertDefinition);
            session.removeAttribute("alertDefinition");
        }

        List<ApplicationsFilter> applicationsFilters;
        ApplicationSearchDTO applicationSearchDTO = (ApplicationSearchDTO) model.asMap().get("applicationSearchDTO");
        if (applicationSearchDTO.getFilters() == null || reloadFilters) {
            // filters not initialized in session or filter reload requested
            applicationsFilters = getUser().getApplicationsFilters();
            if (applicationsFilters.isEmpty()) {
                addFiltersForActiveApplications(applicationsFilters);
            }
        } else {
            applicationsFilters = applicationSearchDTO.getFilters();
        }
        model.addAttribute("filters", applicationsFilters);
        return APPLICATION_LIST_PAGE_VIEW_NAME;
    }

    @ModelAttribute("applicationSearchDTO")
    public ApplicationSearchDTO getApplicationSearchDTO() {
        return new ApplicationSearchDTO();
    }

    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public String getApplicationListSection(@ModelAttribute("applicationSearchDTO") ApplicationSearchDTO dto, Model model) {
        RegisteredUser user = getUser();
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, dto.getFilters(), dto.getSortCategory(),
                dto.getOrder(), dto.getBlockCount());
        Map<String, ApplicationActionsDefinition> actionDefinitions = new LinkedHashMap<String, ApplicationActionsDefinition>();
        for (ApplicationForm applicationForm : applications) {
            ApplicationActionsDefinition actionsDefinition = applicationsService.getActionsDefinition(user, applicationForm);
            actionDefinitions.put(applicationForm.getApplicationNumber(), actionsDefinition);
        }
        model.addAttribute("applications", applications);
        model.addAttribute("actionDefinitions", actionDefinitions);
        return APPLICATION_LIST_SECTION_VIEW_NAME;
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public void getApplicationsReport(@ModelAttribute("applicationSearchDTO") ApplicationSearchDTO dto, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        DataTable reportTable = applicationsReportService.getApplicationsReport(getUser(), dto.getFilters(), dto.getSortCategory(), dto.getOrder());
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
    public String saveFiltersAsDefault(@ModelAttribute("applicationSearchDTO") ApplicationSearchDTO dto) {
        userService.setFilters(getUser(), dto.getFilters());
        return "OK";
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
    public ApplicationFormStatus[] getApplicationStatusValues() {
        return ApplicationFormStatus.values();
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

    private void addFiltersForActiveApplications(List<ApplicationsFilter> applicationsFilters) {
        RegisteredUser user = getUser();
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.APPROVED));
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.REJECTED));
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.WITHDRAWN));
    }

    private ApplicationsFilter getFilterForNonStatus(RegisteredUser user, ApplicationFormStatus status) {
        ApplicationsFilter notApprovedFilter = new ApplicationsFilter();
        notApprovedFilter.setSearchCategory(SearchCategory.APPLICATION_STATUS);
        notApprovedFilter.setSearchPredicate(SearchPredicate.NOT_CONTAINING);
        notApprovedFilter.setUser(user);
        notApprovedFilter.setSearchTerm(status.displayValue());
        return notApprovedFilter;
    }
}
