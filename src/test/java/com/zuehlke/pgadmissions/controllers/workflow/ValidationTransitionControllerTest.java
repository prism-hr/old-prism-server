package com.zuehlke.pgadmissions.controllers.workflow;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.google.common.base.Strings;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class ValidationTransitionControllerTest {

	private static final String DATE_FORMAT = "yyyy/MM/dd";
    private ValidationTransitionController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private CommentFactory commentFactoryMock;
	private CommentService commentServiceMock;
	private StateTransitionService stateTransitionServiceMock;
	private EncryptionHelper encryptionHelperMock;
	private DocumentService documentServiceMock;
	private ApprovalService approvalServiceMock;
	private StateChangeValidator stateChangeValidatorMock;
	private DocumentPropertyEditor documentPropertyEditorMock;
	private BindingResult bindingResultMock;
	private BadgeService badgeServiceMock;
	private MessageSource messageSourceMock;
	private ApplicationFormAccessService accessServiceMock;
	
	@Test
	public void shouldReturnAllValidationQuestionOptions() {
		assertArrayEquals(ValidationQuestionOptions.values(), controller.getValidationQuestionOptions());
	}

	@Test
	public void shouldReturnHomeOrOverseasOptions() {
		assertArrayEquals(HomeOrOverseas.values(), controller.getHomeOrOverseasOptions());
	}
	
	@Test
	public void shouldResolveViewForApplicationForm() {
	    RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).build();
		EasyMock.expect(stateTransitionServiceMock.resolveView(applicationForm)).andReturn("view");
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
		EasyMock.replay(stateTransitionServiceMock, userServiceMock);
		assertEquals("view", controller.getStateTransitionView(applicationForm));
		EasyMock.verify(stateTransitionServiceMock, userServiceMock);
	}
	
	@Test
	public void shouldResolveViewForApplicationFormAsAdmitter() {
	    RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).build();
	    ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).build();
	    EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
	    EasyMock.replay(userServiceMock);
	    assertEquals("private/staff/admin/state_transition", controller.getStateTransitionView(applicationForm));
	    EasyMock.verify(userServiceMock);
	}
	
	@Test
	public void shouldRejectClosingDateIfDateIsInThePast() {
	    Program program = new ProgramBuilder().id(1).build();
	    final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
	    ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.UNSURE).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).build();
	    RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
	    
	    EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
	    
	    controller = new ValidationTransitionController(
	            applicationServiceMock, 
	            userServiceMock, 
	            commentServiceMock, 
	            commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock,
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };
        
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        Date twoMonthsAgo = DateUtils.addMonths(new Date(), -2);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, badgeServiceMock);
        String view = controller.addComment(applicationForm.getApplicationNumber(), format.format(twoMonthsAgo), "projectTitle", comment, bindingResultMock, new ModelMap(), false, delegatedInterviewer);
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, badgeServiceMock);
        assertEquals("private/staff/admin/state_transition", view);
	}
	
	@Test
    public void shouldRejectProjectTitleIfLongerThan500() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.UNSURE).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).build();
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };
        
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        String projectTitle = Strings.repeat("a", 600);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, badgeServiceMock);
        String view = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), projectTitle.toString(), comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, badgeServiceMock);
        assertEquals("private/staff/admin/state_transition", view);
    }
	
	@Test
    public void shouldAllowClosingDateInThePastWithin1MonthFromNow() {
	    Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).build();
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        Date oneMonthAgo = org.apache.commons.lang.time.DateUtils.addMonths(Calendar.getInstance().getTime(), -1);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.makeApplicationNotEditable(applicationForm);
        
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        
        commentServiceMock.save(comment);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        controller.addComment(applicationForm.getApplicationNumber(), format.format(oneMonthAgo), "projectTitle", comment, bindingResultMock, new ModelMap(), false, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
    }
	
    @Test
    public void shouldAllowClosingDateInThePastIfDateExistsInBadgeClosingDate() throws ParseException {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).build();
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        Date twoMontshAgo = org.apache.commons.lang.time.DateUtils.addMonths(Calendar.getInstance().getTime(), -1);
        
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(Arrays.asList(twoMontshAgo));
        
        commentServiceMock.save(comment);
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.makeApplicationNotEditable(applicationForm);
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        controller.addComment(applicationForm.getApplicationNumber(), format.format(twoMontshAgo), "projectTitle", comment, bindingResultMock, new ModelMap(), false, delegatedInterviewer);
        EasyMock.verify(commentServiceMock);
    }	
	
	@Test
	public void shouldCreateValidationCommentWithQuestionaluesIfNoValidationErrors() {
	    Program program = new ProgramBuilder().id(1).build();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
		ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO).englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.APPROVAL).comment("comment").type(CommentType.VALIDATION).id(6).build();
		RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
		
		EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
		
		commentServiceMock.save(comment);
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
		};

		DateFormat format = new SimpleDateFormat("dd MMM yyyy");
		
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		badgeServiceMock.save(EasyMock.anyObject(Badge.class));
		applicationServiceMock.save(applicationForm);
		applicationServiceMock.makeApplicationNotEditable(applicationForm);
		EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
		String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
		EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
		
		assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
	}
	
	@Test
	public void shouldCreateCommentWithDocumentsAndSaveAndRedirectToResolvedView() {
	    Program program = new Program();
        program.setId(1);
        
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
		Document documentOne = new DocumentBuilder().id(1).build();
		Document documentTwo = new DocumentBuilder().id(2).build();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock) {
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		DateFormat format = new SimpleDateFormat("dd MMM yyyy");
		
		ValidationComment comment = new ValidationCommentBuilder().comment("comment").type(CommentType.VALIDATION).documents(documentOne, documentTwo).id(6).build();
		RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
		badgeServiceMock.save(EasyMock.anyObject(Badge.class));
		commentServiceMock.save(comment);
		EasyMock.expect(stateTransitionServiceMock.resolveView(applicationForm)).andReturn("view");
		EasyMock.replay(commentServiceMock, stateTransitionServiceMock, encryptionHelperMock, documentServiceMock, badgeServiceMock);

		assertEquals("view", controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), false, delegatedInterviewer));

		EasyMock.verify(commentServiceMock, stateTransitionServiceMock, encryptionHelperMock, documentServiceMock, badgeServiceMock);
		assertEquals(2, comment.getDocuments().size());
		assertTrue(comment.getDocuments().containsAll(Arrays.asList(documentOne, documentTwo)));
	}
	
	@Test
	public void shouldAddApplicationFormClosingDateIfExistInPast() throws ParseException {
		Program program = new Program();
		Date pastDate = new SimpleDateFormat(DATE_FORMAT).parse("2003/09/09");
		final ApplicationForm applicationForm = new ApplicationFormBuilder().batchDeadline(pastDate).id(1).program(program).build();
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
				
		};
		DateTime now = new DateTime();
		EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>(Arrays.asList(now.toDate())));
		EasyMock.replay(badgeServiceMock);
		assertTrue(controller.getClosingDates(applicationForm.getApplicationNumber()).contains(pastDate));
		EasyMock.verify(badgeServiceMock);
	}
	
	@Test
	public void shouldAddApplicationFormProjectTitleIfExistAndClosingDateInPast() throws ParseException {
		Program program = new Program();
		Date pastDate = new SimpleDateFormat(DATE_FORMAT).parse("2003/09/09");
		final ApplicationForm applicationForm = new ApplicationFormBuilder().projectTitle("title").batchDeadline(pastDate).id(1).program(program).build();
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock){
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
			
		};
		DateTime now = new DateTime();
		EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>(Arrays.asList(now.toDate())));
		EasyMock.replay(badgeServiceMock);
		assertTrue(controller.getClosingDates(applicationForm.getApplicationNumber()).contains(pastDate));
	}
	
	@Test
	public void shouldAddAPplicationFormProjectTitleIfExistAndClosingDateDontExist() throws ParseException {
		Program program = new Program();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().projectTitle("title").id(1).program(program).build();
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock) {
			@Override
			public ApplicationForm getApplicationForm( String applicationId) {
				return applicationForm;
			}
			
		};
		DateTime now = new DateTime();
		EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(Arrays.asList(now.toDate()));
		EasyMock.replay(badgeServiceMock);
		assertTrue(controller.getClosingDates(applicationForm.getApplicationNumber()).contains(now.toDate()));
		EasyMock.verify(badgeServiceMock);
	}
	
	@Test
	public void shouldNotifyRegistryIfHomeOrOverseasIsUnsureAndNextStateIsNotReject() throws ParseException {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder()
                .id(6)
                .qualifiedForPhd(ValidationQuestionOptions.NO)
                .englishCompentencyOk(ValidationQuestionOptions.NO)
                .homeOrOverseas(HomeOrOverseas.UNSURE)
                .nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment")
                .type(CommentType.VALIDATION)
                .build();
        
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        commentServiceMock.save(comment);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(new RegisteredUserBuilder().build());
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.save(applicationForm);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
       
        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
        
        assertNotNull(applicationForm.getAdminRequestedRegistry());
	}
	
	@Test
	public void shouldNotifyRegistryIfQualifiedForPhdIsUnsureAndNextStateIsNotReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder()
                .id(6)
                .qualifiedForPhd(ValidationQuestionOptions.UNSURE)
                .englishCompentencyOk(ValidationQuestionOptions.NO)
                .homeOrOverseas(HomeOrOverseas.HOME)
                .nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment")
                .type(CommentType.VALIDATION)
                .build();
        
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        commentServiceMock.save(comment);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(new RegisteredUserBuilder().build());
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.save(applicationForm);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
       
        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertNotNull(applicationForm.getAdminRequestedRegistry());
	}
	
	@Test
	public void shouldNotifyRegistryIfEnglishCompentencyIsUnsureAndNextStateIsNotReject() {
	    Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder()
                .id(6)
                .qualifiedForPhd(ValidationQuestionOptions.YES)
                .englishCompentencyOk(ValidationQuestionOptions.UNSURE)
                .homeOrOverseas(HomeOrOverseas.HOME)
                .nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment")
                .type(CommentType.VALIDATION)
                .build();
        
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        commentServiceMock.save(comment);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(new RegisteredUserBuilder().build());
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.save(applicationForm);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
       
        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertNotNull(applicationForm.getAdminRequestedRegistry());
	}
	
	@Test
    public void shouldNotNotifyRegistryIfHomeOrOverseasIsUnsureAndNextStateIsReject() {
	       Program program = new ProgramBuilder().id(1).build();
	        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
	        ValidationComment comment = new ValidationCommentBuilder()
	                .id(6)
	                .qualifiedForPhd(ValidationQuestionOptions.YES)
	                .englishCompentencyOk(ValidationQuestionOptions.NO)
	                .homeOrOverseas(HomeOrOverseas.UNSURE)
	                .nextStatus(ApplicationFormStatus.REJECTED)
	                .comment("comment")
	                .type(CommentType.VALIDATION)
	                .build();
	        
	        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
	        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
	        
	        commentServiceMock.save(comment);
	        
	        controller = new ValidationTransitionController(
	                applicationServiceMock, 
	                userServiceMock, 
	                commentServiceMock, 
	                commentFactoryMock,
	                encryptionHelperMock,
	                documentServiceMock, 
	                approvalServiceMock, 
	                stateChangeValidatorMock, 
	                documentPropertyEditorMock, 
	                badgeServiceMock, 
	                messageSourceMock,
	                stateTransitionServiceMock,
	                accessServiceMock){
	            @Override
	            public ApplicationForm getApplicationForm( String applicationId) {
	                return applicationForm;
	            }
	        };

	        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
	        
	        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
	        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
	        applicationServiceMock.save(applicationForm);
	        
	        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
	       
	        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
	        
	        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
	        
	        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
	        
	        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));
    }
    
    @Test
    public void shouldNotNotifyRegistryIfQualifiedForPhdIsUnsureAndNextStateIsReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder()
                .id(6)
                .qualifiedForPhd(ValidationQuestionOptions.UNSURE)
                .englishCompentencyOk(ValidationQuestionOptions.NO)
                .homeOrOverseas(HomeOrOverseas.HOME)
                .nextStatus(ApplicationFormStatus.REJECTED)
                .comment("comment")
                .type(CommentType.VALIDATION)
                .build();
        
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        commentServiceMock.save(comment);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
       
        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
        
        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));
    }
    
    @Test
    public void shouldNotNotifyRegistryIfEnglishCompentencyIsUnsureAndNextStateIsReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder()
                .id(6)
                .qualifiedForPhd(ValidationQuestionOptions.NO)
                .englishCompentencyOk(ValidationQuestionOptions.UNSURE)
                .homeOrOverseas(HomeOrOverseas.HOME)
                .nextStatus(ApplicationFormStatus.REJECTED)
                .comment("comment")
                .type(CommentType.VALIDATION)
                .build();
        
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        EasyMock.expect(badgeServiceMock.getAllClosingDatesByProgram(program)).andReturn(new ArrayList<Date>());
        
        commentServiceMock.save(comment);
        
        controller = new ValidationTransitionController(
                applicationServiceMock, 
                userServiceMock, 
                commentServiceMock, 
                commentFactoryMock,
                encryptionHelperMock,
                documentServiceMock, 
                approvalServiceMock, 
                stateChangeValidatorMock, 
                documentPropertyEditorMock, 
                badgeServiceMock, 
                messageSourceMock,
                stateTransitionServiceMock,
                accessServiceMock){
            @Override
            public ApplicationForm getApplicationForm( String applicationId) {
                return applicationForm;
            }
        };

        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        badgeServiceMock.save(EasyMock.anyObject(Badge.class));
        applicationServiceMock.save(applicationForm);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
       
        String result = controller.addComment(applicationForm.getApplicationNumber(), format.format(new Date()), "projectTitle", comment, bindingResultMock, new ModelMap(), true, delegatedInterviewer);
        
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, badgeServiceMock);
        
        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
        
        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));
        
    }
	
	@Before
	public void setUp() {
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		stateChangeValidatorMock = EasyMock.createMock(StateChangeValidator.class);
		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		stateTransitionServiceMock = EasyMock.createMock(StateTransitionService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		badgeServiceMock = EasyMock.createMock(BadgeService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
		controller = new ValidationTransitionController(
		        applicationServiceMock, 
		        userServiceMock, 
		        commentServiceMock, 
		        commentFactoryMock,
				encryptionHelperMock,
				documentServiceMock, 
				approvalServiceMock, 
				stateChangeValidatorMock, 
				documentPropertyEditorMock, 
				badgeServiceMock, 
				messageSourceMock,
				stateTransitionServiceMock,
				accessServiceMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
