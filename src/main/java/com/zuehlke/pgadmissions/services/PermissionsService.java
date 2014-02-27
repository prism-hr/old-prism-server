package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
@Transactional
public class PermissionsService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

    @Autowired
    private ProgramsService programsService;

    public boolean canSeeOpportunityRequests() {
        RegisteredUser user = authenticationService.getCurrentUser();
        return user.isInRole(Authority.SUPERADMINISTRATOR) || !opportunityRequestDAO.getOpportunityRequestsForAuthor(user).isEmpty();
    }

    public boolean canManageProjects() {
        RegisteredUser user = authenticationService.getCurrentUser();
        return !programsService.getProgramsForWhichCanManageProjects(user).isEmpty();
    }

}
