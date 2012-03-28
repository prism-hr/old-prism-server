package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RegisteredUserMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadUserWithSimpleValues() throws Exception {

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		assertNull(user.getId());

		sessionFactory.getCurrentSession().save(user);

		assertNotNull(user.getId());
		Integer id = user.getId();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertSame(user, reloadedUser);

		flushAndClearSession();

		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertNotSame(user, reloadedUser);
		assertEquals(user, reloadedUser);
		assertEquals(user.getPassword(), reloadedUser.getPassword());
		assertEquals(user.getUsername(), reloadedUser.getUsername());
		assertEquals(user.getFirstName(), reloadedUser.getFirstName());
		assertEquals(user.getLastName(), reloadedUser.getLastName());
		assertEquals(user.getEmail(), reloadedUser.getEmail());
		assertFalse(reloadedUser.isAccountNonExpired());
		assertFalse(reloadedUser.isAccountNonLocked());
		assertFalse(reloadedUser.isCredentialsNonExpired());
		assertFalse(reloadedUser.isEnabled());

	}

	@Test
	public void shouldSaveAndLoadUserWithProjectOriginallyAppliedTo() throws Exception {

		Program program = new ProgramBuilder().code("halloo").description("hallooo").title("halllooo").toProgram();
		Project project = new ProjectBuilder().program(program).code("halloo").description("hallo").title("hallo").toProject();
		save(program, project);

		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().projectOriginallyAppliedTo(project).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		sessionFactory.getCurrentSession().save(user);
		Integer id = user.getId();

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertEquals(project, reloadedUser.getProjectOriginallyAppliedTo());

	}

	@Test
	public void shouldSaveAndLoadUserWithRoles() throws Exception {

		// clear out whatever test data is in there -remember, it will all be
		// rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

		Role roleOne = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		Role roleTwo = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		save(roleOne, roleTwo);
		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleOne).role(roleTwo).toUser();

		sessionFactory.getCurrentSession().save(user);

		Integer id = user.getId();
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);

		assertEquals(2, reloadedUser.getRoles().size());

		assertTrue(reloadedUser.getRoles().containsAll(Arrays.asList(roleOne, roleTwo)));

	}

	@Test
	public void shouldDeleteRoleMappingWhenDeletingUser() throws Exception {
		// clear out whatever test data is in there -remember, it will all be
		// rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		save(role);
		Integer roleId = role.getId();
		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(role).toUser();

		sessionFactory.getCurrentSession().save(user);

		Integer id = user.getId();

		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertEquals(BigInteger.valueOf(1),
				sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " + roleId).uniqueResult());
		sessionFactory.getCurrentSession().delete(reloadedUser);
		flushAndClearSession();

		assertEquals(BigInteger.valueOf(0),
				sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " + roleId).uniqueResult());

	}

	@Test
	public void shouldLoadProgramsOfWhichAdministrator() throws Exception {
		RegisteredUser administratorOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username10")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username11")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(administratorOne, administratorTwo);

		flushAndClearSession();

		Program programOne = new ProgramBuilder().administrator(administratorOne).code("111111").description("hello").title("hello").toProgram();
		Program programTwo = new ProgramBuilder().administrator(administratorOne, administratorTwo).code("222222").description("hello").title("hello")
				.toProgram();

		save(programOne, programTwo);
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, administratorOne.getId());
		assertEquals(2, reloadedUser.getProgramsOfWhichAdministrator().size());
		assertTrue(reloadedUser.getProgramsOfWhichAdministrator().containsAll(Arrays.asList(programOne, programTwo)));

		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, administratorTwo.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichAdministrator().size());
		assertTrue(reloadedUser.getProgramsOfWhichAdministrator().containsAll(Arrays.asList(programTwo)));
	}
	
	@Test
	public void shouldLoadProgramsOfWhichApprover() throws Exception {
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username10")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username11")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(approverOne, approverTwo);

		flushAndClearSession();

		Program programOne = new ProgramBuilder().approver(approverOne).code("111111").description("hello").title("hello").toProgram();
		Program programTwo = new ProgramBuilder().approver(approverOne, approverTwo).code("222222").description("hello").title("hello")
				.toProgram();

		save(programOne, programTwo);
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, approverOne.getId());
		assertEquals(2, reloadedUser.getProgramsOfWhichApprover().size());
		assertTrue(reloadedUser.getProgramsOfWhichApprover().containsAll(Arrays.asList(programOne, programTwo)));

		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, approverTwo.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichApprover().size());
		assertTrue(reloadedUser.getProgramsOfWhichApprover().containsAll(Arrays.asList(programTwo)));
	}
	
	@Test
	public void shouldLoadProgramsOfWhichReviewer() throws Exception {
		RegisteredUser reviewerOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username10")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser reviewerTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username11")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(reviewerOne, reviewerTwo);

		flushAndClearSession();

		Program programOne = new ProgramBuilder().reviewers(reviewerOne).code("111111").description("hello").title("hello").toProgram();
		Program programTwo = new ProgramBuilder().reviewers(reviewerOne, reviewerTwo).code("222222").description("hello").title("hello")
				.toProgram();

		save(programOne, programTwo);
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, reviewerOne.getId());
		assertEquals(2, reloadedUser.getProgramsOfWhichReviewer().size());
		assertTrue(reloadedUser.getProgramsOfWhichReviewer().containsAll(Arrays.asList(programOne, programTwo)));

		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, reviewerTwo.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichReviewer().size());
		assertTrue(reloadedUser.getProgramsOfWhichReviewer().containsAll(Arrays.asList(programTwo)));
	}
}
