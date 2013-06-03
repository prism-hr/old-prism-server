package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class RoleService {
 
    private final RoleDAO roleDAO;
    
    public RoleService() {
        this(null);
    }
    
    @Autowired
    public RoleService(RoleDAO durationDAO) {
        this.roleDAO = durationDAO;
    }

    @Transactional
    public Role getRoleByAuthority(Authority authority) {
        return roleDAO.getRoleByAuthority(authority);
    }
}
