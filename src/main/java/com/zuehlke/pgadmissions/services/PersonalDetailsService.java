package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
@Transactional
public class PersonalDetailsService {

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public PersonalDetails getOrCreate(ApplicationForm application) {
        PersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new PersonalDetails();
        }
        return additionalInformation;
    }

    public void save(int applicationId, PersonalDetails newPersonalDetails, RegisteredUser newApplicant) {
        PersonalDetailsService thisBean = applicationContext.getBean(PersonalDetailsService.class);
        
        ApplicationForm application = applicationFormDAO.get(applicationId);
        PersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new PersonalDetails();
            application.setPersonalDetails(personalDetails);
        }

        Document oldQualificationDocument = personalDetails.getLanguageQualification() == null ? null : personalDetails.getLanguageQualification()
                .getLanguageQualificationDocument();
        Document newQualificationDocument = newPersonalDetails.getLanguageQualification() == null ? null : newPersonalDetails.getLanguageQualification()
                .getLanguageQualificationDocument();
        documentService.replaceDocument(oldQualificationDocument, newQualificationDocument);
        
        thisBean.copyPersonalDetails(personalDetails, newPersonalDetails);
        thisBean.copyApplicantData(application.getApplicant(), newApplicant);

        applicationFormDAO.save(application);
    }

    protected void copyApplicantData(RegisteredUser applicant, RegisteredUser newApplicant) {
        applicant.setFirstName(newApplicant.getFirstName());
        applicant.setFirstName2(newApplicant.getFirstName2());
        applicant.setFirstName3(newApplicant.getFirstName3());
        applicant.setLastName(newApplicant.getLastName());
    }

    protected void copyPersonalDetails(PersonalDetails personalDetails, PersonalDetails newPersonalDetails) {
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
    }

}
