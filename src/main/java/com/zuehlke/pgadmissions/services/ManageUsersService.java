package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.mail.MailService;

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

    public User setUserRoles(String firstname, String lastname, String email, boolean overwriteRoles, Resource resource,
            PrismRole... authorities) {
        User user = userService.getOrCreateUser(firstname, lastname, email);
        if (overwriteRoles) {
            roleService.removeUserRoles(resource, user);
        }
        for (PrismRole authority : authorities) {
            roleService.getOrCreateUserRole(systemService.getSystem(), user, authority);
        }
        return user;
    }

}
