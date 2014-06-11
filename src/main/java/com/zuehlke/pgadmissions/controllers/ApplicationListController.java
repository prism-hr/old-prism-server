package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.rest.domain.ApplicationListRepresentation;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationSummaryService;
import com.zuehlke.pgadmissions.services.ApplicationsFilteringService;
import com.zuehlke.pgadmissions.services.ApplicationsReportService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationListController {

    private static final String APPLICATION_LIST_PAGE_VIEW_NAME = "private/my_applications_page";
    private static final String APPLICATION_LIST_SECTION_VIEW_NAME = "private/my_applications_section";

    @Autowired
    private ResourceService resourceService;

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
    private DozerBeanMapper dozerBeanMapper;

    @InitBinder(value = "filtering")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "filters", filtersPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ApplicationListRepresentation> getApplications(@RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        List<Application> applications = resourceService.getConsoleList(Application.class, userService.getCurrentUser(), page, perPage);

        return Lists.transform(applications, new Function<Application, ApplicationListRepresentation>() {
            @Override
            public ApplicationListRepresentation apply(Application input) {
                return dozerBeanMapper.map(input, ApplicationListRepresentation.class);
            }
        });
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
