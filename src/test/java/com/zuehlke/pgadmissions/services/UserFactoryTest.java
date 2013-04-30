package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class UserFactoryTest {

	private RoleService roleServiceMock;
	private UserFactory userFactory;
	private EncryptionUtils encryptionUtilsMock;
	
	@Test
	public void shouldCreateNewDisabledUserInRoles(){
		String firstname = "bob";
		String lastname = "smith";
		String email = "bob.smith@test.com";
		Authority[] authorities = new Authority[]{Authority.REFEREE, Authority.APPLICANT};
		
		Role refereeRole = new RoleBuilder().id(1).build();
		EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.REFEREE)).andReturn(refereeRole);
		Role applicantRole = new RoleBuilder().id(2).build();
		EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.APPLICANT)).andReturn(applicantRole);
		
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("activationCode");
		EasyMock.replay(roleServiceMock, encryptionUtilsMock);
		
		RegisteredUser newUser = userFactory.createNewUserInRoles(firstname,lastname, email, authorities);
		assertEquals("bob", newUser.getFirstName());
		assertEquals("smith", newUser.getLastName());
		assertEquals("bob.smith@test.com", newUser.getEmail());
		assertEquals("bob.smith@test.com", newUser.getUsername());
		assertTrue(newUser.isAccountNonExpired());
		assertTrue(newUser.isAccountNonLocked());
		assertTrue(newUser.isCredentialsNonExpired());
		assertFalse(newUser.isEnabled());
		assertEquals(2, newUser.getRoles().size());
		assertEquals("activationCode", newUser.getActivationCode());
		assertTrue(newUser.getRoles().containsAll(Arrays.asList(refereeRole, applicantRole)));
	}
	@Before
	public void setUp(){		
		roleServiceMock = EasyMock.createMock(RoleService.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		userFactory = new UserFactory(roleServiceMock, encryptionUtilsMock);
	}
}
