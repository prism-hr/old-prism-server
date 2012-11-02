package com.zuehlke.pgadmissions.services.exporters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.RegistrypersonTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class SubmitAdmissionsApplicationRequestBuilder {

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private final static String SOURCE_IDENTIFIER = "PRISM";
    
    private final ObjectFactory xmlFactory;
    
    private final ProgramInstanceDAO programInstanceDAO;
    
    private final DatatypeFactory datatypeFactory;
    
    private ApplicationForm applicationForm;
    
    public SubmitAdmissionsApplicationRequestBuilder(ProgramInstanceDAO programInstanceDAO, ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        this.programInstanceDAO = programInstanceDAO;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }
    
    public SubmitAdmissionsApplicationRequestBuilder() {
        this(null, null);
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
        applicationTp.setSource(SOURCE_IDENTIFIER);
//      Schema is wrong (should not be an option)  
//      applicationTp.setExternalApplicationID(applicationForm.getApplicationNumber());
        applicationTp.setUclApplicationID(String.valueOf(applicationForm.getApplicant().getId()));
        applicationTp.setApplicant(buildApplicant());
        applicationTp.setCourseApplication(buildCourseApplication());
        applicationTp.getQualificationDetails().addAll(buildQualificationDetails());
        applicationTp.getEmployer().addAll(buildEmployer());
        applicationTp.getReferee().addAll(buildReferee());
        
//      TODO
//      applicationTp.getEnglishLanguageQualification().addAll(c);
        
        return applicationTp;
    }

    private RegistrypersonTp buildApplicant() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        RegistrypersonTp applicant = xmlFactory.createRegistrypersonTp();
        
        applicant.setFullName(buildFullName());
        applicant.setSex(buildSex());
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildNationality(0));
        applicant.setSecondaryNationality(buildNationality(1));
        applicant.setCountryOfBirth(buildCountry());
        applicant.setVisaRequired(personalDetails.getRequiresVisa());
        if (applicant.isVisaRequired()) {
            applicant.setPassport(buildPassport());
        }
        applicant.setDisability(buildDisability());
        applicant.setEthnicity(buildEthnicity());
        applicant.setHomeAddress(buildHomeAddress());
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress());
        applicant.setCriminalConvictionDetails(applicationForm.getAdditionalInformation().getConvictionsText());
        applicant.setCriminalConvictions(applicationForm.getAdditionalInformation().getConvictions());
        return applicant;
    }

    private NameTp buildFullName() {
        NameTp nameTp = xmlFactory.createNameTp();
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        nameTp.setTitle(personalDetails.getTitle().getDisplayValue().toUpperCase());
        nameTp.setSurname(personalDetails.getLastName());
        nameTp.setForename1(personalDetails.getFirstName());
        return nameTp;
    }
    
    private GenderTp buildSex() {
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
    
    private NationalityTp buildNationality(int idx) {
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
    
    private CountryTp buildCountry() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        CountryTp countryTp = xmlFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        return countryTp;
    }
    
    private PassportTp buildPassport() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        PassportTp passportTp = xmlFactory.createPassportTp();
        passportTp.setName(personalDetails.getNameOnPassport());
        passportTp.setNumber(personalDetails.getPassportNumber());
        passportTp.setExpiryDate(buildXmlDate(personalDetails.getPassportExpiryDate()));
        passportTp.setIssueDate(buildXmlDate(personalDetails.getPassportIssueDate()));
        return passportTp;
    }
    
    private DisabilityTp buildDisability() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        DisabilityTp disabilityTp = xmlFactory.createDisabilityTp();
        disabilityTp.setCode(Integer.toString(personalDetails.getDisability().getCode()));
        return disabilityTp;
    }
    
    private EthnicityTp buildEthnicity() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EthnicityTp ethnicityTp = xmlFactory.createEthnicityTp();
        ethnicityTp.setCode(Integer.toString(personalDetails.getEthnicity().getCode()));
        return ethnicityTp;
    }
    
    private ContactDtlsTp buildHomeAddress() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
        
        AddressTp addressTp = xmlFactory.createAddressTp();
