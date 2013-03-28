package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts.mock.MockHttpSession;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationSearchDTO;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationsFiltersPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationListControllerTest {

    private ApplicationListController controller;
    private RegisteredUser user;
    private ApplicationsService applicationsServiceMock;
    private UserService userServiceMock;
    private ApplicationsFiltersPropertyEditor filtersPropertyEditorMock;

    @Test
    public void shouldReturnViewForApplicationListPage() {

        // GIVEN
        Model model = new ExtendedModelMap();
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        ArrayList<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();
        user.setApplicationsFilters(filters);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(model, httpSession));
        EasyMock.verify(userServiceMock);

        // THEN
        assertSame(filters, model.asMap().get("filters"));
        assertSame(alert, model.asMap().get("alertDefinition"));
    }

    @Test
    public void shouldReturnApplicationListSection() {

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
                "{\"APPLICATION_NUMBER\":[\"CONTAINING\",\"NOT_CONTAINING\"],\"APPLICANT_NAME\":[\"CONTAINING\",\"NOT_CONTAINING\"],\"PROGRAMME_NAME\":[\"CONTAINING\",\"NOT_CONTAINING\"],\"APPLICATION_STATUS\":[\"CONTAINING\",\"NOT_CONTAINING\"],\"APPLICATION_DATE\":[\"FROM_DATE\",\"ON_DATE\",\"TO_DATE\"]}",
                predicatesMap);
    }

    @Before
    public void setUp() {
        user = new RegisteredUserBuilder().id(1).build();
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        filtersPropertyEditorMock = EasyMock.createMock(ApplicationsFiltersPropertyEditor.class);
        controller = new ApplicationListController(applicationsServiceMock, userServiceMock, filtersPropertyEditorMock);
    }
}
