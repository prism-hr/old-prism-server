package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts.mock.MockHttpSession;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationSearchDTO;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
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

    @Test
    public void shouldReturnViewForApplicationListPageWithDefaultFiltersWhenSessionFiltersNotInitialized() {

        // GIVEN
        Model model = new ExtendedModelMap();
        model.addAttribute("applicationSearchDTO", new ApplicationSearchDTO());
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ArrayList<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();
        user.setApplicationsFilters(filters);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).times(2);

        // WHEN
        EasyMock.replay(userServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(false, model, httpSession));
        EasyMock.verify(userServiceMock);

        // THEN
        List<ApplicationsFilter> defaultFilters = getFiltersForActiveApplications(user);
        Object actualFilters = model.asMap().get("filters");
        assertSame(filters, actualFilters);
        assertTrue(filters.containsAll(defaultFilters));
        assertSame(alert, model.asMap().get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithDefaultFiltersWhenFilterReloadRequested() {

        // GIVEN
        Model model = new ExtendedModelMap();
        ApplicationSearchDTO sessionDTO = new ApplicationSearchDTO();
        sessionDTO.setFilters(Arrays.asList(new ApplicationsFilter()));
        model.addAttribute("applicationSearchDTO", sessionDTO);
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ArrayList<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();
        user.setApplicationsFilters(filters);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).times(2);

        // WHEN
        EasyMock.replay(userServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(true, model, httpSession));
        EasyMock.verify(userServiceMock);

        // THEN
        List<ApplicationsFilter> defaultFilters = getFiltersForActiveApplications(user);
        Object actualFilters = model.asMap().get("filters");
        assertSame(filters, actualFilters);
        assertTrue(filters.containsAll(defaultFilters));
        assertSame(alert, model.asMap().get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWhenSessionFiltersInitialized() {

        // GIVEN
        Model model = new ExtendedModelMap();
        ApplicationSearchDTO sessionDTO = new ApplicationSearchDTO();
        List<ApplicationsFilter> sessionfFilters = Arrays.asList(new ApplicationsFilter());
        sessionDTO.setFilters(sessionfFilters);
        model.addAttribute("applicationSearchDTO", sessionDTO);
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);

        // WHEN
        assertEquals("private/my_applications_page", controller.getApplicationListPage(false, model, httpSession));

        // THEN
        assertSame(sessionfFilters, model.asMap().get("filters"));
        assertSame(alert, model.asMap().get("alertDefinition"));
    }

    @Test
    public void shouldReturnVisibleAndMatchedApplicationsWithLimitedSize() {

        // GIVEN
        Model model = new ExtendedModelMap();

        ApplicationSearchDTO dto = new ApplicationSearchDTO();
        ArrayList<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();
        dto.setFilters(filters);
        dto.setBlockCount(8);
        dto.setOrder(SortOrder.DESCENDING);
        dto.setSortCategory(SortCategory.APPLICATION_DATE);

        List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
        expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filters, SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 8)).andReturn(
                applications);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_section", controller.getApplicationListSection(dto, model));
        EasyMock.verify(userServiceMock, applicationsServiceMock);

        // THEN
        assertSame(applications, model.asMap().get("applications"));
    }

    @Test
    public void shouldReturnApplicationReport() throws IOException {

        // GIVEN
        ApplicationSearchDTO dto = new ApplicationSearchDTO();
        ArrayList<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();
        dto.setFilters(filters);
        dto.setOrder(SortOrder.DESCENDING);
        dto.setSortCategory(SortCategory.APPLICATION_DATE);

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.setParameter("tqx", "out:html");
        MockHttpServletResponse responseMock = new MockHttpServletResponse();

        DataTable dataTable = new DataTable();
        expect(applicationsReportServiceMock.getApplicationsReport(user, filters, SortCategory.APPLICATION_DATE, SortOrder.DESCENDING)).andReturn(dataTable);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock, applicationsReportServiceMock);
        controller.getApplicationsReport(dto, requestMock, responseMock);
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
        ApplicationSearchDTO dto = new ApplicationSearchDTO();
        List<ApplicationsFilter> emptyFilters = Collections.<ApplicationsFilter> emptyList();
        dto.setFilters(emptyFilters);

        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.setFilters(user, emptyFilters);

        replay(userServiceMock);
        controller.saveFiltersAsDefault(dto);
        verify(userServiceMock);
    }

    @Test
    public void shouldSaveTwoFiltersAsDefault() {
        ApplicationSearchDTO dto = new ApplicationSearchDTO();
        ApplicationsFilter filter1 = new ApplicationsFilterBuilder().id(1).build();
        ApplicationsFilter filter2 = new ApplicationsFilterBuilder().id(2).build();
        List<ApplicationsFilter> filters = Arrays.asList(filter1, filter2);
        dto.setFilters(filters);

        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.setFilters(user, filters);

        replay(userServiceMock);
        controller.saveFiltersAsDefault(dto);
        verify(userServiceMock);
    }

    @Test
    public void shouldReturnSearchPredicatesMap() {
        String predicatesMap = controller.getSearchPredicatesMap();
        assertEquals(
                "{\"APPLICATION_NUMBER\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"APPLICANT_NAME\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"PROGRAMME_NAME\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"APPLICATION_STATUS\":[{\"name\":\"CONTAINING\",\"displayName\":\"containing\"},{\"name\":\"NOT_CONTAINING\",\"displayName\":\"not containing\"}],\"SUBMISSION_DATE\":[{\"name\":\"FROM_DATE\",\"displayName\":\"from\"},{\"name\":\"ON_DATE\",\"displayName\":\"on\"},{\"name\":\"TO_DATE\",\"displayName\":\"to\"}],\"LAST_EDITED_DATE\":[{\"name\":\"FROM_DATE\",\"displayName\":\"from\"},{\"name\":\"ON_DATE\",\"displayName\":\"on\"},{\"name\":\"TO_DATE\",\"displayName\":\"to\"}]}",
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
        filtersPropertyEditorMock = EasyMock.createMock(ApplicationsFiltersPropertyEditor.class);
        controller = new ApplicationListController(applicationsServiceMock, applicationsReportServiceMock, userServiceMock, filtersPropertyEditorMock);
    }

    private List<ApplicationsFilter> getFiltersForActiveApplications(RegisteredUser user) {
        List<ApplicationsFilter> applicationsFilters = new ArrayList<ApplicationsFilter>();
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.APPROVED));
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.REJECTED));
        applicationsFilters.add(getFilterForNonStatus(user, ApplicationFormStatus.WITHDRAWN));
        return applicationsFilters;
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