//      TODO: We need to add these address lines to the database
//      addressTp.setAddressLine1(value)
//      addressTp.setAddressLine2(value)
//      addressTp.setAddressLine3(value)
//      addressTp.setAddressLine4(value)
//      addressTp.setAddressLine5(value)
//      addressTp.setCountry(value)
//      addressTp.setPostCode(value);
        
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(personalDetails.getEmail());
        contactDtlsTp.setLandline(personalDetails.getPhoneNumber());
        return contactDtlsTp;
    }
    
    private ContactDtlsTp buildCorrespondenceAddress() {
        Address contactAddress = applicationForm.getContactAddress();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
        AddressTp addressTp = xmlFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getLocation());
        addressTp.setCountry(contactAddress.getCountry().getCode());
//      TODO: We need to add these address lines to the database
//      addressTp.setAddressLine1(value)
//      addressTp.setAddressLine2(value)
//      addressTp.setAddressLine3(value)
//      addressTp.setAddressLine4(value)
//      addressTp.setAddressLine5(value)
//      addressTp.setCountry(value)
//      addressTp.setPostCode(value);
        contactDtlsTp.setAddressDtls(addressTp);
        return contactDtlsTp;
    }
    
    private CourseApplicationTp buildCourseApplication() {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        CourseApplicationTp applicationTp = xmlFactory.createCourseApplicationTp();
        applicationTp.setProgramme(buildProgrammeOccurence());
        applicationTp.setStartMonth(new DateTime(programmeDetails.getStartDate()));
        if (!programmeDetails.getSuggestedSupervisors().isEmpty()) {
            applicationTp.setSupervisorName(buildSupervisorName(0));
        }
        applicationTp.setPersonalStatement(REFER_TO_ATTACHED_DOCUMENT);
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationTp));
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setApplicationStatus(applicationForm.getStatus().displayValue());
        
//      TODO 
//      <v1_0:atasStatement>string</v1_0:atasStatement> // Project description
//      <v1_0:ipAddress>string</v1_0:ipAddress>
//      <v1_0:departmentalDecision>Accepted</v1_0:departmentalDecision>
//      <v1_0:departmentalOfferConditions>string</v1_0:departmentalOfferConditions>
        
        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence() {
        Program program = applicationForm.getProgram();
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        ProgrammeOccurrenceTp occurrenceTp = xmlFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance());
        
        ProgramInstance currentProgramInstanceForStudyOption = programInstanceDAO.getCurrentProgramInstanceForStudyOption(program, programmeDetails.getStudyOption());
        occurrenceTp.setAcademicYear(buildXmlDateYearOnly(currentProgramInstanceForStudyOption.getAcademic_year()));
        
//      TODO: ONLY FOR TEST
//      ============================================================================================================
        occurrenceTp.setAcademicYearOccurrence("F"); // TODO: ONLY FOR TEST
        occurrenceTp.setBlock("1"); // TODO: ONLY FOR TEST
