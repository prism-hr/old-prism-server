package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class PermissionsService {

    @Autowired
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

    @Autowired
    private ProgramService programsService;

    public boolean canSeeOpportunityRequests() {
        RegisteredUser user = getCurrentUser();
        return user.isInRole(Authority.SUPERADMINISTRATOR) || !opportunityRequestDAO.getOpportunityRequestsForAuthor(user).isEmpty();
    }

    public boolean canManageProjects() {
        RegisteredUser user = getCurrentUser();
        return !programsService.getProgramsForWhichCanManageProjects(user).isEmpty();
    }

    public boolean canSeeOpportunityRequest(OpportunityRequest opportunityRequest) {
        RegisteredUser user = getCurrentUser();
        return user.isInRole(Authority.SUPERADMINISTRATOR) || HibernateUtils.sameEntities(user, opportunityRequest.getAuthor());
    }

    public boolean canPostOpportunityRequestComment(OpportunityRequest opportunityRequest, OpportunityRequestComment comment) {
        RegisteredUser user = getCurrentUser();

        if (opportunityRequest.getStatus() == OpportunityRequestStatus.APPROVED) {
            return false;
        }

        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            return true;
        }

        if (canSeeOpportunityRequest(opportunityRequest) && comment.getCommentType() == OpportunityRequestCommentType.REVISE) {
            return true;
        }

        return false;
    }

    private RegisteredUser getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    public boolean canAdministerApplication(ApplicationForm application, RegisteredUser user) {
        return !applicationFormUserRoleDAO.getByApplicationFormAndUserAndAuthoritiesWithActions(application, user,
                AuthorityGroup.ADMINISTRATOR_AUTHORITIES.getAuthorities()).isEmpty();
    }

    public boolean canApproveApplication(ApplicationForm application, RegisteredUser user) {
        return !applicationFormUserRoleDAO.getByApplicationFormAndUserAndAuthoritiesWithActions(application, user,
                AuthorityGroup.APPROVER_AUTHORITIES.getAuthorities()).isEmpty();
    }
}
