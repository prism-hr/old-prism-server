package com.zuehlke.pgadmissions.components;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;

@Component
public class ApplicationFormCopyHelper {

    public void copyApplicationFormData(ApplicationForm to, ApplicationForm from) {
        if (from.getPersonalDetails() != null) {
            PersonalDetails personalDetails = new PersonalDetails();
            to.setPersonalDetails(personalDetails);
            personalDetails.setApplication(to);
            copyPersonalDetailsData(to.getPersonalDetails(), from.getPersonalDetails());
        }

        if (from.getCurrentAddress() != null) {
            Address currentAddress = new Address();
            to.setCurrentAddress(currentAddress);
            copyAddress(to.getCurrentAddress(), from.getCurrentAddress());
        }

        if (from.getContactAddress() != null) {
            Address contactAddress = new Address();
            to.setContactAddress(contactAddress);
            copyAddress(to.getContactAddress(), from.getContactAddress());
        }

        for (Qualification fromQualification : from.getQualifications()) {
            Qualification qualification = new Qualification();
            to.getQualifications().add(qualification);
            qualification.setApplication(to);
            copyQualification(qualification, fromQualification);
        }
        
        for(EmploymentPosition fromEmployment : from.getEmploymentPositions()) {
            EmploymentPosition employment = new EmploymentPosition();
            to.getEmploymentPositions().add(employment);
            employment.setApplication(to);
            copyEmploymentPosition(employment, fromEmployment);
        }
        
        for(Funding fromFunding : from.getFundings()){
            Funding funding = new Funding();
            to.getFundings().add(funding);
            funding.setApplication(to);
            copyFunding(funding, fromFunding);
        }
        
        for(Referee fromReferee : from.getReferees()) {
            Referee referee = new Referee();
            to.getReferees().add(referee);
            referee.setApplication(to);
            copyReferee(referee, fromReferee);
        }
        
        Document previousPs = from.getPersonalStatement();
        Document ps = new Document();
        copyDocument(ps, previousPs);
        to.setPersonalStatement(ps);

        Document previousCv = from.getCv();
        Document cv = new Document();
        copyDocument(cv, previousCv);
        to.setCv(cv);
        
        if(from.getAdditionalInformation() != null){
            AdditionalInformation additionalInformation = new AdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            copyAdditionalInformation(additionalInformation, from.getAdditionalInformation());
        }
    }

    private void copyAdditionalInformation(AdditionalInformation to, AdditionalInformation from) {
        to.setConvictions(from.getConvictions());
        to.setConvictionsText(from.getConvictionsText());
    }

    private void copyReferee(Referee to, Referee from) {
        to.setFirstname(from.getFirstname());
        to.setLastname(from.getLastname());
        to.setEmail(from.getEmail());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        
        if(from.getAddressLocation() != null){
            Address address = new Address();
            to.setAddressLocation(address);
            copyAddress(address, from.getAddressLocation());
        }
        
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
    }

    private void copyFunding(Funding to, Funding from) {
        to.setType(from.getType());
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        
        Document previousDocument = from.getDocument();
        Document document = new Document();
        copyDocument(document, previousDocument);
        to.setDocument(document);
    }

    private void copyEmploymentPosition(EmploymentPosition to, EmploymentPosition from) {
        to.setEmployerName(from.getEmployerName());
        
        if(from.getEmployerAddress() != null){
            Address address = new Address();
            to.setEmployerAddress(address);
            copyAddress(address, from.getEmployerAddress());
        }
        
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.isCurrent());
        to.setEndDate(from.getEndDate());
        
    }

    private void copyQualification(Qualification to, Qualification from) {
        to.setInstitutionCountry(from.getInstitutionCountry());
        to.setQualificationInstitution(from.getQualificationInstitution());
        to.setQualificationInstitutionCode(from.getQualificationInstitutionCode());
        to.setOtherQualificationInstitution(from.getOtherQualificationInstitution());
        to.setQualificationType(from.getQualificationType());
        to.setQualificationTitle(from.getQualificationTitle());
        to.setQualificationSubject(from.getQualificationSubject());
        to.setQualificationLanguage(from.getQualificationLanguage());
        to.setQualificationStartDate(from.getQualificationStartDate());
        to.setCompleted(from.getCompleted());
        to.setQualificationGrade(from.getQualificationGrade());
        to.setQualificationAwardDate(from.getQualificationAwardDate());

        Document previousDocument = from.getProofOfAward();
        Document document = new Document();
        copyDocument(document, previousDocument);
        to.setProofOfAward(document);

    }

    private void copyAddress(Address to, Address from) {
        to.setAddress1(from.getAddress1());
        to.setAddress2(from.getAddress2());
        to.setAddress3(from.getAddress3());
        to.setAddress4(from.getAddress4());
        to.setAddress5(from.getAddress5());
        to.setDomicile(from.getDomicile());
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

        LanguageQualification previousLanguageQualification = from.getLanguageQualification();

        if (previousLanguageQualification != null) {
            LanguageQualification languageQualification = new LanguageQualification();
            copyLanguageQualification(languageQualification, previousLanguageQualification);
            to.setLanguageQualification(languageQualification);
            languageQualification.setPersonalDetails(to);
        }

        to.setResidenceCountry(from.getResidenceCountry());
        to.setRequiresVisa(from.getRequiresVisa());
        to.setPassportAvailable(from.getPassportAvailable());

        PassportInformation previousPassportInformation = from.getPassportInformation();
        if (previousPassportInformation != null) {
            PassportInformation passportInformation = new PassportInformation();
            copyPassportInformation(passportInformation, previousPassportInformation);
            to.setPassportInformation(passportInformation);
            passportInformation.setPersonalDetails(to);
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