package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
    private ProgramsService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private MailSendingService mailSendingService;

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

    public void approveOpportunityRequest(Integer requestId, OpportunityRequest newOpportunityRequest) {
        OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
        RegisteredUser author = opportunityRequest.getAuthor();

        // update opportunity request
        opportunityRequest.setStatus(OpportunityRequestStatus.APPROVED);
        opportunityRequest.setInstitutionCountry(newOpportunityRequest.getInstitutionCountry());
        opportunityRequest.setInstitutionCode(newOpportunityRequest.getInstitutionCode());
        opportunityRequest.setOtherInstitution(newOpportunityRequest.getOtherInstitution());
        opportunityRequest.setProgramTitle(newOpportunityRequest.getProgramTitle());
        opportunityRequest.setProgramDescription(newOpportunityRequest.getProgramDescription());
        opportunityRequest.setStudyDuration(newOpportunityRequest.getStudyDuration());
        opportunityRequest.setAtasRequired(newOpportunityRequest.getAtasRequired());
        opportunityRequest.setAdvertisingDeadlineYear(newOpportunityRequest.getAdvertisingDeadlineYear());
        opportunityRequest.setStudyOptions(newOpportunityRequest.getStudyOptions());

        // create program
        Program program = programsService.createNewCustomProgram(opportunityRequest);
        
        // create program instances
        List<String> studyOptions = Arrays.asList(opportunityRequest.getStudyOptions().split(","));
        Integer advertisingDeadlineYear = opportunityRequest.getAdvertisingDeadlineYear();

        List<ProgramInstance> programInstances = programInstanceService.createRemoveProgramInstances(program, studyOptions, advertisingDeadlineYear);
        program.getInstances().addAll(programInstances);
        
        // grant permissions to the author 
        author.getInstitutions().add(program.getInstitution());
        author.getProgramsOfWhichAdministrator().add(program);
    }

    public void rejectOpportunityRequest(Integer requestId, String rejectionReason) {
        OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
        opportunityRequest.setRejectionReason(rejectionReason);
        opportunityRequest.setStatus(OpportunityRequestStatus.REJECTED);
        
        mailSendingService.sendOpportunityRequestRejectionConfirmation(opportunityRequest);
    }

}
