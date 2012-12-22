package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Service
public class PersonalDetailsService {

    private final PersonalDetailDAO personalDetailDAO;

    PersonalDetailsService() {
        this(null);
    }

    @Autowired
    public PersonalDetailsService(PersonalDetailDAO personalDetailDAO) {
        this.personalDetailDAO = personalDetailDAO;

    }

    @Transactional
    public void save(PersonalDetails personalDetails) {
        if (BooleanUtils.isFalse(personalDetails.getPassportAvailable())) {
            personalDetails.setPassportInformation(null);
        }
        personalDetailDAO.save(personalDetails);
    }
}
