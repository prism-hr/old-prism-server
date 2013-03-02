package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Service
@Transactional
public class PersonalDetailsService {

    private final PersonalDetailDAO personalDetailDAO;

    public PersonalDetailsService() {
        this(null);
    }

    @Autowired
    public PersonalDetailsService(PersonalDetailDAO personalDetailDAO) {
        this.personalDetailDAO = personalDetailDAO;
    }

    public void save(PersonalDetails personalDetails) {
        if (BooleanUtils.isNotTrue(personalDetails.getPassportAvailable()) || BooleanUtils.isNotTrue(personalDetails.getRequiresVisa())) {
            personalDetails.setPassportAvailable(false);
            personalDetails.setPassportInformation(null);
        }
        
        if (BooleanUtils.isTrue(personalDetails.getEnglishFirstLanguage()) || BooleanUtils.isNotTrue(personalDetails.getLanguageQualificationAvailable())) {
            personalDetails.getLanguageQualifications().clear();
        }
        personalDetailDAO.save(personalDetails);
    }
}
