package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

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

    public void createNewOpportunityRequestAndAuthor(OpportunityRequest opportunityRequest) {
        RegisteredUser author = opportunityRequest.getAuthor();
        registrationService.updateOrSaveUser(author, null);

        opportunityRequest.setCreatedDate(new Date());
        opportunityRequest.setType(OpportunityRequestType.CREATE);
        opportunityRequest.setStatus(OpportunityRequestStatus.NEW);
        opportunityRequest.setStudyDuration(opportunityRequest.getStudyDuration());

        opportunityRequestDAO.save(opportunityRequest);
    }

    public void createOpportunityChangeRequest(OpportunityRequest opportunityRequest) {
        Program program = opportunityRequest.getSourceProgram();
        Preconditions.checkNotNull(program);

        RegisteredUser author = opportunityRequest.getAuthor();
        if (programsService.canChangeInstitution(author, opportunityRequest)) {
            throw new RuntimeException("No change request needed, user " + author.getEmail() + " has permissions to " + opportunityRequest.getInstitutionCode());
        }

        OpportunityRequest example = new OpportunityRequest();
        example.setSourceProgram(program);
        example.setStatus(OpportunityRequestStatus.NEW);
        if (!opportunityRequestDAO.findByExample(example).isEmpty()) {
            throw new RuntimeException("Cannot create new opprotunity request if there is already a new one. Program: " + program);
        }

        opportunityRequest.setCreatedDate(new Date());
        opportunityRequest.setType(OpportunityRequestType.CHANGE);
        opportunityRequest.setStudyDuration(opportunityRequest.getStudyDuration());
        opportunityRequest.setProgramTitle(program.getTitle());
        opportunityRequest.setAtasRequired(program.getAtasRequired());

        // lock the program
        program.setLocked(true);
        programsService.merge(program);

        opportunityRequestDAO.save(opportunityRequest);
    }

    public List<OpportunityRequest> getInitialOpportunityRequests() {
        return opportunityRequestDAO.getInitialOpportunityRequests();
    }

    public OpportunityRequest getOpportunityRequest(Integer requestId) {
        return opportunityRequestDAO.findById(requestId);
    }

    public void respondToOpportunityRequest(Integer requestId, OpportunityRequest newOpportunityRequest, OpportunityRequestComment comment) {
        OpportunitiesService thisBean = applicationContext.getBean(OpportunitiesService.class);
        OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
        OpportunityRequest lastRequest = thisBean.getAllRelatedOpportunityRequests(opportunityRequest).get(0);
        if (lastRequest.getId().intValue() != opportunityRequest.getId().intValue()) {
            throw new RuntimeException("Trying to respond to deprecated opportunity request: " + requestId + ", last ID: " + lastRequest.getId());
        }

        if (opportunityRequest.getStatus() == OpportunityRequestStatus.APPROVED) {
            throw new RuntimeException("Already approved");
        }

        // update opportunity request
        opportunityRequest.setStatus(comment.getCommentType() == OpportunityRequestCommentType.APPROVE ? OpportunityRequestStatus.APPROVED
                : OpportunityRequestStatus.REJECTED);
        opportunityRequest.setInstitutionCountry(newOpportunityRequest.getInstitutionCountry());
        opportunityRequest.setInstitutionCode(newOpportunityRequest.getInstitutionCode());
        opportunityRequest.setOtherInstitution(newOpportunityRequest.getOtherInstitution());
        opportunityRequest.setProgramTitle(newOpportunityRequest.getProgramTitle());
        opportunityRequest.setProgramDescription(newOpportunityRequest.getProgramDescription());
        opportunityRequest.setStudyDuration(newOpportunityRequest.getStudyDuration());
        opportunityRequest.setAtasRequired(newOpportunityRequest.getAtasRequired());
        opportunityRequest.setAdvertisingDeadlineYear(newOpportunityRequest.getAdvertisingDeadlineYear());
        opportunityRequest.setStudyOptions(newOpportunityRequest.getStudyOptions());

        // unlock program if already exists
        Program program = opportunityRequest.getSourceProgram();
        if (program != null) {
            program.setLocked(false);
            programsService.merge(program);
        }

        // create comment
        comment.setAuthor(userService.getCurrentUser());
        opportunityRequest.getComments().add(comment);

        if (comment.getCommentType() == OpportunityRequestCommentType.APPROVE) {
            thisBean.approveApportunityRequest(opportunityRequest);
        }
    }

    protected void approveApportunityRequest(OpportunityRequest opportunityRequest) {
        RegisteredUser author = opportunityRequest.getAuthor();

        // create program
        Program program = programsService.saveProgramOpportunity(opportunityRequest);
        opportunityRequest.setSourceProgram(program);

        // grant permissions to the author
        if (!HibernateUtils.containsEntity(author.getInstitutions(), program.getInstitution())) {
            author.getInstitutions().add(program.getInstitution());
        }
        Role adminRole = roleService.getRoleByAuthority(Authority.ADMINISTRATOR);
        if (!HibernateUtils.containsEntity(author.getRoles(), adminRole)) {
            author.getRoles().add(adminRole);
        }
        if (!HibernateUtils.containsEntity(author.getProgramsOfWhichAdministrator(), program)) {
            author.getProgramsOfWhichAdministrator().add(program);
        }
    }

    public List<OpportunityRequest> getAllRelatedOpportunityRequests(OpportunityRequest opportunityRequest) {
        if (opportunityRequest.getSourceProgram() == null) {
            // program not created yet
            return Lists.newArrayList(opportunityRequest);
        }

        return getOpportunityRequests(opportunityRequest.getSourceProgram());
    }

    private List<OpportunityRequest> getOpportunityRequests(Program program) {
        return opportunityRequestDAO.getOpportunityRequests(program);
    }

    public List<OpportunityRequest> getNewOpportunityRequests() {
        OpportunityRequest example = new OpportunityRequest();
        example.setStatus(OpportunityRequestStatus.NEW);
        return opportunityRequestDAO.findByExample(example);
    }

}
