package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
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
	public void shouldLoadRegisteredUserWithReferee() throws ParseException {
		
		Referee referee = new RefereeBuilder().relationship("tutor").id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		

		RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").toUser();
		
		sessionFactory.getCurrentSession().save(admin1);
		flushAndClearSession();
		referee.setUser(admin1);

		save(referee);

		RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin1.getId()));
		
		assertEquals(referee, reloadedUser.getReferee());
		assertNotNull(referee.getUser());

	}

	@Test
	public void shouldSaveAndLoadRegisteredUserWithReferee() {


		RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").toUser();

		flushAndClearSession();
		assertNull(admin1.getId());

		sessionFactory.getCurrentSession().save(admin1);

		Referee referee = new RefereeBuilder().relationship("tutor").firstname("ref").lastname("erre").email("ref@test.com").user(admin1).toReferee();
		save(referee);
		assertNotNull(admin1.getId());
		Integer id = admin1.getId();
		RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));
		
		assertSame(admin1, reloadedUser);

		flushAndClearSession();

		
		reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));
		
		assertNotSame(admin1, reloadedUser);
		assertEquals(admin1, reloadedUser);

		assertEquals("email", reloadedUser.getUsername());
		Assert.assertNotNull(reloadedUser.getReferee());
		Assert.assertTrue(reloadedUser.getReferee().equals(referee));
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
	public void shouldSaveAndLoadProgramsOfWhichAdministrator() throws Exception {

		Program program = new ProgramBuilder().code("111111").description("hello").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser admin = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(admin);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichAdministrator().size());
		assertTrue(reloadedUser.getProgramsOfWhichAdministrator().containsAll(Arrays.asList(program)));

	}

	@Test
	public void shouldLoadProgramsOfWhichApprover() throws Exception {

		Program program = new ProgramBuilder().code("111111").description("hello").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser approver = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(approver);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, approver.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichApprover().size());
		assertTrue(reloadedUser.getProgramsOfWhichApprover().containsAll(Arrays.asList(program)));

	}

	@Test
	public void shouldSaveAndLoadProgramsOfWhichReviewer() throws Exception {

		Program program = new ProgramBuilder().code("111111").description("hello").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(reviewer);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, reviewer.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichReviewer().size());
		assertTrue(reloadedUser.getProgramsOfWhichReviewer().containsAll(Arrays.asList(program)));

	}

}
