package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVAL;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.APPROVED;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.INTERVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REJECTED;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.REVIEW;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.VALIDATION;
import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.WITHDRAWN;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.struts.mock.MockHttpSession;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationSummaryService;
import com.zuehlke.pgadmissions.services.ApplicationsFilteringService;
import com.zuehlke.pgadmissions.services.ApplicationsReportService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationListControllerTest {

    private ApplicationListController controller;
    private RegisteredUser user;
    private ApplicationsService applicationsServiceMock;
    private ApplicationsReportService applicationsReportServiceMock;
    private UserService userServiceMock;
    private ApplicationsFiltersPropertyEditor filtersPropertyEditorMock;
    private ApplicationSummaryService applicationSummaryServiceMock;
    private ApplicationsFilteringService filteringServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldReturnViewForApplicationListPageWithStoredFiltersWhenSessionFiltersNotInitialized() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        user.setFiltering(filtering);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(filteringServiceMock.getStoredOrDefaultFiltering(user)).andReturn(filtering);

        // WHEN
        EasyMock.replay(userServiceMock, filteringServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(null, model, httpSession));
        EasyMock.verify(userServiceMock, filteringServiceMock);

        // THEN
        Object actualFiltering = model.get("filtering");
        assertSame(filtering, actualFiltering);
        assertSame(alert, model.get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithActiveApplicationFiltersWhenSessionFiltersNotInitialized() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ApplicationsFiltering filtering = new ApplicationsFiltering();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(filteringServiceMock.getStoredOrDefaultFiltering(user)).andReturn(filtering);

        // WHEN
        EasyMock.replay(userServiceMock, filteringServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(null, model, httpSession));
        EasyMock.verify(userServiceMock, filteringServiceMock);

        // THEN
        ApplicationsFiltering actualFiltering = (ApplicationsFiltering) model.get("filtering");
        assertSame(filtering, actualFiltering);
        assertSame(alert, model.get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithDefaultFiltersWhenFilterReloadRequested() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();

        model.addAttribute("filtering", new ApplicationsFiltering());
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        user.setFiltering(filtering);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(filteringServiceMock.getStoredOrDefaultFiltering(user)).andReturn(filtering);

        // WHEN
        EasyMock.replay(userServiceMock, filteringServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage("reload", model, httpSession));
        EasyMock.verify(userServiceMock, filteringServiceMock);

        // THEN
        Object actualFiltering = model.get("filtering");
        assertSame(filtering, actualFiltering);
        assertSame(alert, model.get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithDefaultFiltersWhenMyApplicationsRequested() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();

        model.addAttribute("filtering", new ApplicationsFiltering());
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);

        // WHEN
        assertEquals("private/my_applications_page", controller.getApplicationListPage("my", model, httpSession));

        // THEN
        ApplicationsFiltering actualFiltering = (ApplicationsFiltering) model.get("filtering");
        assertEquals(ApplicationsPreFilter.MY, actualFiltering.getPreFilter());
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithDefaultFiltersWhenUrgentApplicationsRequested() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();

        model.addAttribute("filtering", new ApplicationsFiltering());
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);

        // WHEN
        assertEquals("private/my_applications_page", controller.getApplicationListPage("urgent", model, httpSession));

        // THEN
        ApplicationsFiltering actualFiltering = (ApplicationsFiltering) model.get("filtering");
        assertEquals(ApplicationsPreFilter.URGENT, actualFiltering.getPreFilter());
    }

    @Test
    public void shouldReturnViewForApplicationListPageWhenSessionFiltersInitialized() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();

        ApplicationsFiltering filtering = new ApplicationsFiltering();
        model.addAttribute("filtering", filtering);
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);

        // WHEN
        assertEquals("private/my_applications_page", controller.getApplicationListPage(null, model, httpSession));

        // THEN
        assertSame(filtering, model.get("filtering"));
        assertSame(alert, model.get("alertDefinition"));
    }

    @Test
    public void shouldReturnVisibleAndMatchedApplicationsWithLimitedSize() {

        // GIVEN
        ModelMap model = new ExtendedModelMap();
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setPreFilter(ApplicationsPreFilter.URGENT);
        List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filtering)).andReturn(applications);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_section", controller.getApplicationListSection(filtering, true, model, response));
        EasyMock.verify(userServiceMock, applicationsServiceMock);

        // THEN
        assertSame(applications, model.get("applications"));
        assertEquals(HttpServletResponse.SC_SEE_OTHER, response.getStatus());
    }

    @Test
    public void shouldReturnApplicationReport() throws IOException {

        // GIVEN
        ApplicationsFiltering filtering = new ApplicationsFiltering();

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.setParameter("tqx", "out:html");
        MockHttpServletResponse responseMock = new MockHttpServletResponse();

        DataTable dataTable = new DataTable();
        expect(applicationsReportServiceMock.getApplicationsReport(user, filtering)).andReturn(dataTable);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock, applicationsReportServiceMock);
        controller.getApplicationsReport(filtering, requestMock, responseMock);
        EasyMock.verify(userServiceMock, applicationsReportServiceMock);

        assertEquals("text/html; charset=UTF-8", responseMock.getContentType());
    }

    @Test
    public void shouldAddUserCurrentUser() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.replay(userServiceMock);
        assertEquals(user, controller.getUser());
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnNullMessageForNullParams() {
        assertNull(controller.getMessage(false, null, null));
    }

    @Test
    public void shouldGetApplicationFormByNumber() {
        String appNumber = "abc";
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(appNumber)).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        assertEquals(applicationForm, controller.getApplicationForm(appNumber));
    }

    @Test
    public void shouldAddSubmissionSuccesMessageIfRequired() {
        assertEquals("Your application has been successfully submitted.", controller.getMessage(true, null, null));
    }

    @Test
    public void shouldAddDecissionMessageIfRequired() {
        assertEquals("The application was successfully bobbed.", controller.getMessage(false, "bobbed", null));
    }

    @Test
    public void shouldAddPassedMessageIfRequired() {
        assertEquals("my message", controller.getMessage(false, null, "my message"));
    }

    @Test
    public void shouldRegisterApplicationSearchDTOEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.registerCustomEditor(List.class, "filters", filtersPropertyEditorMock);

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldSaveNoFiltersAsDefault() {

        List<ApplicationsFilter> emptyFilters = Collections.<ApplicationsFilter> emptyList();
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setFilters(emptyFilters);

        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.setFiltering(user, filtering);

        replay(userServiceMock);
        controller.saveFiltersAsDefault(filtering, false);
        verify(userServiceMock);
    }

    @Test
    public void shouldSaveTwoFiltersAsDefault() {

        ApplicationsFilter filter1 = new ApplicationsFilterBuilder().id(1).build();
        ApplicationsFilter filter2 = new ApplicationsFilterBuilder().id(2).build();
        List<ApplicationsFilter> filters = Arrays.asList(filter1, filter2);
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setFilters(filters);

        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.setFiltering(user, filtering);

        replay(userServiceMock);
        controller.saveFiltersAsDefault(filtering, true);
        verify(userServiceMock);
    }

    @Test
    public void shouldReturnSearchPredicatesMap() {
        String predicatesMap = controller.getSearchPredicatesMap();
        assertEquals(
                "{\"APPLICATION_NUMBER\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"APPLICANT_NAME\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"PROGRAMME_NAME\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"PROJECT_TITLE\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"APPLICATION_STATUS\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"SUBMISSION_DATE\":[{\"name\":\"FROM_DATE\",\"displayName\":\"from\"},{\"name\":\"ON_DATE\",\"displayName\":\"on\"},{\"name\":\"TO_DATE\",\"displayName\":\"to\"}],\"SUPERVISOR\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"LAST_EDITED_DATE\":[{\"name\":\"FROM_DATE\",\"displayName\":\"from\"},{\"name\":\"ON_DATE\",\"displayName\":\"on\"},{\"name\":\"TO_DATE\",\"displayName\":\"to\"}],\"CLOSING_DATE\":[{\"name\":\"FROM_DATE\",\"displayName\":\"from\"},{\"name\":\"ON_DATE\",\"displayName\":\"on\"},{\"name\":\"TO_DATE\",\"displayName\":\"to\"}]}",
                predicatesMap);
    }

    @Test
    public void shouldReturnRelevantApplicationStatusValues() {
        List<ApplicationFormStatus> values = controller.getApplicationStatusValues();
        assertEquals(values, Lists.newArrayList(VALIDATION, REVIEW, INTERVIEW, APPROVAL, APPROVED, WITHDRAWN, REJECTED));
    }

    @Before
    public void setUp() {
        user = new RegisteredUserBuilder().id(1).build();
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        applicationsReportServiceMock = EasyMock.createMock(ApplicationsReportService.class);
        filtersPropertyEditorMock = createMock(ApplicationsFiltersPropertyEditor.class);
        applicationSummaryServiceMock = createMock(ApplicationSummaryService.class);
        filteringServiceMock = EasyMock.createMock(ApplicationsFilteringService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        controller = new ApplicationListController(applicationsServiceMock, applicationsReportServiceMock, userServiceMock, filtersPropertyEditorMock,
                applicationSummaryServiceMock, filteringServiceMock, actionsProviderMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCallSummaryServiceToGetApplicationSummary() {
        EasyMock.expect(applicationSummaryServiceMock.getSummary("appID")).andReturn(Collections.EMPTY_MAP);

        replay(applicationSummaryServiceMock);
        Assert.assertTrue(controller.getApplicationDetails("appID").isEmpty());
        EasyMock.verify(applicationSummaryServiceMock);
    }
}
