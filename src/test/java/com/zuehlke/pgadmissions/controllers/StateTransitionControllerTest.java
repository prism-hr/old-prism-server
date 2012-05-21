package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;

public class StateTransitionControllerTest {

	private StateTransitionController controller;
	private ApplicationsService applicationServiceMock;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private UserService userServiceMock;
	private CommentFactory commentFactoryMock;
	private CommentService commentServiceMock;

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		final RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUserMock);
		controller =  new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock){

			@Override
			RegisteredUser getCurrentUser() {
				return currentUserMock;
			}
			
		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);
	
	
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioNDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminInApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		final RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);		
		authenticationToken.setDetails(currentUserMock);
		controller =  new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock){

			@Override
			RegisteredUser getCurrentUser() {
				return currentUserMock;
			}
			
		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm(5);
	}

	@Test
	public void shouldReturnCurrentUserRefreshed(){
		RegisteredUser currentUser = new RegisteredUserBuilder().id(4).toUser();		
		authenticationToken.setDetails(currentUser);
		RegisteredUser refreshedUser = new RegisteredUserBuilder().toUser();
		EasyMock.expect(userServiceMock.getUser(4)).andReturn(refreshedUser);
		EasyMock.replay(userServiceMock);
		assertSame(refreshedUser, controller.getUser());
	}
	
	@Test
	public void shouldReturnAvaialableNextStati() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		controller = new StateTransitionController(applicationServiceMock,userServiceMock, commentServiceMock, commentFactoryMock){

			@Override
			public ApplicationForm getApplicationForm(Integer application) {
				return applicationForm;
			}
			
		};
		assertArrayEquals(ApplicationFormStatus.getAvailableNextStati(ApplicationFormStatus.VALIDATION), controller.getAvailableNextStati(5));
	}

	@Test
	public void shouldReturnStateTransitionView(){
		assertEquals("private/staff/admin/state_transition", controller.getStateTransitionView());
	}
	
	@Test
	public void shouldCreateCommentAndSave(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "comment";
		Comment comment = new CommentBuilder().id(6).toComment();
		CommentType type = CommentType.VALIDATION;
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, user, strComment, type)).andReturn(comment);
		commentServiceMock.save(comment);
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		assertEquals("private/common/simpleMessage", controller.addComment(applicationForm, user, type, strComment));
		EasyMock.verify(commentServiceMock);
	}
	
	@Test
	public void shouldNotCreateCommentORSaveIfCommentParameterIsBlank(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		String strComment = "";		
		CommentType type = CommentType.VALIDATION;
		
		EasyMock.replay(commentFactoryMock, commentServiceMock);
		controller.addComment(applicationForm, user, type, strComment);
		EasyMock.verify(commentServiceMock);
	}
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentServiceMock = EasyMock.createMock(CommentService.class);
		controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
