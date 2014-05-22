package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ManageUsersService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailSendingService mailService;

    public User setUserRoles(String firstname, String lastname, String email, boolean replaceRoles, PrismResource scope,
            Authority... authorities) {
        User user = userService.getOrCreateUser(firstname, lastname, email);
        if (replaceRoles) {
            roleService.removeRoles(user, scope);
        }
        for (Authority authority : authorities) {
            roleService.getOrCreateUserRole(roleService.getPrismSystem(), user, authority);
        }
        return user;
    }
    
    public System getPrismSystem() {
        return roleService.getPrismSystem();
    }

}
