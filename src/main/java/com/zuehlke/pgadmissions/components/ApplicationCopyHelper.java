package com.zuehlke.pgadmissions.components;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ImportedEntityInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
@Component
public class ApplicationCopyHelper {

    @Transactional
    public void copyApplicationFormData(Application to, Application from) {
        if (from.getPersonalDetail() != null) {
            ApplicationPersonalDetail personalDetail = new ApplicationPersonalDetail();
            to.setPersonalDetail(personalDetail);
            personalDetail.setApplication(to);
            copyPersonalDetail(to.getPersonalDetail(), from.getPersonalDetail());
        }

        if (from.getAddress() != null) {
            ApplicationAddress applicationFormAddress = new ApplicationAddress();
            to.setAddress(applicationFormAddress);
            applicationFormAddress.setApplication(to);
            copyApplicationFormAddress(to.getAddress(), from.getAddress());
        }

        for (ApplicationQualification fromQualification : from.getQualifications()) {
            ApplicationQualification qualification = new ApplicationQualification();
            to.getQualifications().add(qualification);
            qualification.setApplication(to);
            copyQualification(qualification, fromQualification);
        }

        for (ApplicationEmploymentPosition fromEmployment : from.getEmploymentPositions()) {
            ApplicationEmploymentPosition employment = new ApplicationEmploymentPosition();
            to.getEmploymentPositions().add(employment);
            employment.setApplication(to);
            copyEmploymentPosition(employment, fromEmployment);
        }

        for (ApplicationFunding fromFunding : from.getFundings()) {
            ApplicationFunding funding = new ApplicationFunding();
            to.getFundings().add(funding);
            funding.setApplication(to);
            copyFunding(funding, fromFunding);
        }

        for (ApplicationReferee fromReferee : from.getReferees()) {
            ApplicationReferee referee = new ApplicationReferee();
            to.getReferees().add(referee);
            referee.setApplication(to);
            copyReferee(referee, fromReferee);
        }

        if (from.getDocument() != null) {
            ApplicationDocument applicationFormDocument = new ApplicationDocument();
            to.setDocument(applicationFormDocument);
            applicationFormDocument.setApplication(to);
            copyApplicationFormDocument(to.getDocument(), from.getDocument());
        }

        if (from.getAdditionalInformation() != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            copyAdditionalInformation(additionalInformation, from.getAdditionalInformation());
        }
    }

    public void copyAdditionalInformation(ApplicationAdditionalInformation to, ApplicationAdditionalInformation from) {
        to.setConvictionsText(from.getConvictionsText());
    }

    public void copyReferee(ApplicationReferee to, ApplicationReferee from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setUser(from.getUser());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setSkype(from.getSkype());
        to.setAddress(copyAddress(toInstitution, from.getAddress()));
    }

    public void copyFunding(ApplicationFunding to, ApplicationFunding from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setFundingSource(getEnabledImportedObject(toInstitution, from.getFundingSource()));
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        to.setDocument(copyDocument(from.getDocument()));
    }

    public void copyEmploymentPosition(ApplicationEmploymentPosition to, ApplicationEmploymentPosition from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setEmployerName(from.getEmployerName());
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.isCurrent());
        to.setEndDate(from.getEndDate());
        to.setEmployerAddress(copyAddress(toInstitution, from.getEmployerAddress()));
    }

    public void copyQualification(ApplicationQualification to, ApplicationQualification from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setInstitution(getEnabledImportedObject(toInstitution, from.getInstitution()));
        to.setType(getEnabledImportedObject(toInstitution, from.getType()));
        to.setTitle(from.getTitle());
        to.setSubject(from.getSubject());
        to.setLanguage(from.getLanguage());
        to.setStartDate(from.getStartDate());
        to.setCompleted(from.getCompleted());
        to.setGrade(from.getGrade());
        to.setAwardDate(from.getAwardDate());
        to.setDocument(copyDocument(from.getDocument()));
    }

    public void copyPersonalDetail(ApplicationPersonalDetail to, ApplicationPersonalDetail from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setTitle(getEnabledImportedObject(toInstitution, from.getTitle()));
        to.setGender(getEnabledImportedObject(toInstitution, from.getGender()));
        to.setDateOfBirth(from.getDateOfBirth());
        to.setCountry(getEnabledImportedObject(toInstitution, from.getCountry()));
        to.setFirstNationality(getEnabledImportedObject(toInstitution, from.getFirstNationality()));
        to.setSecondNationality(getEnabledImportedObject(toInstitution, from.getSecondNationality()));
        to.setFirstLanguageEnglish(from.getFirstLanguageEnglish());
        to.setLanguageQualificationAvailable(from.getLanguageQualificationAvailable());
        to.setResidenceCountry(getEnabledImportedObject(toInstitution, from.getResidenceCountry()));
        to.setVisaRequired(from.getVisaRequired());
        to.setPassportAvailable(from.getPassportAvailable());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        to.setEthnicity(getEnabledImportedObject(toInstitution, from.getEthnicity()));
        to.setDisability(getEnabledImportedObject(toInstitution, from.getDisability()));
        to.setLanguageQualification(copyLanguageQualification(toInstitution, from.getLanguageQualification()));
        to.setPassport(copyPassport(from.getPassport()));
    }

    public void copyApplicationFormAddress(ApplicationAddress to, ApplicationAddress from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setCurrentAddress(copyAddress(toInstitution, from.getCurrentAddress()));
        to.setContactAddress(copyAddress(toInstitution, from.getContactAddress()));
    }

    public void copyApplicationFormDocument(ApplicationDocument to, ApplicationDocument from) {
        to.setCv(copyDocument(from.getCv()));
        to.setPersonalStatement(copyDocument(from.getPersonalStatement()));
    }

    private Address copyAddress(Institution toInstitution, Address from) {
        if (from == null) {
            return null;
        }
        Address to = new Address();
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(from.getAddressLine2());
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(from.getAddressRegion());
        to.setAddressCode(from.getAddressCode());
        to.setDomicile(getEnabledImportedObject(toInstitution, from.getDomicile()));
        return to;
    }

    private Document copyDocument(Document from) {
        if (from == null) {
            return null;
        }
        Document to = new Document();
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setContent(from.getContent());
        to.setUser(from.getUser());
        return to;
    }

    private ApplicationLanguageQualification copyLanguageQualification(Institution toInstitution, ApplicationLanguageQualification from) {
        if (from == null) {
            return null;
        }
        ApplicationLanguageQualification to = new ApplicationLanguageQualification();
        to.setType(getEnabledImportedObject(toInstitution, from.getType()));
        to.setExamDate(from.getExamDate());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setDocument(copyDocument(from.getDocument()));
        return to;
    }

    private ApplicationPassport copyPassport(ApplicationPassport from) {
        if (from == null) {
            return null;
        }
        ApplicationPassport to = new ApplicationPassport();
        to.setNumber(from.getNumber());
        to.setName(from.getName());
        to.setIssueDate(from.getIssueDate());
        to.setExpiryDate(from.getExpiryDate());
        return to;
    }

    private <T extends ImportedEntityInstitution> T getEnabledImportedObject(Institution toInstitution, T fromEntity) {
        if (fromEntity == null || (fromEntity.isEnabled() && fromEntity.getInstitution() == toInstitution)) {
            return fromEntity;
        }
        return null;
    }

}