//      ============================================================================================================
        
        // TODO: We do not have the following data values
        // <v1_0:academicYearOccurrence>F</v1_0:academicYearOccurrence>
        // <v1_0:block>1</v1_0:block>
        return occurrenceTp;
    }
    
    private ModeofattendanceTp buildModeofattendance() {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        ModeofattendanceTp modeofattendanceTp = xmlFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(String.valueOf(programmeDetails.getStudyOptionCode()));
        modeofattendanceTp.setName(programmeDetails.getStudyOption());
        return modeofattendanceTp;
    }
    
    private NameTp buildSupervisorName(int idx) {
        // TODO: This should be selectable in the future by an administrator
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        NameTp nameTp = xmlFactory.createNameTp();
        List<SuggestedSupervisor> suggestedSupervisors = programmeDetails.getSuggestedSupervisors();
        
        if (idx < suggestedSupervisors.size()) {
            SuggestedSupervisor suggestedSupervisor = suggestedSupervisors.get(idx);
            nameTp.setForename1(suggestedSupervisor.getFirstname());
            nameTp.setSurname(suggestedSupervisor.getLastname());
            return nameTp;
        } else {
            return null;
        }
    }
    
    private SourceOfInterestTp buildSourcesOfInterest(CourseApplicationTp applicationTp) {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        SourceOfInterestTp interestTp = xmlFactory.createSourceOfInterestTp();
        SourcesOfInterest sourcesOfInterest = programmeDetails.getSourcesOfInterest();
        interestTp.setCode(sourcesOfInterest.getCode());
        if (sourcesOfInterest.isFreeText()) {
            applicationTp.setOtherSourceofInterest(programmeDetails.getSourcesOfInterestText());
        }
        return interestTp;
    }

    private Collection<? extends QualificationsTp> buildQualificationDetails() {
        List<QualificationsTp> resultList = new ArrayList<QualificationsTp>();
        List<Qualification> qualifications = applicationForm.getQualifications();
        if (!qualifications.isEmpty()) {
            for (Qualification qualification : qualifications) {
                QualificationsTp qualificationsTp = xmlFactory.createQualificationsTp();
                
                qualificationsTp.setStartDate(buildXmlDate(qualification.getQualificationStartDate()));
                qualificationsTp.setEndDate(buildXmlDate(qualification.getQualificationAwardDate()));
                qualificationsTp.setGrade(qualification.getQualificationGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getQualificationLanguage());
                qualificationsTp.setMainSubject(qualification.getQualificationSubject());
                
                QualificationTp qualificationTp = xmlFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getQualificationType().getCode());
                qualificationsTp.setQualification(qualificationTp);
                
                InstitutionTp institutionTp = xmlFactory.createInstitutionTp();
                institutionTp.setCode(qualification.getInstitutionCountry().getCode());
                institutionTp.setName(qualification.getQualificationInstitution());
                qualificationsTp.setInstitution(institutionTp);
                
                resultList.add(qualificationsTp);
            }
        }
        return resultList;
    }
    
    private Collection<? extends AppointmentTp> buildEmployer() {
        List<AppointmentTp> resultList = new ArrayList<AppointmentTp>();
        List<EmploymentPosition> employmentPositions = applicationForm.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (EmploymentPosition employmentPosition : employmentPositions) {
                AppointmentTp appointmentTp = xmlFactory.createAppointmentTp();
                
                appointmentTp.setJobTitle(employmentPosition.getPosition());
                appointmentTp.setResponsibilities(employmentPosition.getRemit());
                appointmentTp.setStartDate(buildXmlDate(employmentPosition.getStartDate()));
                appointmentTp.setEndDate(buildXmlDate(employmentPosition.getEndDate()));
                
                EmployerTp employerTp = xmlFactory.createEmployerTp();
                employerTp.setName(employmentPosition.getEmployerName());
                appointmentTp.setEmployer(employerTp);
                
                resultList.add(appointmentTp);
            }
            
        }
        return resultList;
    }
    
    private Collection<? extends RefereeTp> buildReferee() {
        List<RefereeTp> resultList = new ArrayList<RefereeTp>();
        List<Referee> referees = applicationForm.getReferees();
        if (!referees.isEmpty()) {
            for (Referee referee : referees) {
                RefereeTp refereeTp = xmlFactory.createRefereeTp();
                
                refereeTp.setPosition(referee.getJobTitle());
                
                NameTp nameTp = xmlFactory.createNameTp();
                nameTp.setForename1(referee.getFirstname());
                nameTp.setSurname(referee.getLastname());
                refereeTp.setName(nameTp);
                
                ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
                contactDtlsTp.setEmail(referee.getEmail());
                contactDtlsTp.setLandline(referee.getPhoneNumber());
                
                AddressTp addressTp = xmlFactory.createAddressTp();
                addressTp.setAddressLine1(referee.getAddressLocation());
                addressTp.setCountry(referee.getAddressCountry().getCode());
                contactDtlsTp.setAddressDtls(addressTp);
                
                refereeTp.setContactDetails(contactDtlsTp);
                
                resultList.add(refereeTp);
            }
        }
        return resultList;
    }
    
    private XMLGregorianCalendar buildXmlDate(Date date) {  
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        return datatypeFactory.newXMLGregorianCalendar(gc);
    }
    
    private XMLGregorianCalendar buildXmlDateYearOnly(String date) {
        XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
        xmlCalendar.setYear(Integer.valueOf(date));
        return xmlCalendar;
    }
}
