package com.zuehlke.pgadmissions.integration.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.InstitutionService;

@Service
public class InstitutionCreationHelper {

    @Autowired
    private InstitutionService institutionService;
    
    @Transactional
    public Institution verifyInstitutionCreation() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        InstitutionDTO institutionDTO = new InstitutionDTO();
        InstitutionAddressDTO institutionAddressDTO = new InstitutionAddressDTO();
        
        institutionAddressDTO.setAddressLine1("Address line 1");
        institutionAddressDTO.setAddressLine2("Address line 2");
        institutionAddressDTO.setAddressTown("Address town");
        institutionAddressDTO.setAddressDistrict("Address district");
        institutionAddressDTO.setAddressCode("Address code");
        
        return null;
    }
    
}
