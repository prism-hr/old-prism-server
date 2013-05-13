package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Component
public class UserFactory {

    private final RoleService roleService;
    private final EncryptionUtils encryptionUtils;

    public UserFactory() {
        this(null, null);
    }

    @Autowired
    public UserFactory(RoleService roleDAO, EncryptionUtils encryptionUtils) {
        this.roleService = roleDAO;
        this.encryptionUtils = encryptionUtils;
    }

    public RegisteredUser createNewUserInRoles(String firstname, String lastname, String email, List<Authority> authorities) {
        RegisteredUser user = buildRegisteredUser(firstname, lastname, email);
        for (Authority authority : authorities) {
            user.getRoles().add(roleService.getRoleByAuthority(authority));
        }
        return user;
    }
    
    public RegisteredUser createNewUserInRoles(String firstname, String lastname, String email, Authority... authorities) {
        return createNewUserInRoles(firstname, lastname, email, Arrays.asList(authorities));
    }
    
    private RegisteredUser buildRegisteredUser(final String firstname, final String lastname, final String email) {
        RegisteredUser user = new RegisteredUser();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(email);
        user.setEmail(email);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setCredentialsNonExpired(true);
        user.setActivationCode(encryptionUtils.generateUUID());
        return user;
    }
}
