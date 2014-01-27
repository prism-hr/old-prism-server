package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;

@Component
public class ApplicationFormCopyHelper {

    @Autowired
    private PersonalDetailsService personalDetailsService;

    public void copyApplicationFormData(ApplicationForm to, ApplicationForm from) {
        PersonalDetails personalDetails = new PersonalDetails();
        to.setPersonalDetails(personalDetails);
        personalDetails.setApplication(to);
        copyPersonalDetailsData(to.getPersonalDetails(), from.getPersonalDetails());
        personalDetailsService.save(personalDetails);

    }

    private void copyPersonalDetailsData(PersonalDetails to, PersonalDetails from) {
        to.setTitle(from.getTitle());
        to.setGender(from.getGender());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setCountry(from.getCountry());
        to.setFirstNationality(from.getFirstNationality());
        to.setSecondNationality(from.getSecondNationality());
        to.setEnglishFirstLanguage(from.getEnglishFirstLanguage());
        to.setLanguageQualificationAvailable(from.getLanguageQualificationAvailable());

        LanguageQualification previousLanguageQualification = Iterables.getFirst(from.getLanguageQualifications(), null);

        if (previousLanguageQualification != null) {
            LanguageQualification languageQualification = new LanguageQualification();
            copyLanguageQualification(languageQualification, previousLanguageQualification);
            to.getLanguageQualifications().add(languageQualification);
            languageQualification.setPersonalDetails(to);
        }
        
        to.setResidenceCountry(from.getResidenceCountry());
        to.setRequiresVisa(from.getRequiresVisa());
        to.setPassportAvailable(from.getPassportAvailable());
        
        PassportInformation previousPassportInformation = from.getPassportInformation();
        if(previousPassportInformation != null){
            PassportInformation passportInformation = new PassportInformation();
            copyPassportInformation(passportInformation, previousPassportInformation);
            to.setPassportInformation(passportInformation);
        }
        
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        to.setEthnicity(from.getEthnicity());
        to.setDisability(from.getDisability());
    }

    private void copyPassportInformation(PassportInformation to, PassportInformation from) {
        to.setPassportNumber(from.getPassportNumber());
        to.setNameOnPassport(from.getNameOnPassport());
        to.setPassportIssueDate(from.getPassportIssueDate());
        to.setPassportExpiryDate(from.getPassportExpiryDate());
    }

    private void copyLanguageQualification(LanguageQualification to, LanguageQualification from) {
        to.setQualificationType(from.getQualificationType());
        to.setOtherQualificationTypeName(from.getOtherQualificationTypeName());
        to.setDateOfExamination(from.getDateOfExamination());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setExamTakenOnline(from.getExamTakenOnline());

        Document previousDocument = from.getLanguageQualificationDocument();
        Document document = new Document();

        copyDocument(document, previousDocument);

        to.setLanguageQualificationDocument(document);
    }

    private void copyDocument(Document to, Document from) {
        to.setUploadedBy(from.getUploadedBy());
        to.setType(from.getType());
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setContent(from.getContent());
    }

}