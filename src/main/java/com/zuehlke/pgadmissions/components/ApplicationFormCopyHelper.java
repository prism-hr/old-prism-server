package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class ApplicationFormCopyHelper {

    @Autowired
    DocumentService documentService;

    @Transactional
    public void copyApplicationFormData(ApplicationForm to, ApplicationForm from) {
        if (from.getPersonalDetails() != null) {
            PersonalDetails personalDetails = new PersonalDetails();
            to.setPersonalDetails(personalDetails);
            personalDetails.setApplication(to);
            copyPersonalDetails(to.getPersonalDetails(), from.getPersonalDetails(), true);
        }

        if (from.getApplicationAddress() != null) {
            ApplicationAddress applicationFormAddress = new ApplicationAddress();
            to.setApplicationAddress(applicationFormAddress);
            applicationFormAddress.setApplication(to);
            copyApplicationFormAddress(to.getApplicationAddress(), from.getApplicationAddress(), true);
        }

        for (Qualification fromQualification : from.getQualifications()) {
            Qualification qualification = new Qualification();
            to.getQualifications().add(qualification);
            qualification.setApplication(to);
            copyQualification(qualification, fromQualification, false);
        }

        for (EmploymentPosition fromEmployment : from.getEmploymentPositions()) {
            EmploymentPosition employment = new EmploymentPosition();
            to.getEmploymentPositions().add(employment);
            employment.setApplication(to);
            copyEmploymentPosition(employment, fromEmployment, true);
        }

        for (Funding fromFunding : from.getFundings()) {
            Funding funding = new Funding();
            to.getFundings().add(funding);
            funding.setApplication(to);
            copyFunding(funding, fromFunding, true);
        }

        for (Referee fromReferee : from.getReferees()) {
            Referee referee = new Referee();
            to.getReferees().add(referee);
            referee.setApplication(to);
            copyReferee(referee, fromReferee, true);
        }

        if (from.getApplicationDocument() != null) {
            ApplicationDocument applicationFormDocument = new ApplicationDocument();
            to.setApplicationDocument(applicationFormDocument);
            applicationFormDocument.setApplication(to);
            copyApplicationFormDocument(to.getApplicationDocument(), from.getApplicationDocument(), true);
        }

        if (from.getAdditionalInformation() != null) {
            AdditionalInformation additionalInformation = new AdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            copyAdditionalInformation(additionalInformation, from.getAdditionalInformation());
        }
    }

    public void copyProgramDetails(ProgramDetails to, ProgramDetails from) {
        to.setStudyOption(from.getStudyOption());
        to.setStartDate(from.getStartDate());
        to.setSourceOfInterest(from.getSourceOfInterest());
        to.setSourceOfInterestText(from.getSourceOfInterestText());
        to.getSuggestedSupervisors().addAll(from.getSuggestedSupervisors());
    }

    public void copyAdditionalInformation(AdditionalInformation to, AdditionalInformation from) {
        to.setConvictions(from.getConvictions());
        to.setConvictionsText(from.getConvictionsText());
    }

    public void copyReferee(Referee to, Referee from, boolean doPerformDeepCopy) {
        to.setUser(from.getUser());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        if (doPerformDeepCopy) {
            to.setAddress(copyAddress(from.getAddress()));
        } else {
            to.setAddress(from.getAddress());
        }
    }

    public void copyFunding(Funding to, Funding from, boolean doPerformDeepCopy) {
        to.setType(from.getType());
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        if (doPerformDeepCopy) {
            to.setDocument(copyDocument(from.getDocument()));
        } else {
            documentService.replaceDocument(from.getDocument(), to.getDocument());
            to.setDocument(from.getDocument());
        }
    }

    public void copyEmploymentPosition(EmploymentPosition to, EmploymentPosition from, boolean doPerformDeepCopy) {
        to.setEmployerName(from.getEmployerName());
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.isCurrent());
        to.setEndDate(from.getEndDate());
        if (doPerformDeepCopy) {
            to.setEmployerAddress(copyAddress(from.getEmployerAddress()));
        } else {
            to.setEmployerAddress(from.getEmployerAddress());
        }
    }

    public void copyQualification(Qualification to, Qualification from, boolean doPerformDeepCopy) {
        to.setInstitution(from.getInstitution());
        to.setType(getEnabledImportedObject(from.getType()));
        to.setTitle(from.getTitle());
        to.setSubject(from.getSubject());
        to.setLanguage(from.getLanguage());
        to.setStartDate(from.getStartDate());
        to.setCompleted(from.getCompleted());
        to.setGrade(from.getGrade());
        to.setAwardDate(from.getAwardDate());
        if (doPerformDeepCopy) {
            to.setDocument(copyDocument(from.getDocument()));
        } else {
            documentService.replaceDocument(from.getDocument(), to.getDocument());
            to.setDocument(from.getDocument());
        }
    }

    public void copyPersonalDetails(PersonalDetails to, PersonalDetails from, boolean doPerformDeepCopy) {
        to.setTitle(from.getTitle());
        to.setGender(from.getGender());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setCountry(getEnabledImportedObject(from.getCountry()));
        to.setFirstNationality(getEnabledImportedObject(from.getFirstNationality()));
        to.setSecondNationality(getEnabledImportedObject(from.getSecondNationality()));
        to.setEnglishFirstLanguage(from.getEnglishFirstLanguage());
        to.setLanguageQualificationAvailable(from.getLanguageQualificationAvailable());
        to.setResidenceCountry(getEnabledImportedObject(from.getResidenceCountry()));
        to.setRequiresVisa(from.getRequiresVisa());
        to.setPassportAvailable(from.getPassportAvailable());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        to.setEthnicity(getEnabledImportedObject(from.getEthnicity()));
        to.setDisability(getEnabledImportedObject(from.getDisability()));
        if (doPerformDeepCopy) {
            to.setLanguageQualification(copyLanguageQualification(from.getLanguageQualification()));
            to.setPassport(copyPassport(from.getPassport()));
        } else {
            LanguageQualification toQualification = to.getLanguageQualification();
            LanguageQualification fromQualification = from.getLanguageQualification();
            Document toQualificationDocument = null;
            if (toQualification != null) {
                toQualificationDocument = toQualification.getProofOfAward();
            }
            Document fromQualificationDocument = null;
            if (fromQualification != null) {
                fromQualificationDocument = fromQualification.getProofOfAward();
            }
            documentService.replaceDocument(fromQualificationDocument, toQualificationDocument);
            to.setLanguageQualification(from.getLanguageQualification());
            to.setPassport(from.getPassport());
        }
    }

    public void copyApplicationFormAddress(ApplicationAddress to, ApplicationAddress from, boolean doPerformDeepCopy) {
        if (doPerformDeepCopy) {
            to.setCurrentAddress(copyAddress(from.getCurrentAddress()));
            to.setContactAddress(copyAddress(from.getContactAddress()));
        } else {
            to.setCurrentAddress(from.getCurrentAddress());
            to.setContactAddress(from.getContactAddress());
        }
    }

    public void copyApplicationFormDocument(ApplicationDocument to, ApplicationDocument from, boolean doPerformDeepCopy) {
        if (doPerformDeepCopy) {
            to.setCv(copyDocument(from.getCv()));
            to.setPersonalStatement(copyDocument(from.getPersonalStatement()));
        } else {
            to.setCv(from.getCv());
            documentService.replaceDocument(from.getCv(), to.getCv());
            to.setPersonalStatement(from.getPersonalStatement());
            documentService.replaceDocument(from.getPersonalStatement(), to.getPersonalStatement());
        }
    }

    private Address copyAddress(Address from) {
        if (from == null) {
            return null;
        }
        Address to = new Address();
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(from.getAddressLine2());
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(from.getAddressRegion());
        to.setAddressCode(from.getAddressCode());
        to.setDomicile(getEnabledImportedObject(from.getDomicile()));
        return to;
    }

    private Document copyDocument(Document from) {
        if (from == null) {
            return null;
        }
        Document to = new Document();
        to.setType(from.getType());
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setContent(from.getContent());
        to.setIsReferenced(true);
        return to;
    }

    private LanguageQualification copyLanguageQualification(LanguageQualification from) {
        if (from == null) {
            return null;
        }
        LanguageQualification to = new LanguageQualification();
        to.setQualificationType(from.getQualificationType());
        to.setQualificationTypeOther(from.getQualificationTypeOther());
        to.setExamDate(from.getExamDate());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setExamOnline(from.getExamOnline());
        to.setProofOfAward(copyDocument(from.getProofOfAward()));
        return to;
    }

    private Passport copyPassport(Passport from) {
        if (from == null) {
            return null;
        }
        Passport to = new Passport();
        to.setNumber(from.getNumber());
        to.setName(from.getName());
        to.setIssueDate(from.getIssueDate());
        to.setExpiryDate(from.getExpiryDate());
        return to;
    }

    private <T extends ImportedEntity> T getEnabledImportedObject(T object) {
        if (object == null || object.isEnabled()) {
            return object;
        }
        return null;
    }

}
