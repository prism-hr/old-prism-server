package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DeclineControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationServiceMock;
    
    @Mock
    @InjectIntoByType
    private UserService userServiceMock;
    
    @Mock
    @InjectIntoByType
    private CommentService commentServiceMock;
    
    @Mock
    @InjectIntoByType
    private RefereeService refereeServiceMock;
    
    @Mock
    @InjectIntoByType
    private ActionService actionServiceMock;
    
    @TestedObject
    private DeclineController controller;

    @Test
    public void shouldGetReviewerFromId() {
        User reviewer = new User().withId(5);
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(reviewer);
        EasyMock.replay(userServiceMock);
        User returnedReviewer = controller.getReviewer("5");
        assertEquals(reviewer, returnedReviewer);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUsernotFound() {
        User reviewer = new User().withId(5);
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(null);
        EasyMock.replay(userServiceMock);
        User returnedReviewer = controller.getReviewer("5");
        assertEquals(reviewer, returnedReviewer);
    }

    @Test
    public void shouldGetApplicationFromId() {
        Application applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock);
        Application returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowExceptionIfApplicationNotExists() {
        Application applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);
        Application returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);
    }

    @Test
    public void shouldGetRefereeFromActivationCodeAndApplicationForm() {
        User userMock = EasyMock.createMock(User.class);
        Application applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(userMock);
        Referee referee = new RefereeBuilder().id(5).build();
        EasyMock.replay(userServiceMock, userMock);
        Referee returnedReferee = controller.getReferee("5", applicationForm);
        assertEquals(referee, returnedReferee);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserDoesNotExists() {
        Application applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(null);
        EasyMock.replay(userServiceMock);
        controller.getReferee("5", applicationForm);

    }

    @Test
    public void shouldReturnConfirmationDialogForReference() {
        final Application applicationForm = new ApplicationFormBuilder().applicationNumber("ABC")
                .applicant(new User().withFirstName("").withLastName("")).id(5).build();
        final Referee referee = new RefereeBuilder().application(applicationForm).id(5).build();
        EasyMock.replay(refereeServiceMock);
        String view = controller.declineReference("5", "ABC", null, new ModelMap());
        EasyMock.verify(refereeServiceMock);
        assertEquals(TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME, view);
    }

}