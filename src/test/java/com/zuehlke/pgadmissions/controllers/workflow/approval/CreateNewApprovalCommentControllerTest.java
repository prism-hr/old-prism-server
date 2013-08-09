package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Arrays;
import java.util.Locale;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalCommentValidator;

public class CreateNewApprovalCommentControllerTest {

    private ApplicationsService applicationsServiceMock;

    private UserService userServiceMock;

    private ApprovalService approvalServiceMock;

    private CommentService commentServiceMock;

    private ApprovalCommentValidator validatorMock;

    private MessageSource messageSourceMock;

    private BindingResult bindingResultMock;

    private ApplicationFormAccessService accessServiceMock;

    private CreateNewApprovalCommentController controller;

    private ProgramsService programServiceMock;

    @Before
    public void setup() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        validatorMock = EasyMock.createMock(ApprovalCommentValidator.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        programServiceMock = EasyMock.createMock(ProgramsService.class);
        controller = new CreateNewApprovalCommentController(applicationsServiceMock, userServiceMock, approvalServiceMock, commentServiceMock, validatorMock,
                messageSourceMock, accessServiceMock, programServiceMock);
    }

    @Test
    public void shouldThrowInsufficientApplicationFormPrivilegesExceptionWhenRequestingThePage() {
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.UNSUBMITTED)
                .approvalRounds(new ApprovalRoundBuilder().build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);

        try {
            controller.get(form.getApplicationNumber());
            Assert.fail("Should have thrown InsufficientApplicationFormPrivilegesException");
        } catch (InsufficientApplicationFormPrivilegesException e) {
            // do nothing
        }

