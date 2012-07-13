package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;

public class AccountControllerTest {

	private AccountController accountController;
	private UserService userServiceMock;
	private RegisteredUser student;

	private AccountValidator accountValidatorMock;
	private BindingResult bindingResultMock;
	private RegisteredUser curretnUser;

	@Test
	public void shouldBindValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(accountValidatorMock);
		EasyMock.replay(binderMock);
		accountController.registerValidator(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnMyAccountPage() {
		assertEquals("/private/my_account", accountController.getMyAccountPage());
	}

	@Test
	public void shouldReturnMyAccountSection() {
		assertEquals("/private/my_account_section", accountController.getMyAccountSection());
	}

	@Test
	public void shouldReturnToAccountPageAndNotSaveIfErrors() {
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		Assert.assertEquals("/private/my_account_section", accountController.saveAccountDetails(student, bindingResultMock));
	}

	@Test
	public void shouldSaveUserIfNoErrorsAccountIsChangedAndReturnAjaxOk() {
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		userServiceMock.updateCurrentUser(student);

		EasyMock.replay(bindingResultMock, userServiceMock);

		Assert.assertEquals("/private/common/ajax_OK", accountController.saveAccountDetails(student, bindingResultMock));

		EasyMock.verify(bindingResultMock, userServiceMock);
	}

	@Test
	public void shouldReturnCloneOfCurrentUserAsUpdatedUser() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student);
		EasyMock.replay(userServiceMock);
		RegisteredUser updateUser= accountController.getUpdatedUser();
		assertNotSame(updateUser, student);
		assertEquals(updateUser.getFirstName(), student.getFirstName());
		assertEquals(updateUser.getLastName(), student.getLastName());
		assertEquals(updateUser.getEmail(), student.getEmail());
		assertEquals(updateUser.getPassword(), student.getPassword());
	}

	@Before
	public void setUp() {

		userServiceMock = EasyMock.createMock(UserService.class);
		accountValidatorMock = EasyMock.createMock(AccountValidator.class);
		accountController = new AccountController(userServiceMock, accountValidatorMock);
		bindingResultMock = EasyMock.createMock(BindingResult.class);

		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").password("password").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();

		
	
		
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
