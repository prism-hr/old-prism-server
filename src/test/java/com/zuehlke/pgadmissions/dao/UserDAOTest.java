package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class UserDAOTest extends AutomaticRollbackTestCase {
	private UserDAO userDAO;

	@Test
	public void shouldSaveAndLoadUser() throws Exception {

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		assertNull(user.getId());

		userDAO.save(user);

		assertNotNull(user.getId());
		Integer id = user.getId();
		RegisteredUser reloadedUser = userDAO.get(id);
		assertSame(user, reloadedUser);

		flushAndClearSession();

		reloadedUser = userDAO.get(id);
		assertNotSame(user, reloadedUser);
		assertEquals(user, reloadedUser);

	}

	@Test
	public void shouldFindUsersByUsername() throws Exception {

		RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(userOne, userTwo);

		flushAndClearSession();

		RegisteredUser foundUser = userDAO.getUserByUsername("username");
		assertEquals(userOne, foundUser);

	}

	@Test
	public void shouldFindUsersByActivationCode() throws Exception {

		RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).activationCode("abc")
				.toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).activationCode("def")
				.toUser();

		save(userOne, userTwo);

		flushAndClearSession();

		RegisteredUser foundUser = userDAO.getUserByActivationCode("abc");
		assertEquals(userOne, foundUser);

	}

	@Test
	public void shouldGetUsersByRole() {
		// clear out whatever test data is in there -remember, it will all be
		// rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

		Role roleOne = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		Role roleTwo = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		save(roleOne, roleTwo);
		flushAndClearSession();

		RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleOne).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).roles(roleOne, roleTwo)
				.toUser();

		save(userOne, userTwo);

		flushAndClearSession();

		List<RegisteredUser> usersInRole = userDAO.getUsersInRole(roleTwo);
		assertEquals(1, usersInRole.size());
		assertEquals(userTwo, usersInRole.get(0));

		usersInRole = userDAO.getUsersInRole(roleOne);
		assertEquals(2, usersInRole.size());
		assertTrue(usersInRole.containsAll(Arrays.asList(userOne, userTwo)));

	}

	@Test
	public void shouldGetUsersByProgramme() {
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role superAdminRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
		
		int numberOfExistingSuperAdminUsers = userDAO.getUsersInRole(superAdminRole).size();
		
		RegisteredUser superAdminOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(superAdminRole)
				.toUser();
		RegisteredUser superAdminTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).roles(superAdminRole)
				.toUser();
		RegisteredUser superAdminThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username3")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).roles(superAdminRole)
				.toUser();
		
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username4")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username5")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username6")				
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		
		
		RegisteredUser reviewerOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username7")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser reviewerTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username8")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser reviewerThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username9")				
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		
		RegisteredUser administratorOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username10")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username11")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser administratorThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username12")				
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		RegisteredUser reviewerAndApprover = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username13")				
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		
		
		save(superAdminOne, superAdminTwo, superAdminThree, administratorOne, administratorThree,administratorTwo, approverOne, approverThree,approverTwo, reviewerOne, reviewerThree, reviewerTwo, reviewerAndApprover);
		
		flushAndClearSession();

		Program programOne = new ProgramBuilder().administrator(administratorOne, administratorThree).approver(approverOne, approverThree, reviewerAndApprover).reviewers(reviewerOne, reviewerThree, reviewerAndApprover).code("111111").description("hello").title("hello").toProgram();
		Program programTwo = new ProgramBuilder().administrator(administratorTwo).approver(approverTwo).reviewers(reviewerTwo).code("222222").description("hello").title("hello").toProgram();
		
		save(programOne, programTwo);
		
		flushAndClearSession();
		
		List<RegisteredUser> usersInProgram = userDAO.getUsersForProgram(programOne);
		assertEquals(numberOfExistingSuperAdminUsers + 10, usersInProgram.size());
		assertTrue(usersInProgram.containsAll(Arrays.asList(reviewerAndApprover, superAdminOne, superAdminThree, superAdminTwo, reviewerOne,reviewerThree, approverOne, approverThree, administratorOne, administratorThree)));

	}

	@Before
	public void setup() {
		userDAO = new UserDAO(sessionFactory);

	}
}
