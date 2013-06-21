package com.zuehlke.pgadmissions.controllers.workflow.review;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ASSIGN_REVIEWERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public class MoveToReviewControllerTest {

    private MoveToReviewController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private ReviewService reviewServiceMock;
    private BindingResult bindingResultMock;
    private RegisteredUser currentUserMock;
    private MoveToReviewReviewerPropertyEditor reviewerPropertyEditorMock;
    private ReviewRoundValidator reviewRoundValidatorMock;
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldGetReviewRoundPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        actionsProviderMock.validateAction(applicationForm, currentUserMock, ASSIGN_REVIEWERS);

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
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test
    public void shouldReturnNewReviewRoundWithExistingRoundsReviewersWhoHaveNotDeclined() {
        Reviewer reviewerOne = new ReviewerBuilder().id(1).build();
        ReviewComment reviewTwoComment = new ReviewCommentBuilder().decline(true).build();
        Reviewer reviewerTwo = new ReviewerBuilder().id(2).review(reviewTwoComment).build();
        Reviewer reviewerThree = new ReviewerBuilder().id(2).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc")
                .latestReviewRound(new ReviewRoundBuilder().reviewers(reviewerOne, reviewerTwo, reviewerThree).build()).build();

        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
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
        assertEquals(2, returnedReviewRound.getReviewers().size());
        assertTrue(returnedReviewRound.getReviewers().containsAll(Arrays.asList(reviewerOne, reviewerThree)));
    }

    @Test
    public void shouldReturnNewReviewRoundWithEmtpyReviewersIfNoLatestReviewRound() {

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();

        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
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
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return application;
            }

        };

        reviewServiceMock.moveApplicationToReview(application, reviewRound);
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
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
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
    public void shouldGetNominatedReviewers() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().reviewers(interUser1, interUser2).id(6).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();

        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("5")) {
                    return applicationForm;
                }
                return null;
            }

            @Override
            public ReviewRound getReviewRound(Object applicationId) {
                return null;
            }

        };

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);
        EasyMock.replay(userServiceMock);
        List<RegisteredUser> reviewersUsers = controller.getNominatedSupervisors("5");
        assertEquals(2, reviewersUsers.size());
        assertTrue(reviewersUsers.containsAll(Arrays.asList(interUser1, interUser2)));
    }

    @Test
    public void shouldGetProgrammeReviewersAndRemoveApplicantNominatedSupervisors() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();
        final RegisteredUser interUser3 = new RegisteredUserBuilder().id(5).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().reviewers(interUser1, interUser2, interUser3).id(6).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("5")) {
                    return applicationForm;
                }
                return null;
            }

            @Override
            public ReviewRound getReviewRound(Object applicationId) {
                return null;
            }

        };

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);
        EasyMock.replay(userServiceMock);

        List<RegisteredUser> reviewersUsers = controller.getProgrammeReviewers("5");
        assertEquals(1, reviewersUsers.size());
        assertTrue(reviewersUsers.containsAll(Arrays.asList(interUser3)));
    }

    @Test
    public void shouldGetApplicationFromIdForAdmin() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("5");
    }

    @Test
    public void shouldGetListOfPreviousReviewersAndRemoveDefaultReviewersAndApplicantNominatedSupervisors() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser applicantNominatedSupervisor = new RegisteredUserBuilder().id(5).build();
        final RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser reviewer = new RegisteredUserBuilder().id(6).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().reviewers(defaultReviewer).id(6).build();

        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("5")) {
                    return applicationForm;
                }
                return null;
            }

            @Override
            public ReviewRound getReviewRound(Object applicationId) {
                return null;
            }

        };

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(applicantNominatedSupervisor).times(2);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(defaultReviewer).times(2);
        List<RegisteredUser> reviewerList = new ArrayList<RegisteredUser>();
        reviewerList.add(defaultReviewer);
        reviewerList.add(reviewer);
        EasyMock.expect(userServiceMock.getAllPreviousReviewersOfProgram(program)).andReturn(reviewerList);
        EasyMock.replay(userServiceMock);
        List<RegisteredUser> reviewersUsers = controller.getPreviousReviewers("5");
        assertEquals(1, reviewersUsers.size());
        assertTrue(reviewersUsers.contains(reviewer));
    }

    @Test
    public void shouldGetCurrentUserAsUser() {
        assertEquals(currentUserMock, controller.getUser());
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);
        reviewServiceMock = EasyMock.createMock(ReviewService.class);

        reviewerPropertyEditorMock = EasyMock.createMock(MoveToReviewReviewerPropertyEditor.class);
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResultMock);
        reviewRoundValidatorMock = EasyMock.createMock(ReviewRoundValidator.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        controller = new MoveToReviewController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, accessServiceMock, actionsProviderMock);

    }

}
