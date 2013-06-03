package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.propertyeditors.AssignReviewersReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

public class AssignReviewerControllerTest {

    private AssignReviewerController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;

    private ReviewService reviewServiceMock;

    private BindingResult bindingResultMock;

    private static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
    private static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
    private RegisteredUser currentUserMock;
    private AssignReviewersReviewerPropertyEditor reviewerPropertyEditorMock;
    private ReviewRoundValidator reviewRoundValidatorMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldGetReviewRoundPageWithOnlyAssignTrue() {
        ModelMap modelMap = new ModelMap();
        String reviewRoundDetailsPage = controller.getAssignReviewersPage(modelMap);
        Assert.assertEquals(REVIEW_DETAILS_VIEW_NAME, reviewRoundDetailsPage);
        Assert.assertTrue((Boolean) modelMap.get("assignOnly"));

    }

    @Test
    public void shouldGetReviewesSectionWithOnlyAssignTrue() {
        ModelMap modelMap = new ModelMap();
        String reviewersDetailsSection = controller.getReviewersSectionView(modelMap);
        Assert.assertEquals(REVIEWERS_SECTION_NAME, reviewersDetailsSection);
        Assert.assertTrue((Boolean) modelMap.get("assignOnly"));

    }

    @Test
    public void shouldReturnLatestReviewRoundIfApplicationForm() {
        Program program = new ProgramBuilder().id(6).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).latestReviewRound(reviewRound).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        assertEquals(reviewRound, controller.getReviewRound("5"));
    }

    @Test
    public void shouldSaveReviewRoundIfNoErrors() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(4).application(new ApplicationFormBuilder().id(1).applicationNumber("abc").build()).build();
        reviewServiceMock.save(reviewRound);
        EasyMock.replay(reviewServiceMock);
        String view = controller.assignReviewers(reviewRound, bindingResultMock);
        assertEquals("/private/common/ajax_OK", view);
        EasyMock.verify(reviewServiceMock);

    }

    @Test
    public void shouldSetToFlagForAdminNotifiesToNoIfNoErrors() {
        Reviewer reviewer3 = new ReviewerBuilder().id(1).requiresAdminNotification(CheckedStatus.NO).build();
        Reviewer reviewer1 = new ReviewerBuilder().id(null).requiresAdminNotification(CheckedStatus.NO).build();
        Reviewer reviewer2 = new ReviewerBuilder().id(2).requiresAdminNotification(CheckedStatus.YES).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2, reviewer3).id(4)
                .application(new ApplicationFormBuilder().id(1).applicationNumber("abc").build()).build();
        reviewServiceMock.save(reviewRound);
        EasyMock.replay(reviewServiceMock);
        String view = controller.assignReviewers(reviewRound, bindingResultMock);
        assertEquals("/private/common/ajax_OK", view);
        EasyMock.verify(reviewServiceMock);
        Assert.assertEquals(CheckedStatus.YES, reviewer1.getRequiresAdminNotification());
        Assert.assertEquals(CheckedStatus.YES, reviewer2.getRequiresAdminNotification());
    }

    @Test
    public void shouldReturnToViewAndNotSaveIfErros() {
        EasyMock.reset(bindingResultMock);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        EasyMock.replay(bindingResultMock);
        ReviewRound reviewRound = new ReviewRoundBuilder().id(4).build();
        EasyMock.replay(reviewServiceMock);
        String view = controller.assignReviewers(reviewRound, bindingResultMock);
        assertEquals(REVIEWERS_SECTION_NAME, view);
        EasyMock.verify(reviewServiceMock);

    }

    @Test
    public void shouldAddReviewRoundValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(reviewRoundValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        binderMock.registerCustomEditor(Reviewer.class, reviewerPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerReviewRoundValidator(binderMock);
        EasyMock.verify(binderMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        reviewServiceMock = EasyMock.createMock(ReviewService.class);
        reviewerPropertyEditorMock = EasyMock.createMock(AssignReviewersReviewerPropertyEditor.class);
        reviewRoundValidatorMock = EasyMock.createMock(ReviewRoundValidator.class);
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(userServiceMock, bindingResultMock);

        controller = new AssignReviewerController(applicationServiceMock, userServiceMock, reviewServiceMock, reviewRoundValidatorMock,
                reviewerPropertyEditorMock, actionsProviderMock);
    }

}
