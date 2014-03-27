package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public class MoveToReviewControllerTest {

    private MoveToReviewController controller;
    private ApplicationFormService applicationServiceMock;
    private UserService userServiceMock;
    private ReviewService reviewServiceMock;
    private BindingResult bindingResultMock;
    private RegisteredUser currentUserMock;
    private MoveToReviewReviewerPropertyEditor reviewerPropertyEditorMock;
    private ReviewRoundValidator reviewRoundValidatorMock;
    private WorkflowService applicationFormUserRoleServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldGetReviewRoundPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        actionsProviderMock.validateAction(applicationForm, currentUserMock, ApplicationFormAction.ASSIGN_REVIEWERS);

        EasyMock.replay(actionsProviderMock);
        String reviewRoundDetailsPage = controller.getReviewRoundDetailsPage(modelMap);
        EasyMock.verify(actionsProviderMock);

        Assert.assertEquals(MoveToReviewController.REVIEW_DETAILS_VIEW_NAME, reviewRoundDetailsPage);
    }

    @Test
    public void shouldGetReviewesSectionWithOnlyAssignFalseNewReviewersFunctionality() {
        String reviewersDetailsSection = controller.getReviewersSectionView();
        Assert.assertEquals(MoveToReviewController.REVIEWERS_SECTION_NAME, reviewersDetailsSection);
    }

    @Test
    public void shouldGetApplicationFromId() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test
    public void shouldReturnNewReviewRoundWithEmtpyReviewersIfNoLatestReviewRound() {

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();

        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("bob")) {
                    return application;
                }
                return null;
            }

        };
        ReviewRound returnedReviewRound = controller.getReviewRound("bob");
        assertNull(returnedReviewRound.getId());
        assertTrue(returnedReviewRound.getReviewers().isEmpty());
    }

    @Test
    public void shouldMoveApplicationToReview() {

        ReviewRound reviewRound = new ReviewRoundBuilder().id(4).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();

        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return application;
            }

        };

        reviewServiceMock.moveApplicationToReview(application, reviewRound, currentUserMock);
        EasyMock.replay(reviewServiceMock);

        String view = controller.moveToReview("abc", reviewRound, bindingResultMock);
        assertEquals("/private/common/ajax_OK", view);
        EasyMock.verify(reviewServiceMock);

    }

    @Test
    public void shouldNotSaveReviewRoundAndReturnToReviewRoundPageIfHasErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).build();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock, applicationServiceMock);
        assertEquals(MoveToReviewController.REVIEWERS_SECTION_NAME, controller.moveToReview("1", reviewRound, errorsMock));

    }

    @Test
    public void shouldAddReviewRoundValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(reviewRoundValidatorMock);
        binderMock.registerCustomEditor(Reviewer.class, reviewerPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerReviewRoundValidator(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFromIdForAdmin() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("5");
    }

    @Test
    public void shouldGetCurrentUserAsUser() {
        assertEquals(currentUserMock, controller.getUser());
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationFormService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);
        reviewServiceMock = EasyMock.createMock(ReviewService.class);

        reviewerPropertyEditorMock = EasyMock.createMock(MoveToReviewReviewerPropertyEditor.class);
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResultMock);
        reviewRoundValidatorMock = EasyMock.createMock(ReviewRoundValidator.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, applicationFormUserRoleServiceMock, actionsProviderMock);

    }

}
