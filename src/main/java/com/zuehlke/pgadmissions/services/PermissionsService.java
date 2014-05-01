package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class PermissionsService {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

    @Autowired
    private ProgramService programsService;

    public boolean canSeeOpportunityRequests() {
        User user = userService.getCurrentUser();
        return roleService.hasRole(user, Authority.SYSTEM_ADMINISTRATOR) || !opportunityRequestDAO.getOpportunityRequestsForAuthor(user).isEmpty();
    }

    public boolean canManageProjects() {
        User user = userService.getCurrentUser();
        return !programsService.getProgramsForWhichCanManageProjects(user).isEmpty();
    }

    public boolean canSeeOpportunityRequest(OpportunityRequest opportunityRequest) {
        User user = userService.getCurrentUser();
        return roleService.hasRole(user, Authority.SYSTEM_ADMINISTRATOR) || HibernateUtils.sameEntities(user, opportunityRequest.getAuthor());
    }

    public boolean canPostOpportunityRequestComment(OpportunityRequest opportunityRequest, OpportunityRequestComment comment) {
        User user = userService.getCurrentUser();

        if (opportunityRequest.getStatus() == OpportunityRequestStatus.APPROVED) {
            return false;
        }

        if (roleService.hasRole(user, Authority.SYSTEM_ADMINISTRATOR)) {
            return true;
        }

        if (canSeeOpportunityRequest(opportunityRequest) && comment.getCommentType() == OpportunityRequestCommentType.REVISE) {
            return true;
        }

        return false;
    }
    
}
