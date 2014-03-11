package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Service
@Transactional
public class PersonalDetailsService {

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    public void save(ApplicationForm application, PersonalDetails newPersonalDetails) {

        PersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new PersonalDetails();
            application.setPersonalDetails(personalDetails);
        }

        personalDetails.setMessenger(newPersonalDetails.getMessenger());
        personalDetails.setPhoneNumber(newPersonalDetails.getPhoneNumber());
        personalDetails.setEnglishFirstLanguage(newPersonalDetails.getEnglishFirstLanguage());
        personalDetails.setLanguageQualificationAvailable(newPersonalDetails.getLanguageQualificationAvailable());
        
        if (BooleanUtils.isNotTrue(personalDetails.getLanguageQualificationAvailable())) {
            personalDetails.setLanguageQualification(null);
        } else {
            personalDetails.setLanguageQualification(newPersonalDetails.getLanguageQualification());
        }
        
        personalDetails.setRequiresVisa(newPersonalDetails.getRequiresVisa());
        personalDetails.setPassportAvailable(newPersonalDetails.getPassportAvailable());
        
        if (BooleanUtils.isNotTrue(personalDetails.getPassportAvailable()) || BooleanUtils.isNotTrue(personalDetails.getRequiresVisa())) {
            personalDetails.setPassportAvailable(false);
            personalDetails.setPassportInformation(null);
        } else {
            personalDetails.setPassportInformation(newPersonalDetails.getPassportInformation());
        }
        
        personalDetails.setFirstNationality(newPersonalDetails.getFirstNationality());
        personalDetails.setSecondNationality(newPersonalDetails.getSecondNationality());
        personalDetails.setTitle(newPersonalDetails.getTitle());
        personalDetails.setGender(newPersonalDetails.getGender());
        personalDetails.setDateOfBirth(newPersonalDetails.getDateOfBirth());
        personalDetails.setCountry(newPersonalDetails.getCountry());
        personalDetails.setEthnicity(newPersonalDetails.getEthnicity());
        personalDetails.setDisability(newPersonalDetails.getDisability());
        personalDetails.setResidenceCountry(newPersonalDetails.getResidenceCountry());
        
        applicationFormDAO.save(application);
    }

}
