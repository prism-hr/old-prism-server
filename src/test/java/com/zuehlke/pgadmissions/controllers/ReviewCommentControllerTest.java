package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class ReviewCommentControllerTest {

    private ApplicationsService applicationsServiceMock;
    private UserService userServiceMock;
    private ReviewCommentController controller;
    private FeedbackCommentValidator reviewFeedbackValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Test
    public void shouldGetApplicationFormFromId() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        ;
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentUserNotReviewerOfForm() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(false);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");

    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentUserCannotSeeApplication() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");

    }

    @Test
    public void shouldReturnGenericCommentPage() {
        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage());
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewReviewCommentForApplicationForm() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        Reviewer reviewer = new ReviewerBuilder().id(5).build();
        EasyMock.expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        EasyMock.replay(userServiceMock, currentUser);
        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                return applicationForm;
            }

            @Override
            public RegisteredUser getUser() {
                return currentUser;
            }

        };
        ReviewComment comment = controller.getComment("5");

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.REVIEW, comment.getType());
        assertEquals(reviewer, comment.getReviewer());

    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(reviewFeedbackValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerBinders(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnToCommentsPageIfErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        ReviewComment comment = new ReviewCommentBuilder().application(new ApplicationForm()).build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);
        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.addComment(comment, errorsMock));
    }

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowResourceNotFoundIfApplicationAlreadyDecided() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        ReviewComment comment = new ReviewCommentBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build()).build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);
        controller.addComment(comment, errorsMock);
    }

    @Test
    public void shouldSaveCommentAndRedirectApplicationsPagefNoErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).build();
        ReviewComment comment = new ReviewCommentBuilder().id(1).application(applicationForm).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        commentServiceMock.save(comment);
        EasyMock.replay(errorsMock, commentServiceMock);
        assertEquals("redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber(),
                controller.addComment(comment, errorsMock));
        EasyMock.verify(errorsMock, commentServiceMock);

    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        reviewFeedbackValidatorMock = EasyMock.createMock(FeedbackCommentValidator.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock);

    }
}
