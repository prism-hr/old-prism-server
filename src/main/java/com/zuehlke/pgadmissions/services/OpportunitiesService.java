package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class OpportunitiesService {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

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

    public void createOpportunityRequest(OpportunityRequest opportunityRequest, boolean createAuthorUser) {
        Program program = opportunityRequest.getSourceProgram();
        RegisteredUser author = opportunityRequest.getAuthor();

        if (program != null && !opportunityRequestDAO.findByProgramAndStatus(program, OpportunityRequestStatus.NEW).isEmpty()) {
            throw new RuntimeException("Cannot create new opprotunity request for a program if there is already a new one. Program: " + program);
        }

        if (createAuthorUser) {
            registrationService.updateOrSaveUser(author, null);
        }

        opportunityRequest.setCreatedDate(new Date());
        opportunityRequest.setStatus(OpportunityRequestStatus.NEW);
        if (program != null) {
            // program exists
            opportunityRequest.setType(OpportunityRequestType.CHANGE);
            opportunityRequest.setProgramTitle(program.getTitle());
            opportunityRequest.setAtasRequired(program.getAtasRequired());

            // lock the program
            program.setLocked(true);
            programsService.merge(program);
        } else {
            // new program
            opportunityRequest.setType(OpportunityRequestType.CREATE);
            opportunityRequest.setProgramTitle(opportunityRequest.getProgramTitle());
            opportunityRequest.setAtasRequired(false);
        }

        opportunityRequestDAO.save(opportunityRequest);
    }

    public List<OpportunityRequest> getInitialOpportunityRequests() {
        return opportunityRequestDAO.getInitialOpportunityRequests();
    }

    public OpportunityRequest getOpportunityRequest(Integer requestId) {
        // TODO remove it
        OpportunityRequest request = opportunityRequestDAO.findById(requestId);
        request.setProgramType(new ProgramType(ProgramTypeId.VISITING_RESEARCH, 5));
        return request;
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
            program = programsService.merge(program);
        }

        // create comment
        comment.setAuthor(userService.getCurrentUser());
        opportunityRequest.getComments().add(comment);

        if (comment.getCommentType() == OpportunityRequestCommentType.APPROVE) {
            Program savedProgram = programsService.saveProgramOpportunity(opportunityRequest);
            opportunityRequest.setSourceProgram(savedProgram);
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
        return opportunityRequestDAO.findByStatus(OpportunityRequestStatus.NEW);
    }

}
