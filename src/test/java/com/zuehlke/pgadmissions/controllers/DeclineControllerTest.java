package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

public class DeclineControllerTest {

    private DeclineController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private CommentService commentServiceMock;
    private RefereeService refereeServiceMock;
    private ActionsProvider actionsProviderMock;
    private static final String DECLINE_REVIEW_SUCCESS_VIEW_NAME = "/private/reviewers/decline_success_confirmation";
    private static final String DECLINE_CONFIRMATION_VIEW_NAME = "/private/reviewers/decline_confirmation";

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
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock);
        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationNotExists() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
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
        EasyMock.expect(userMock.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
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
        controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock, actionsProviderMock) {
            @Override
            public RegisteredUser getReviewer(String activationCode) {
                if ("5".equals(activationCode)) {
                    return reviewer;
                }
                return null;
            }

            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("ABC")) {
                    return applicationForm;
                }
                return null;
            }
        };
        commentServiceMock.declineReview(reviewer, applicationForm);
        reviewer.setDirectToUrl(null);
        EasyMock.replay(commentServiceMock, reviewer);
        String view = controller.declineReview("5", applicationForm.getApplicationNumber(), "OK", new ModelMap());
        EasyMock.verify(commentServiceMock);
        assertEquals(DECLINE_REVIEW_SUCCESS_VIEW_NAME, view);
    }

    @Test
    public void shouldReturnConfirmationDialogForReview() {
        final RegisteredUser reviewer = EasyMock.createMock(RegisteredUser.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW)
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).applicationNumber("ABC").build();
        controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock, actionsProviderMock) {
            @Override
            public RegisteredUser getReviewer(String activationCode) {
                if ("5".equals(activationCode)) {
                    return reviewer;
                }
                return null;
            }

            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("ABC")) {
                    return applicationForm;
                }
                return null;
            }
        };
        String view = controller.declineReview("5", applicationForm.getApplicationNumber(), null, new ModelMap());
        assertEquals(DECLINE_CONFIRMATION_VIEW_NAME, view);
    }

    @Test
    public void shouldReturnConfirmationDialogForReference() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC")
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).build();
        final Referee referee = new RefereeBuilder().application(applicationForm).id(5).build();
        controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock, actionsProviderMock) {
            @Override
            public Referee getReferee(String activationCode, ApplicationForm app) {
                if ("5".equals(activationCode) && applicationForm == app) {
                    return referee;
                }
                return null;
            }

            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("ABC")) {
                    return applicationForm;
                }
                return null;
            }
        };
        EasyMock.replay(refereeServiceMock);
        String view = controller.declineReference("5", "ABC", null, new ModelMap());
        EasyMock.verify(refereeServiceMock);
        assertEquals(DECLINE_CONFIRMATION_VIEW_NAME, view);
    }

    @Test
    public void shouldDeclineReferenceAndReturnMessageView() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC")
                .applicant(new RegisteredUserBuilder().firstName("").lastName("").build()).id(5).build();
        final RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        final Referee referee = new RefereeBuilder().application(applicationForm).id(5).build();

        EasyMock.expect(userServiceMock.getUserByActivationCode("5")).andReturn(userMock);

        controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock, actionsProviderMock) {
            @Override
            public Referee getReferee(String activationCode, ApplicationForm app) {
                if ("5".equals(activationCode) && applicationForm == app) {
                    return referee;
                }
                return null;
            }

            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("ABC")) {
                    return applicationForm;
                }
                return null;
            }
        };

        refereeServiceMock.declineToActAsRefereeAndSendNotification(referee);

        userMock.setDirectToUrl(null);

        userServiceMock.save(userMock);

        EasyMock.replay(userServiceMock, refereeServiceMock);

        String view = controller.declineReference("5", "ABC", "OK", new ModelMap());

        EasyMock.verify(refereeServiceMock);

        assertEquals(DECLINE_REVIEW_SUCCESS_VIEW_NAME, view);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        controller = new DeclineController(userServiceMock, commentServiceMock, applicationServiceMock, refereeServiceMock, actionsProviderMock);

    }
}