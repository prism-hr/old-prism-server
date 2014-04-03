package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
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
        RegisteredUser reviewer = new RegisteredUserBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(reviewer);
        EasyMock.replay(userServiceMock);
        RegisteredUser returnedReviewer = controller.getReviewer("5");
        assertEquals(reviewer, returnedReviewer);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUsernotFound() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(null);
        EasyMock.replay(userServiceMock);
        RegisteredUser returnedReviewer = controller.getReviewer("5");
        assertEquals(reviewer, returnedReviewer);
    }

    @Test
    public void shouldGetApplicationFromId() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock);
        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationNotExists() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);
        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);
    }

    @Test
    public void shouldGetRefereeFromActivationCodeAndApplicationForm() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(userMock);
        Referee referee = new RefereeBuilder().id(5).build();
        EasyMock.replay(userServiceMock, userMock);
        Referee returnedReferee = controller.getReferee("5", applicationForm);
        assertEquals(referee, returnedReferee);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserDoesNotExists() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(null);
        EasyMock.replay(userServiceMock);
        controller.getReferee("5", applicationForm);

    }

    @Test
    public void shouldDeclineReviewAndReturnMessageView() {
        final RegisteredUser reviewer = EasyMock.createMock(RegisteredUser.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW)
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).applicationNumber("ABC").build();
        commentServiceMock.declineReview(reviewer, applicationForm);
        reviewer.setDirectToUrl(null);
        EasyMock.replay(commentServiceMock, reviewer);
        String view = controller.declineReview("5", applicationForm.getApplicationNumber(), "OK", new ModelMap());
        EasyMock.verify(commentServiceMock);
        assertEquals(TemplateLocation.DECLINE_SUCCESS_VIEW_NAME, view);
    }

    @Test
    public void shouldReturnConfirmationDialogForReview() {
        final RegisteredUser reviewer = EasyMock.createMock(RegisteredUser.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW)
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).applicationNumber("ABC").build();
        String view = controller.declineReview("5", applicationForm.getApplicationNumber(), null, new ModelMap());
        assertEquals(TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME, view);
    }

    @Test
    public void shouldReturnConfirmationDialogForReference() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC")
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).build();
        final Referee referee = new RefereeBuilder().application(applicationForm).id(5).build();
        EasyMock.replay(refereeServiceMock);
        String view = controller.declineReference("5", "ABC", null, new ModelMap());
        EasyMock.verify(refereeServiceMock);
        assertEquals(TemplateLocation.DECLINE_CONFIRMATION_VIEW_NAME, view);
    }

    @Test
    public void shouldDeclineReferenceAndReturnMessageView() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC")
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).build();
        final RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        final Referee referee = new RefereeBuilder().application(applicationForm).id(5).build();

        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(userMock);

        refereeServiceMock.declineToActAsRefereeAndSendNotification(referee);

        userMock.setDirectToUrl(null);

        userServiceMock.save(userMock);

        EasyMock.replay(userServiceMock, refereeServiceMock);

        String view = controller.declineReference("5", "ABC", "OK", new ModelMap());

        EasyMock.verify(refereeServiceMock);

        assertEquals(TemplateLocation.DECLINE_SUCCESS_VIEW_NAME, view);
    }

}