package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class OpportunitiesService {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProgramsService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    public void createOpportunityRequestAndAuthor(OpportunityRequest opportunityRequest) {
        RegisteredUser author = opportunityRequest.getAuthor();
        registrationService.updateOrSaveUser(author, null);

        opportunityRequest.setCreatedDate(new Date());
        opportunityRequest.setStatus(OpportunityRequestStatus.NEW);
        opportunityRequest.setStudyDuration(opportunityRequest.getStudyDuration());

        opportunityRequestDAO.save(opportunityRequest);
    }

    public List<OpportunityRequest> getOpportunityRequests() {
        return opportunityRequestDAO.getOpportunityRequests();
    }

    public OpportunityRequest getOpportunityRequest(Integer requestId) {
        return opportunityRequestDAO.findById(requestId);
    }

    public void respondToOpportunityRequest(Integer requestId, OpportunityRequest newOpportunityRequest, OpportunityRequestCommentType action) {
        OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
        RegisteredUser author = opportunityRequest.getAuthor();

        // update opportunity request
        opportunityRequest.setStatus(action == OpportunityRequestCommentType.APPROVE ? OpportunityRequestStatus.APPROVED : OpportunityRequestStatus.REJECTED);
        opportunityRequest.setInstitutionCountry(newOpportunityRequest.getInstitutionCountry());
        opportunityRequest.setInstitutionCode(newOpportunityRequest.getInstitutionCode());
        opportunityRequest.setOtherInstitution(newOpportunityRequest.getOtherInstitution());
        opportunityRequest.setProgramTitle(newOpportunityRequest.getProgramTitle());
        opportunityRequest.setProgramDescription(newOpportunityRequest.getProgramDescription());
        opportunityRequest.setStudyDuration(newOpportunityRequest.getStudyDuration());
        opportunityRequest.setAtasRequired(newOpportunityRequest.getAtasRequired());
        opportunityRequest.setAdvertisingDeadlineYear(newOpportunityRequest.getAdvertisingDeadlineYear());
        opportunityRequest.setStudyOptions(newOpportunityRequest.getStudyOptions());

        // create comment
        OpportunityRequestComment comment = new OpportunityRequestComment();
        comment.setType(action);
        comment.setAuthor(userService.getCurrentUser());
        comment.setContent(newOpportunityRequest.getRespondComment());
        opportunityRequest.getComments().add(comment);

        if (action == OpportunityRequestCommentType.APPROVE) {
            // create program
            Program program = programsService.saveProgramOpportunity(opportunityRequest);

            // grant permissions to the author
            author.getInstitutions().add(program.getInstitution());
            author.getRoles().add(roleService.getRoleByAuthority(Authority.ADMINISTRATOR));
            author.getProgramsOfWhichAdministrator().add(program);
        }
    }

}
