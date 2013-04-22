package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bouncycastle.cert.ocsp.Req;
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
import com.google.gson.GsonBuilder;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
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
            RegisteredUser user = getUser();
            if (!user.isStoredFilters()) {
                applicationsFilters = getActiveApplicationFilters();
            } else {
                applicationsFilters = user.getApplicationsFilters();
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

    @RequestMapping(value = "/getApplicationDetails", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getApplicationDetails(@RequestParam String applicationId){
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        
        Map<String , String> result = Maps.newHashMap();
        Map<String, String> applicantResult = Maps.newHashMap();
        RegisteredUser applicant = application.getApplicant();
        PersonalDetails personalDetails = application.getPersonalDetails();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        result.put("applicationSubmissionDate",  dateFormat.format(application.getSubmittedDate()));
        result.put("applicationUpdateDate", dateFormat.format(application.getLastUpdated()));
        result.put("numberOfActiveApplications", "0");
        
        applicantResult.put("title", personalDetails.getTitle().getDisplayValue());
        applicantResult.put("name", applicant.getDisplayName());
        applicantResult.put("phoneNumber", personalDetails.getPhoneNumber());
        applicantResult.put("email", applicant.getEmail());
        
        List<Qualification> qualifications = application.getQualifications();
        
        if (qualifications.size() > 0) {
            Collections.sort(qualifications, new Comparator<Qualification>() {
                public int compare(Qualification q1, Qualification q2) {
                    return q1.getQualificationAwardDate().compareTo(q2.getQualificationAwardDate());
                }
            });
            
            Qualification mostRecentQualification = qualifications.get(0);
            applicantResult.put("mostRecentQualification", mostRecentQualification.getQualificationTitle() + " in " + mostRecentQualification.getQualificationSubject());
        }
        else {
            applicantResult.put("mostRecentQualification", "None provided");
        }
        
        List<EmploymentPosition> employments = application.getEmploymentPositions();
        
        if (employments.size() > 0) {
            Collections.sort(employments, new Comparator<EmploymentPosition>() {
               public int compare(EmploymentPosition e1, EmploymentPosition e2) {
                   
                   Date e1Date = e1.getEndDate();
                   Date e2Date = e2.getEndDate();
                   
                   if (e1Date == null) {
                       return -1;
                   }
                   else if (e2Date == null) {
                       return 1;
                   }
                   
                   return e1Date.compareTo(e2Date);
               }
            });
            
            
            applicantResult.put("mostRecentEmployment", employments.get(0).getEmployerName());
        }
        else {
            applicantResult.put("mostRecentEmployment", "None provided");
        }
        
        List<Funding> fundings = application.getFundings();
        List<String> fundingsResult = new ArrayList<String>();
        
        if (fundings.size() > 0) {
            for (int i = 0; i < fundings.size(); i++) {
                fundingsResult.add(fundings.get(i).getDescription());
            }
        }
        else {
            fundingsResult.add("None provided");
        }
        
        applicantResult.put("fundingRequirements", gson.toJson(fundingsResult));
        
        // What is the criteria to select fundings? All?
        
        List<ReferenceComment> references = application.getReferencesToSendToPortico();
        result.put("numberOfReferences", Integer.toString(references.size()));
        
        // No of References responded? application.getReferencesToSendToPortico()?
        Document personalStatement = application.getPersonalStatement();
        
        result.put("personalStatement", personalStatement.getFileName());
        
        // How to reference the statement?
        
        result.put("applicationStatus", application.getStatus().displayValue());
        
        result.put("applicant", gson.toJson(applicantResult));
//        new Gson().toJson(result);
        return result;
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

    private List<ApplicationsFilter> getActiveApplicationFilters() {
        List<ApplicationsFilter> filters = Lists.newArrayListWithExpectedSize(3);
        filters.add(getFilterForNonStatus(ApplicationFormStatus.APPROVED));
        filters.add(getFilterForNonStatus(ApplicationFormStatus.REJECTED));
        filters.add(getFilterForNonStatus(ApplicationFormStatus.WITHDRAWN));
        return filters;
    }

    private ApplicationsFilter getFilterForNonStatus(ApplicationFormStatus status) {
        ApplicationsFilter filter = new ApplicationsFilter();
        filter.setSearchCategory(SearchCategory.APPLICATION_STATUS);
        filter.setSearchPredicate(SearchPredicate.NOT_CONTAINING);
        filter.setSearchTerm(status.displayValue());
        return filter;
    }
}
