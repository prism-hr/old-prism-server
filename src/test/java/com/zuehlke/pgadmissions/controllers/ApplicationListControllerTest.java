package com.zuehlke.pgadmissions.controllers;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.*;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.struts.mock.MockHttpSession;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationListControllerTest {

    private RegisteredUser user;
    private ApplicationsService applicationsServiceMock;
    private ApplicationListController controller;
    private UserService userServiceMock;

    @Test
    public void shouldReturnViewForApplicationListPageWithNoFilters() {

        // GIVEN
        Model model = new ExtendedModelMap();
        HttpSession httpSession = new MockHttpSession();
        AlertDefinition alert = new AlertDefinition(AlertType.WARNING, "title", "desc");
        httpSession.setAttribute("alertDefinition", alert);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        // WHEN
        EasyMock.replay(userServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(model, httpSession));
        EasyMock.verify(userServiceMock);

        // THEN
        assertEquals(false, model.asMap().get("hasFilter"));
        assertSame(alert, model.asMap().get("alertDefinition"));
    }

    @Test
    public void shouldReturnViewForApplicationListPageWithFilter() {

        Model model = new ExtendedModelMap();
        HttpSession httpSession = new MockHttpSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("term").build();
        user.setApplicationsFilters(Arrays.asList(filter));
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_page", controller.getApplicationListPage(model, httpSession));
        assertEquals(true, model.asMap().get("hasFilter"));
        assertEquals("term", model.asMap().get("searchTerm"));
        assertEquals(SearchCategory.APPLICANT_NAME, model.asMap().get("searchCategory"));
        EasyMock.verify(userServiceMock, applicationsServiceMock);
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

    @SuppressWarnings("unchecked")
    @Test
    public void shouldApplyanExistingFilter() {
        Model model = new ExtendedModelMap();

        ApplicationForm application = new ApplicationFormBuilder().id(4).build();
        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("term").build();
        user.setApplicationsFilters(Arrays.asList(filter));
        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        expect(
                applicationsServiceMock.getAllVisibleAndMatchedApplications(user, filter.getSearchCategory(), filter.getSearchTerm(),
                        SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4)).andReturn(asList(application));

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_section",
                controller.getApplicationListSection(null, null, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4, false, model));

        List<ApplicationForm> result = ((List<ApplicationForm>) model.asMap().get("applications"));
        assertEquals(1, result.size());
        assertEquals(application, result.get(0));

        verify(userServiceMock, applicationsServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateANewFilter() {
        Model model = new ExtendedModelMap();

        ApplicationForm application = new ApplicationFormBuilder().id(4).build();
        user.setApplicationsFilters(new ArrayList<ApplicationsFilter>());
        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.saveFilter(eq(user), isA(ApplicationsFilter.class));
        expect(
                applicationsServiceMock.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICANT_NAME, "Johnno", SortCategory.APPLICANT_NAME,
                        SortOrder.ASCENDING, 4)).andReturn(asList(application));

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_section", controller.getApplicationListSection(SearchCategory.APPLICANT_NAME, "Johnno",
                SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4, false, model));

        List<ApplicationForm> result = ((List<ApplicationForm>) model.asMap().get("applications"));
        assertEquals(1, result.size());
        assertEquals(application, result.get(0));

        verify(userServiceMock, applicationsServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldClearApplicationFilterForUser() {
        Model model = new ExtendedModelMap();

        ApplicationForm application = new ApplicationFormBuilder().id(4).build();
        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("term").build();
        user.setApplicationsFilters(asList(filter));
        expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        userServiceMock.clearApplicationsFilter(filter);
        expect(
                applicationsServiceMock.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICANT_NAME, "Johnno", SortCategory.APPLICANT_NAME,
                        SortOrder.ASCENDING, 4)).andReturn(asList(application));

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        assertEquals("private/my_applications_section",
                controller.getApplicationListSection(SearchCategory.APPLICANT_NAME, "Johnno", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4, true, model));

        List<ApplicationForm> result = ((List<ApplicationForm>) model.asMap().get("applications"));
        assertEquals(1, result.size());
        assertEquals(application, result.get(0));

        verify(userServiceMock, applicationsServiceMock);
    }

    @Test
    public void shouldAddAllApplications() {
        ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).build();
        ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,//
                SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 4))//
                .andReturn(Arrays.asList(applicationOne, applicationTwo));
        EasyMock.replay(applicationsServiceMock);
        controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {

            @Override
            public RegisteredUser getUser() {
                return user;
            }

        };
        List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME,
                SortOrder.ASCENDING, 4);

        EasyMock.verify(applicationsServiceMock);
        Assert.assertEquals(2, applications.size());
        Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
    }

    @Test
    public void shouldReturnFirstBlockOfApplications() {
        ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).build();
        ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(applicationsServiceMock.getAllVisibleAndMatchedApplications(user,//
                SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1))//
                .andReturn(Arrays.asList(applicationOne, applicationTwo));
        EasyMock.replay(applicationsServiceMock);
        controller = new ApplicationListController(applicationsServiceMock, userServiceMock) {

            @Override
            public RegisteredUser getUser() {
                return user;
            }

        };

        List<ApplicationForm> applications = controller.getApplications(SearchCategory.APPLICATION_DATE, "bladibla", SortCategory.APPLICANT_NAME,
                SortOrder.ASCENDING, 1);

        EasyMock.verify(applicationsServiceMock);
        Assert.assertEquals(2, applications.size());
        Assert.assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
    }

    @Before
    public void setUp() {
        user = new RegisteredUserBuilder().id(1).build();
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        controller = new ApplicationListController(applicationsServiceMock, userServiceMock);
    }
}
