package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Service
public class InstitutionCreationHelper {
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private SystemService systemService;
    
    @Transactional
    public Institution verifyInstitutionCreation() throws WorkflowEngineException {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        InstitutionDTO institutionDTO = new InstitutionDTO();
        InstitutionAddressDTO institutionAddressDTO = new InstitutionAddressDTO();
        
        institutionAddressDTO.setAddressLine1("Address line 1");
        institutionAddressDTO.setAddressLine2("Address line 2");
        institutionAddressDTO.setAddressTown("Address town");
        institutionAddressDTO.setAddressDistrict("Address district");
        institutionAddressDTO.setAddressCode("Address code");
        institutionAddressDTO.setCountry("GB");
        institutionAddressDTO.setRegion("GB-LND");
        
        institutionDTO.setDomicile("GB");
        institutionDTO.setName("University College London");
        institutionDTO.setHomepage("http://www.ucl.ac.uk/");
        institutionDTO.setAddress(institutionAddressDTO);
        institutionDTO.setUclInstitution(true);
        
        registrationDTO.setFirstName("Chris");
        registrationDTO.setLastName("Neil");
        registrationDTO.setEmail("c.neil@ucl.ac.uk");
        registrationDTO.setPassword("pgadmissions");
        registrationDTO.setResourceId(systemService.getSystem().getId());
        registrationDTO.setActionId(PrismAction.SYSTEM_CREATE_INSTITUTION);
        registrationDTO.setNewInstitution(institutionDTO);
        
        User user = registrationService.submitRegistration(registrationDTO);
        return null;
    }
    
}
