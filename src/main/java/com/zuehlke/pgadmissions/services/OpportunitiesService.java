package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;

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

        opportunityRequest.setStatus(OpportunityRequestStatus.APPROVED);
        opportunityRequest.setInstitutionCountry(newOpportunityRequest.getInstitutionCountry());
        opportunityRequest.setInstitutionCode(newOpportunityRequest.getInstitutionCode());
        opportunityRequest.setOtherInstitution(newOpportunityRequest.getOtherInstitution());
        opportunityRequest.setProgramTitle(newOpportunityRequest.getProgramTitle());
        opportunityRequest.setProgramDescription(newOpportunityRequest.getProgramDescription());
        opportunityRequest.setStudyDuration(newOpportunityRequest.getStudyDuration());
        opportunityRequest.setAtasRequired(newOpportunityRequest.getAtasRequired());
        opportunityRequest.setApplicationStartDate(newOpportunityRequest.getApplicationStartDate());
        opportunityRequest.setAdvertisingDuration(newOpportunityRequest.getAdvertisingDuration());
        opportunityRequest.setStudyOptions(newOpportunityRequest.getStudyOptions());

        Program program = programsService.createNewCustomProgram(opportunityRequest);

        List<ProgramInstance> programInstances = programInstanceService.createNewCustomProgramInstances(opportunityRequest, program);
        program.getInstances().addAll(programInstances);
    }

    public void rejectOpportunityRequest(Integer requestId) {
        OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
        opportunityRequest.setStatus(OpportunityRequestStatus.REJECTED);
    }

}