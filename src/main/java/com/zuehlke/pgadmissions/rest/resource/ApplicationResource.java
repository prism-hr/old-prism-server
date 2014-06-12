package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.rest.domain.ApplicationListRowRepresentation;
import com.zuehlke.pgadmissions.rest.domain.StateActionRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationsReportService applicationsReportService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationsFiltersPropertyEditor filtersPropertyEditor;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private ApplicationsFilteringService filteringService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @InitBinder(value = "filtering")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "filters", filtersPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ApplicationListRowRepresentation> getApplications(@RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        User currentUser = userService.getCurrentUser();
        List<ApplicationListRowRepresentation> applications = applicationService.getApplicationList(currentUser, page, perPage);
//        List<ApplicationListRowRepresentation> representations = Lists.newArrayListWithExpectedSize(applications.size());

//        for (Application application : applications) {
//            ApplicationListRowRepresentation representation = dozerBeanMapper.map(application, ApplicationListRowRepresentation.class);
//            List<PrismAction> permittedActions = actionService.getPermittedActions(application, currentUser);
//            representation.getPermittedActions().addAll(permittedActions);
//            representations.add(representation);
//        }
        return applications;
    }

//    @RequestMapping(method = RequestMethod.GET)
//    public String getApplicationListPage(@RequestParam(required = false) String applyFilters,
//        ModelMap model, HttpSession session) {
//        Object alertDefinition = session.getAttribute("alertDefinition");
//        if (alertDefinition != null) {
//            model.addAttribute("alertDefinition", alertDefinition);
//            session.removeAttribute("alertDefinition");
//        }
//
//        Filter filtering = (Filter) model.get("filtering");
//
//        if (("urgent").equals(applyFilters)) {
//            filtering = filteringService.getUrgentApplicationFiltering();
//        } else if (("active").equals(applyFilters)) {
//            filtering = filteringService.getActiveApplicationFiltering();
//        } else if (("default").equals(applyFilters) || filtering == null) {
//            filtering = filteringService.getDefaultApplicationFiltering(getUser());
//        }
//
//        model.addAttribute("filtering", filtering);
//        return APPLICATION_LIST_PAGE_VIEW_NAME;
//    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public void getApplicationsReport(@ModelAttribute("filtering") Filter filtering, @RequestParam(required = false) ReportFormat reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        DataTable reportTable = applicationsReportService.getApplicationsReport(getUser(), filtering, reportType);
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
    public String saveFiltersAsDefault(@ModelAttribute("filtering") Filter filtering, @RequestParam Boolean useDisjunction) {
        filtering.setSatisfyAllConditions(useDisjunction);
        userService.setFiltering(getUser(), filtering);
        return "OK";
    }

    @RequestMapping(value = "/getApplicationDetails", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getApplicationDetails(@RequestParam String applicationId) {
        return applicationSummaryService.getSummary(applicationId);
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

}
