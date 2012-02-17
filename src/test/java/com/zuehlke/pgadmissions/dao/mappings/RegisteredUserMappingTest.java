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

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RegisteredUserMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadUserWithSimpleValues() throws Exception {

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

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
	public void shouldSaveAndLoadUserWithRoles() throws Exception {

		//clear out whatever test data is in there -remember, it will all be rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		
		Role roleOne = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		Role roleTwo = new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole();
		save(roleOne, roleTwo);
		flushAndClearSession();
		
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).role(roleOne).role(roleTwo).toUser();


		sessionFactory.getCurrentSession().save(user);
		
		Integer id = user.getId();
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);

		assertEquals(2, reloadedUser.getRoles().size());

		assertTrue(reloadedUser.getRoles().containsAll(Arrays.asList(roleOne, roleTwo)));

	}
	
	@Test
	public void shouldDeleteRoleMappingWhenDeletingUser() throws Exception {
		//clear out whatever test data is in there -remember, it will all be rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();		
		save(role);
		Integer roleId = role.getId();
		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).role(role).toUser();

		sessionFactory.getCurrentSession().save(user);
		
		Integer id = user.getId();
		
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertEquals(BigInteger.valueOf(1), sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " +  roleId).uniqueResult());
		sessionFactory.getCurrentSession().delete(reloadedUser);
		flushAndClearSession();

		assertEquals(BigInteger.valueOf(0), sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " +  roleId).uniqueResult());
		
	}
}
