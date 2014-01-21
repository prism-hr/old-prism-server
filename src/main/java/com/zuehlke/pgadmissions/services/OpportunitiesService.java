package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
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
	private QualificationInstitutionService qualificationInstitutionService;

	@Autowired
	private ProgramsService programsService;

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

		Advert advert = new Advert();
		advert.setActive(true);
		advert.setDescription(opportunityRequest.getProgramDescription());
		advert.setStudyDuration(opportunityRequest.getStudyDuration());

		Program program = new Program();

		if ("OTHER".equals(opportunityRequest.getInstitutionCode())) {
			QualificationInstitution institution = new QualificationInstitution();
			institution.setDomicileCode(opportunityRequest.getInstitutionCountry().getCode());
			institution.setEnabled(true);
			institution.setName(opportunityRequest.getOtherInstitution());
			qualificationInstitutionService.createNewCustomInstitution(institution);

			program.setInstitution(institution);
		} else {
			QualificationInstitution institution = qualificationInstitutionService.getInstitutionByCode(opportunityRequest.getInstitutionCode());
			program.setInstitution(institution);
		}

		program.setEnabled(true);
		program.setTitle(opportunityRequest.getProgramTitle());

		ProgramInstance pi;
		// pi.se
		// program.se
	}

	public void rejectOpportunityRequest(Integer requestId) {
		OpportunityRequest opportunityRequest = getOpportunityRequest(requestId);
		opportunityRequest.setStatus(OpportunityRequestStatus.REJECTED);
	}

}