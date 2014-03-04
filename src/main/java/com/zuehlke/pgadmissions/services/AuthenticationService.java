package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service("authenticationService")
@Transactional
public class AuthenticationService {

    @Autowired
    private UserDAO userDAO;

    public RegisteredUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        RegisteredUser currentUser = (RegisteredUser) authentication.getDetails();
        RegisteredUser user = userDAO.get(currentUser.getId());
        return user;
    }

}