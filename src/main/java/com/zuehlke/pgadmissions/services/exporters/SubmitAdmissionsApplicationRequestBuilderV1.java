package com.zuehlke.pgadmissions.services.exporters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.LanguageBandScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.QualificationsinEnglishTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.RegistrypersonTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class SubmitAdmissionsApplicationRequestBuilderV1 {

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private final static String SOURCE_IDENTIFIER = "PRISM";
    
    private final ObjectFactory xmlFactory;
    
    private final ProgramInstanceDAO programInstanceDAO;
    
    private final DatatypeFactory datatypeFactory;
    
    private ApplicationForm applicationForm;
    
    public SubmitAdmissionsApplicationRequestBuilderV1(ProgramInstanceDAO programInstanceDAO, ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        this.programInstanceDAO = programInstanceDAO;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }
    
    public SubmitAdmissionsApplicationRequestBuilderV1() {
        this(null, null);
    }
    
    public SubmitAdmissionsApplicationRequestBuilderV1 applicationForm(final ApplicationForm applicationForm) {
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
        if (applicationForm.getPersonalDetails().getLanguageQualificationAvailable()) {
            applicationTp.getEnglishLanguageQualification().addAll(buildEnglishLanguageQualification());
        }
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
        if (personalDetails.getRequiresVisa()) {
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
        PassportInformation passportInformation = personalDetails.getPassportInformation();
        PassportTp passportTp = xmlFactory.createPassportTp();
        passportTp.setName(passportInformation.getNameOnPassport());
        passportTp.setNumber(passportInformation.getPassportNumber());
        passportTp.setExpiryDate(buildXmlDate(passportInformation.getPassportExpiryDate()));
        passportTp.setIssueDate(buildXmlDate(passportInformation.getPassportIssueDate()));
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
        Address currentAddress = applicationForm.getCurrentAddress();
        addressTp.setAddressLine1(currentAddress.getAddress1());
        addressTp.setAddressLine2(currentAddress.getAddress2());
        addressTp.setAddressLine3(currentAddress.getAddress3());
        addressTp.setAddressLine4(currentAddress.getAddress4());
        addressTp.setAddressLine5(currentAddress.getAddress5());
        addressTp.setCountry(currentAddress.getCountry().getCode());
        
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(personalDetails.getEmail());
        contactDtlsTp.setLandline(personalDetails.getPhoneNumber());
        return contactDtlsTp;
    }
    
    private ContactDtlsTp buildCorrespondenceAddress() {
        Address contactAddress = applicationForm.getContactAddress();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
        AddressTp addressTp = xmlFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getAddress1());
        addressTp.setAddressLine2(contactAddress.getAddress2());
        addressTp.setAddressLine3(contactAddress.getAddress3());
        addressTp.setAddressLine4(contactAddress.getAddress4());
        addressTp.setAddressLine5(contactAddress.getAddress5());
        addressTp.setCountry(contactAddress.getCountry().getCode());

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
        applicationTp.setIpAddress(applicationForm.getIpAddressAsString());
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setDepartmentalDecision(applicationForm.getStatus().displayValue());
        
//      TODO 
//      <v1_0:atasStatement>string</v1_0:atasStatement> // Project description
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
                addressTp.setAddressLine1(referee.getAddressLocation().getAddress1());
                addressTp.setAddressLine2(referee.getAddressLocation().getAddress2());
                addressTp.setAddressLine3(referee.getAddressLocation().getAddress3());
                addressTp.setAddressLine4(referee.getAddressLocation().getAddress4());
                addressTp.setAddressLine5(referee.getAddressLocation().getAddress5());
                addressTp.setCountry(referee.getAddressLocation().getCountry().getCode());
                contactDtlsTp.setAddressDtls(addressTp);
                
                refereeTp.setContactDetails(contactDtlsTp);
                
                resultList.add(refereeTp);
            }
        }
        return resultList;
    }
    
    private Collection<? extends EnglishLanguageTp> buildEnglishLanguageQualification() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ArrayList<EnglishLanguageTp> resultList = new ArrayList<EnglishLanguageTp>();
        for (LanguageQualification languageQualifications : personalDetails.getLanguageQualifications()) {
            EnglishLanguageTp englishLanguageTp = xmlFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(buildXmlDate(languageQualifications.getDateOfExamination()));
            
            if (languageQualifications.getQualificationType() == LanguageQualificationEnum.OTHER) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.OTHER);
                englishLanguageTp.setOtherLanguageExam(languageQualifications.getOtherQualificationTypeName());
            } else if (languageQualifications.getQualificationType() == LanguageQualificationEnum.TOEFL) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.TOEFL);
            } else if (languageQualifications.getQualificationType() == LanguageQualificationEnum.IELTS_ACADEMIC) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.IELTS);
            } else {
                throw new IllegalArgumentException(String.format("QualificationType type [%s] could not be converted", languageQualifications.getQualificationType()));
            }            
        
        
            if (languageQualifications.getExamTakenOnline()) {
                englishLanguageTp.setMethod("ONLINE");
            } else {
                englishLanguageTp.setMethod("WRITTEN");
            }
        
            EnglishLanguageScoreTp overallScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            overallScoreTp.setName(LanguageBandScoreTp.OVERALL);
            overallScoreTp.setScore(String.valueOf(languageQualifications.getOverallScore()));
            
            EnglishLanguageScoreTp readingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            readingScoreTp.setName(LanguageBandScoreTp.READING);
            readingScoreTp.setScore(String.valueOf(languageQualifications.getReadingScore()));
            
            EnglishLanguageScoreTp writingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            writingScoreTp.setName(LanguageBandScoreTp.WRITING);
            writingScoreTp.setScore(String.valueOf(languageQualifications.getWritingScore()));
            
            EnglishLanguageScoreTp speakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            speakingScoreTp.setName(LanguageBandScoreTp.SPEAKING);
            speakingScoreTp.setScore(String.valueOf(languageQualifications.getSpeakingcore()));
            
            EnglishLanguageScoreTp listeningScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            listeningScoreTp.setName(LanguageBandScoreTp.LISTENING);
            listeningScoreTp.setScore(String.valueOf(languageQualifications.getListeningScore()));
            
            englishLanguageTp.getLanguageScore().addAll(Arrays.asList(overallScoreTp, readingScoreTp, writingScoreTp, speakingScoreTp, listeningScoreTp));
            
            resultList.add(englishLanguageTp);
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
