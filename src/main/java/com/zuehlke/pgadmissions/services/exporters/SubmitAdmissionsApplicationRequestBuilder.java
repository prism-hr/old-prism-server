package com.zuehlke.pgadmissions.services.exporters;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.RegistrypersonTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class SubmitAdmissionsApplicationRequestBuilder {

    private final static String APPLICATION_ID = "PRISM";
    
    private final ObjectFactory xmlFactory;
    
    private ApplicationForm applicationForm;
    
    public SubmitAdmissionsApplicationRequestBuilder() {
        xmlFactory = new ObjectFactory();
    }
    
    public SubmitAdmissionsApplicationRequestBuilder applicationForm(final ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }
    
    public SubmitAdmissionsApplicationRequest toSubmitAdmissionsApplicationRequest() {
        SubmitAdmissionsApplicationRequest request = xmlFactory.createSubmitAdmissionsApplicationRequest();
        request.setApplication(buildApplication());
        return request;
    }
    
    private ApplicationTp buildApplication() {
        ApplicationTp applicationTp = xmlFactory.createApplicationTp();
        
        applicationTp.setSource(APPLICATION_ID);
        applicationTp.setApplicant(buildRegistrypersonTp());
        
//        applicationTp.setCourseApplication(value)
//        applicationTp.setExternalApplicationID(value)
//        applicationTp.setUclApplicationID(value)
//        
        return applicationTp;
    }
    
    private RegistrypersonTp buildRegistrypersonTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        RegistrypersonTp applicant = xmlFactory.createRegistrypersonTp();
        
        applicant.setFullName(buildNameTp());
        applicant.setSex(buildGenderTp());
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildNationalityTp(0));
        applicant.setSecondaryNationality(buildNationalityTp(1));
        applicant.setCountryOfBirth(buildCountryTp());
        applicant.setPassport(buildPassportTp());
        applicant.setVisaRequired(personalDetails.getRequiresVisa());
        applicant.setDisability(buildDisabilityTp());
        applicant.setEthnicity(buildEthnicityTp());
        applicant.setHomeAddress(buildContactDtlsTp());
        
        return applicant;
    }
    
    private NameTp buildNameTp() {
        NameTp nameTp = xmlFactory.createNameTp();
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        nameTp.setTitle(personalDetails.getTitle().getDisplayValue().toUpperCase());
        nameTp.setSurname(personalDetails.getLastName());
        nameTp.setForename1(personalDetails.getFirstName());
        return nameTp;
    }
    
    private GenderTp buildGenderTp() {
        Gender gender = applicationForm.getPersonalDetails().getGender();
        if (gender == Gender.MALE) {
            return GenderTp.M;
        } else if (gender == Gender.FEMALE) {
            return GenderTp.F;
        } else if (gender == Gender.INDETERMINATE_GENDER) {
            return GenderTp.N;
        } else {
            throw new IllegalArgumentException(String.format("Gender type [%s] could not be converted", gender));
        }
    }
    
    private XMLGregorianCalendar buildDateOfBirth() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        return buildXmlDate(personalDetails.getDateOfBirth());
    }
    
    private NationalityTp buildNationalityTp(int idx) {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        List<Language> candidateNationalities = personalDetails.getCandidateNationalities();
        
        NationalityTp nationalityTp = xmlFactory.createNationalityTp();
        if (candidateNationalities.isEmpty()) {
            throw new IllegalArgumentException("Nationality is empty");
        }
        
        if (idx < candidateNationalities.size()) {
            nationalityTp.setCode(candidateNationalities.get(idx).getCode());            
            return nationalityTp;
        } else {
            return null;
        }
    }
    
    private CountryTp buildCountryTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        CountryTp countryTp = xmlFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        return countryTp;
    }
    
    private PassportTp buildPassportTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        PassportTp passportTp = xmlFactory.createPassportTp();
        passportTp.setName(personalDetails.getNameOnPassport());
        passportTp.setNumber(personalDetails.getPassportNumber());
        passportTp.setExpiryDate(buildXmlDate(personalDetails.getPassportExpiryDate()));
        passportTp.setIssueDate(buildXmlDate(personalDetails.getPassportIssueDate()));
        return passportTp;
    }
    
    private DisabilityTp buildDisabilityTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        DisabilityTp disabilityTp = xmlFactory.createDisabilityTp();
        disabilityTp.setCode(Integer.toString(personalDetails.getDisability().getCode()));
        return disabilityTp;
    }
    
    private EthnicityTp buildEthnicityTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EthnicityTp ethnicityTp = xmlFactory.createEthnicityTp();
        ethnicityTp.setCode(Integer.toString(personalDetails.getEthnicity().getCode()));
        return ethnicityTp;
    }
    
    private ContactDtlsTp buildContactDtlsTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
        contactDtlsTp.setAddressDtls(buildAddressTp());
        contactDtlsTp.setEmail(personalDetails.getEmail());
        contactDtlsTp.setLandline(personalDetails.getPhoneNumber());
        return contactDtlsTp;
    }
    
    private AddressTp buildAddressTp() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        AddressTp addressTp = xmlFactory.createAddressTp();
//        TODO: We need to add these address lines to the database
//        addressTp.setAddressLine1(value)
//        addressTp.setAddressLine2(value)
//        addressTp.setAddressLine3(value)
//        addressTp.setAddressLine4(value)
//        addressTp.setAddressLine5(value)
//        addressTp.setCountry(value)
//        addressTp.setPostCode(value);
        return addressTp;
    }
    
    
    
    
    
    
    private XMLGregorianCalendar buildXmlDate(Date date) {
        DatatypeFactory df = null;
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }  
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        return df.newXMLGregorianCalendar(gc);
    }
}
