package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.NotificationService;

@Service
@Transactional
public class ManageUsersService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotificationService mailService;
    
    @Autowired
    private SystemService systemService;

    public User setUserRoles(String firstname, String lastname, String email, boolean overwriteRoles, PrismResource resource,
            Authority... authorities) {
        User user = userService.getOrCreateUser(firstname, lastname, email);
        if (overwriteRoles) {
            roleService.removeUserRoles(user, resource);
        }
        for (Authority authority : authorities) {
            roleService.getOrCreateUserRole(systemService.getSystem(), user, roleService.getById(authority));
        }
        return user;
    }

}
