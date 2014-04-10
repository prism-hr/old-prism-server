package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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

    public RegisteredUser setUserRoles(String firstname, String lastname, String email, boolean createIfNotExist, boolean replaceRoles, PrismScope scope,
            Authority... authorities) {
        RegisteredUser user = userService.getUser(firstname, lastname, email, createIfNotExist);
        if (replaceRoles) {
            roleService.removeRoles(user, scope);
        }
        roleService.createSystemUserRoles(user, authorities);
        return user;
    }

    public PrismSystem getPrismSystem() {
        return roleService.getPrismSystem();
    }

}
