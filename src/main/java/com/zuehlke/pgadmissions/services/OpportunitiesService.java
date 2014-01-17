package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
@Transactional
public class OpportunitiesService {

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private OpportunityRequestDAO opportunityRequestDAO;

    
    public void createOpportunityRequestAndAuthor(OpportunityRequest opportunityRequest){
        RegisteredUser author = opportunityRequest.getAuthor();
        registrationService.updateOrSaveUser(author, null);
        
        opportunityRequestDAO.save(opportunityRequest);
    }

}