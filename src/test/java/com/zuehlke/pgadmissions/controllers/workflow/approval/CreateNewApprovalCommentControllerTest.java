package com.zuehlke.pgadmissions.controllers.workflow.approval;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
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
    
    private CreateNewApprovalCommentController controller;
    
    @Before
    public void setup() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        validatorMock = EasyMock.createMock(ApprovalCommentValidator.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        controller = new CreateNewApprovalCommentController(applicationsServiceMock, 
                userServiceMock, approvalServiceMock,
                commentServiceMock, validatorMock, messageSourceMock);
    }
    
    @Test
    public void shouldThrowInsufficientApplicationFormPrivilegesExceptionWhenRequestingThePage() {
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.UNSUBMITTED).approvalRounds(new ApprovalRoundBuilder().build()).build();
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
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.REVIEW).approvalRounds(new ApprovalRoundBuilder().build()).build();
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
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract")
                                .projectDescriptionAvailable(true).recommendedConditions("recommendedConditions")
                                .recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);
        
        String json = controller.get(form.getApplicationNumber());
        
        Assert.assertEquals("{\"projectTitle\":\"projectTitle\",\"projectAbstract\":\"projectAbstract\",\"projectDescriptionAvailable\":\"true\",\"recommendedConditionsAvailable\":\"true\",\"recommendedConditions\":\"recommendedConditions\",\"recommendedStartDate\":\"" + now.toString("dd MMM yyyy") +"\"}", json);
        EasyMock.verify(applicationsServiceMock, userServiceMock);
    }
    
    @Test
    public void shouldThrowInsufficientApplicationFormPrivilegesExceptionWhenPostingToThePage() {
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("ABCD-XXX").status(ApplicationFormStatus.UNSUBMITTED).approvalRounds(new ApprovalRoundBuilder().build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.replay(applicationsServiceMock, userServiceMock);
        
        try {
            controller.save(form.getApplicationNumber(), null, null);
            Assert.fail("Should have thrown InsufficientApplicationFormPrivilegesException");
        } catch (InsufficientApplicationFormPrivilegesException e) {
            // do nothing
        }
        
        EasyMock.verify(applicationsServiceMock, userServiceMock);
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
                        new ApprovalRoundBuilder().projectTitle("projectTitle").projectAbstract("projectAbstract")
                                .projectDescriptionAvailable(true).recommendedConditions("recommendedConditions")
                                .recommendedConditionsAvailable(true).recommendedStartDate(now.toDate()).build())
                .build();
        
        ApprovalComment newComment = new ApprovalCommentBuilder().projectTitle("1").projectAbstract("1")
                .projectDescriptionAvailable(false).recommendedConditions("1")
                .recommendedConditionsAvailable(false).recommendedStartDate(now.plusDays(1).toDate()).build();
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        approvalServiceMock.save(EasyMock.capture(approvalRoundCapture));
        commentServiceMock.save(EasyMock.capture(approvalCommentCapture));
        EasyMock.replay(userServiceMock, applicationsServiceMock, bindingResultMock, approvalServiceMock, commentServiceMock);
        
        String json = controller.save(form.getApplicationNumber(), newComment, bindingResultMock);
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
}