        EasyMock.verify(applicationsServiceMock, userServiceMock);
    }

    @Test
    public void shouldReturnEmptyMapIfNoApprovalRound() {
        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.REVIEW)
                .approvalRounds(new ApprovalRoundBuilder().build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);

        String json = controller.get(form.getApplicationNumber());

        Assert.assertEquals("{\"success\":true}", json);
        EasyMock.verify(applicationsServiceMock, userServiceMock);
    }

    @Test
    public void shouldReturnLatestApprovalRoundAsJson() {
        DateTime now = new DateTime();
        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm form = new ApplicationFormBuilder()
                .applicationNumber("ABCD-XXX")
                .status(ApplicationFormStatus.REVIEW)
                .latestApprovalRound(
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract").projectDescriptionAvailable(true)
                                .recommendedConditions("recommendedConditions").recommendedConditionsAvailable(true).recommendedStartDate(now.toDate())
                                .projectAcceptingApplications(true).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);

        String json = controller.get(form.getApplicationNumber());

        Assert.assertEquals(
                "{\"projectTitle\":\"projectTitle\",\"projectAbstract\":\"projectAbstract\",\"projectDescriptionAvailable\":\"true\",\"recommendedConditionsAvailable\":\"true\",\"recommendedConditions\":\"recommendedConditions\",\"recommendedStartDate\":\""
                        + now.toString("dd MMM yyyy") + "\",\"projectAcceptingApplications\":\"true\"}", json);
        EasyMock.verify(applicationsServiceMock, userServiceMock);
    }

    @Test
    public void shouldThrowInsufficientApplicationFormPrivilegesExceptionWhenPostingToThePage() {
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.UNSUBMITTED)
                .approvalRounds(new ApprovalRoundBuilder().build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);

        try {
            controller.save(form.getApplicationNumber(), null, null, null);
            Assert.fail("Should have thrown InsufficientApplicationFormPrivilegesException");
        } catch (InsufficientApplicationFormPrivilegesException e) {
            // do nothing
        }

        EasyMock.verify(applicationsServiceMock, userServiceMock);
    }

    @Test
    public void shouldNotValidateIfCommentOrNextStageIsEmpty() {
        DateTime now = new DateTime();

        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();

        ApplicationForm form = new ApplicationFormBuilder()
                .applicationNumber("ABCD-XXX")
                .status(ApplicationFormStatus.REVIEW)
                .latestApprovalRound(
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract").projectDescriptionAvailable(true)
                                .recommendedConditions("recommendedConditions").recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .build();

        ApprovalComment newComment = new ApprovalCommentBuilder().projectTitle("1").projectAbstract("1").projectDescriptionAvailable(false)
                .recommendedConditions("1").recommendedConditionsAvailable(false).recommendedStartDate(now.plusDays(1).toDate()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        FieldError fieldError = new FieldError(Comment.class.getName(), "comment", "");
        EasyMock.expect(bindingResultMock.getFieldErrors()).andReturn(Arrays.asList(fieldError));

        EasyMock.expect(messageSourceMock.getMessage(fieldError, Locale.getDefault())).andReturn(null);
        EasyMock.expect(messageSourceMock.getMessage("text.field.empty", null, Locale.getDefault())).andReturn(null);

        EasyMock.replay(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, messageSourceMock);

        String json = controller.validate(form.getApplicationNumber(), newComment, bindingResultMock, "", "", null);
        Assert.assertEquals("{\"success\":false}", json);

        EasyMock.verify(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, messageSourceMock);
    }

    @Test
    public void shouldUpdateTheLatestApprovalRoundWithNewestValues() {
        DateTime now = new DateTime();

        Capture<ApprovalRound> approvalRoundCapture = new Capture<ApprovalRound>();
        Capture<ApprovalComment> approvalCommentCapture = new Capture<ApprovalComment>();

        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();

        ApplicationForm form = new ApplicationFormBuilder()
                .applicationNumber("ABCD-XXX")
                .status(ApplicationFormStatus.REVIEW)
                .latestApprovalRound(
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract").projectDescriptionAvailable(true)
                                .recommendedConditions("recommendedConditions").recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .build();

        ApprovalComment newComment = new ApprovalCommentBuilder().projectTitle("1").projectAbstract("1").projectDescriptionAvailable(false)
                .recommendedConditions("1").recommendedConditionsAvailable(false).recommendedStartDate(now.plusDays(1).toDate()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        approvalServiceMock.save(EasyMock.capture(approvalRoundCapture));
        commentServiceMock.save(EasyMock.capture(approvalCommentCapture));
        applicationsServiceMock.save(form);
        EasyMock.replay(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock);

        String json = controller.save(form.getApplicationNumber(), newComment, bindingResultMock, null);
        Assert.assertEquals("{\"success\":true}", json);

        EasyMock.verify(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock);

        ApprovalRound updatedApprovalRound = approvalRoundCapture.getValue();
        Assert.assertEquals("1", updatedApprovalRound.getProjectTitle());
        Assert.assertEquals("1", updatedApprovalRound.getProjectAbstract());
        Assert.assertEquals(false, updatedApprovalRound.getProjectDescriptionAvailable());
        Assert.assertEquals("1", updatedApprovalRound.getRecommendedConditions());
        Assert.assertEquals(false, updatedApprovalRound.getRecommendedConditionsAvailable());
        Assert.assertEquals(now.plusDays(1).toDate(), updatedApprovalRound.getRecommendedStartDate());

        ApprovalComment newApprovalComment = approvalCommentCapture.getValue();
        Assert.assertEquals(CommentType.APPROVAL, newApprovalComment.getType());
        Assert.assertEquals("1", newApprovalComment.getProjectTitle());
        Assert.assertEquals("1", newApprovalComment.getProjectAbstract());
        Assert.assertEquals(false, newApprovalComment.getProjectDescriptionAvailable());
        Assert.assertEquals("1", newApprovalComment.getRecommendedConditions());
        Assert.assertEquals(false, newApprovalComment.getRecommendedConditionsAvailable());
        Assert.assertEquals(now.plusDays(1).toDate(), newApprovalComment.getRecommendedStartDate());
    }

    @Test
    public void shouldNotValidateIfProjectExistsAndAcceptingApplicationsIsEmpty() {
        DateTime now = new DateTime();

        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm form = new ApplicationFormBuilder()
                .applicationNumber("ABCD-XXX")
                .status(ApplicationFormStatus.REVIEW)
                .latestApprovalRound(
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract").projectDescriptionAvailable(true)
                                .recommendedConditions("recommendedConditions").recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .project(new ProjectBuilder().id(1).advert(new AdvertBuilder().id(1).build()).build()).build();

        ApprovalComment newComment = new ApprovalCommentBuilder().projectTitle("1").projectAbstract("1").projectDescriptionAvailable(false)
                .recommendedConditions("1").recommendedConditionsAvailable(false).recommendedStartDate(now.plusDays(1).toDate()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        FieldError fieldError = new FieldError(Comment.class.getName(), "acceptingApplications", "");
        EasyMock.expect(bindingResultMock.getFieldErrors()).andReturn(Arrays.asList(fieldError));
        EasyMock.expect(messageSourceMock.getMessage(fieldError, Locale.getDefault())).andReturn(null);
        EasyMock.expect(messageSourceMock.getMessage("dropdown.radio.select.none", null, Locale.getDefault())).andReturn(null);

        EasyMock.replay(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, messageSourceMock);
        String json = controller.validate(form.getApplicationNumber(), newComment, bindingResultMock, "adf", "true", null);
        EasyMock.verify(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, messageSourceMock);

        Assert.assertEquals("{\"success\":false}", json);
    }

    @Test
    public void shouldUpdateProjectActiveValue() {
        DateTime now = new DateTime();

        Capture<ApprovalRound> approvalRoundCapture = new Capture<ApprovalRound>();
        Capture<ApprovalComment> approvalCommentCapture = new Capture<ApprovalComment>();
        Capture<Project> projectCapture = new Capture<Project>();

        RegisteredUser currentUser = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();

        ApplicationForm form = new ApplicationFormBuilder()
                .applicationNumber("ABCD-XXX")
                .status(ApplicationFormStatus.REVIEW)
                .latestApprovalRound(
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract").projectDescriptionAvailable(true)
                                .recommendedConditions("recommendedConditions").recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .project(new ProjectBuilder().id(1).advert(new AdvertBuilder().id(1).build()).build()).build();

        ApprovalComment newComment = new ApprovalCommentBuilder().projectTitle("1").projectAbstract("1").projectDescriptionAvailable(false)
                .recommendedConditions("1").recommendedConditionsAvailable(false).recommendedStartDate(now.plusDays(1).toDate()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        approvalServiceMock.save(EasyMock.capture(approvalRoundCapture));
        commentServiceMock.save(EasyMock.capture(approvalCommentCapture));
        programServiceMock.saveProject(EasyMock.capture(projectCapture));
        applicationsServiceMock.save(form);
        EasyMock.replay(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, programServiceMock);

        String json = controller.save(form.getApplicationNumber(), newComment, bindingResultMock, false);
        Assert.assertEquals("{\"success\":true}", json);

        EasyMock.verify(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock, programServiceMock);

        ApprovalRound updatedApprovalRound = approvalRoundCapture.getValue();
        Assert.assertEquals("1", updatedApprovalRound.getProjectTitle());
        Assert.assertEquals("1", updatedApprovalRound.getProjectAbstract());
        Assert.assertEquals(false, updatedApprovalRound.getProjectDescriptionAvailable());
        Assert.assertEquals("1", updatedApprovalRound.getRecommendedConditions());
        Assert.assertEquals(false, updatedApprovalRound.getRecommendedConditionsAvailable());
        Assert.assertEquals(now.plusDays(1).toDate(), updatedApprovalRound.getRecommendedStartDate());

        ApprovalComment newApprovalComment = approvalCommentCapture.getValue();
        Assert.assertEquals(CommentType.APPROVAL, newApprovalComment.getType());
        Assert.assertEquals("1", newApprovalComment.getProjectTitle());
        Assert.assertEquals("1", newApprovalComment.getProjectAbstract());
        Assert.assertEquals(false, newApprovalComment.getProjectDescriptionAvailable());
        Assert.assertEquals("1", newApprovalComment.getRecommendedConditions());
        Assert.assertEquals(false, newApprovalComment.getRecommendedConditionsAvailable());
        Assert.assertEquals(now.plusDays(1).toDate(), newApprovalComment.getRecommendedStartDate());

        Project updatedProject = projectCapture.getValue();
        Assert.assertEquals(false, updatedProject.getAdvert().getActive());
    }
}
