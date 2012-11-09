package com.zuehlke.pgadmissions.services.exporters;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ApplicantTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.DomicileTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EmploymentDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EnglishLanguageQualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.LanguageBandScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.QualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.QualificationsinEnglishTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.RefereeListTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.SubmitAdmissionsApplicationRequest;
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

public class SubmitAdmissionsApplicationRequestBuilderV2 {

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private final static String SOURCE_IDENTIFIER = "PRISM";
    
    private final ObjectFactory xmlFactory;
    
    private final ProgramInstanceDAO programInstanceDAO;
    
    private final DatatypeFactory datatypeFactory;
    
    private ApplicationForm applicationForm;
    
    public SubmitAdmissionsApplicationRequestBuilderV2(ProgramInstanceDAO programInstanceDAO, ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        this.programInstanceDAO = programInstanceDAO;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }
    
    public SubmitAdmissionsApplicationRequestBuilderV2() {
        this(null, null);
    }
    
    public SubmitAdmissionsApplicationRequestBuilderV2 applicationForm(final ApplicationForm applicationForm) {
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
        applicationTp.setApplicant(buildApplicant());
        applicationTp.setCourseApplication(buildCourseApplication());
        return applicationTp;
    }

    private ApplicantTp buildApplicant() {
        ApplicantTp applicant = xmlFactory.createApplicantTp();
        applicant.setFullName(buildFullName());
        applicant.setSex(buildSex());        
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildNationality(0));
        applicant.setSecondaryNationality(buildNationality(1));
        applicant.setCountryOfBirth(buildCountry());
        applicant.setCountryOfDomicile(buildDomicile());
        applicant.setVisaRequired(applicationForm.getPersonalDetails().getRequiresVisa());            
        if (applicationForm.getPersonalDetails().getRequiresVisa()) {
            applicant.setPassport(buildPassport());
        }
        applicant.setDisability(buildDisability());
        applicant.setEthnicity(buildEthnicity());
        applicant.setHomeAddress(buildHomeAddress());
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress());
        applicant.setCriminalConvictionDetails(applicationForm.getAdditionalInformation().getConvictionsText());
        applicant.setCriminalConvictions(applicationForm.getAdditionalInformation().getConvictions());
        applicant.setQualificationList(buildQualificationDetails());
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification());
        applicant.setEmployerList(buildEmployer());
        
        if (StringUtils.isNotBlank( applicationForm.getApplicant().getUclUserId())) {
            applicant.setApplicantID(String.valueOf(applicationForm.getApplicant().getId()));
        } else {
            applicant.setApplicantID(applicationForm.getApplicant().getUclUserId());
        }
        
        return applicant;
    }
    
    private DomicileTp buildDomicile() {
        DomicileTp domicileTp = xmlFactory.createDomicileTp();
        domicileTp.setCode(applicationForm.getPersonalDetails().getResidenceCountry().getCode());
        domicileTp.setName(applicationForm.getPersonalDetails().getResidenceCountry().getName());
        return domicileTp;
    }

    private NameTp buildFullName() {
        NameTp nameTp = xmlFactory.createNameTp();
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        nameTp.setSurname(personalDetails.getLastName());
        nameTp.setForename1(personalDetails.getFirstName());
        nameTp.setTitle(personalDetails.getTitle().toString());
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
            nationalityTp.setName(candidateNationalities.get(idx).getName());   
            return nationalityTp;
        } else {
            return null;
        }
    }
    
    private CountryTp buildCountry() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        CountryTp countryTp = xmlFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        countryTp.setName(personalDetails.getCountry().getName());
        return countryTp;
    }
    
    private PassportTp buildPassport() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        PassportInformation passportInformation = personalDetails.getPassportInformation();
        if (passportInformation != null) {
            PassportTp passportTp = xmlFactory.createPassportTp();
            passportTp.setName(passportInformation.getNameOnPassport());
            passportTp.setNumber(passportInformation.getPassportNumber());
            passportTp.setExpiryDate(buildXmlDate(passportInformation.getPassportExpiryDate()));
            passportTp.setIssueDate(buildXmlDate(passportInformation.getPassportIssueDate()));
            return passportTp;
        }
        return null;
    }
    
    private DisabilityTp buildDisability() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        DisabilityTp disabilityTp = xmlFactory.createDisabilityTp();
        disabilityTp.setCode(Integer.toString(personalDetails.getDisability().getCode()));
        disabilityTp.setName(personalDetails.getDisability().getName());
        return disabilityTp;
    }
    
    private EthnicityTp buildEthnicity() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EthnicityTp ethnicityTp = xmlFactory.createEthnicityTp();
        ethnicityTp.setCode(Integer.toString(personalDetails.getEthnicity().getCode()));
        ethnicityTp.setName(personalDetails.getEthnicity().getName());
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
        contactDtlsTp.setLandline(StringUtils.deleteWhitespace(personalDetails.getPhoneNumber()));
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
        contactDtlsTp.setEmail(applicationForm.getPersonalDetails().getEmail());
        contactDtlsTp.setLandline(StringUtils.deleteWhitespace(applicationForm.getPersonalDetails().getPhoneNumber()));
        return contactDtlsTp;
    }
    
    private CourseApplicationTp buildCourseApplication() {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        CourseApplicationTp applicationTp = xmlFactory.createCourseApplicationTp();
        applicationTp.setExternalApplicationID(applicationForm.getApplication().getApplicationNumber());
        applicationTp.setProgramme(buildProgrammeOccurence());
        applicationTp.setStartMonth(new DateTime(programmeDetails.getStartDate()));
        if (!programmeDetails.getSuggestedSupervisors().isEmpty()) {
            // TODO: Which supervisor to pick if there are multiple
            applicationTp.setSupervisorName(buildSupervisorName(0));
        }
        applicationTp.setPersonalStatement(REFER_TO_ATTACHED_DOCUMENT);
        
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationTp));
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setApplicationStatus(applicationForm.getStatus().displayValue());
        applicationTp.setIpAddress(applicationForm.getIpAddressAsString());
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setDepartmentalDecision(applicationForm.getStatus().displayValue());
        applicationTp.setRefereeList(buildReferee());
        
//      TODO: applicationTp.setApplicationStatus("Active"); ?
//      <v1_0:atasStatement>string</v1_0:atasStatement> // Project description
        
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
        occurrenceTp.setIdentifier(currentProgramInstanceForStudyOption.getIdentifier());
        occurrenceTp.setStartDate(buildXmlDate(currentProgramInstanceForStudyOption.getApplicationStartDate()));
        occurrenceTp.setEndDate(buildXmlDate(currentProgramInstanceForStudyOption.getApplicationDeadline()));
        return occurrenceTp;
    }
    
    private ModeofattendanceTp buildModeofattendance() {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        ModeofattendanceTp modeofattendanceTp = xmlFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOptionCode());
        modeofattendanceTp.setName(programmeDetails.getStudyOption());
        return modeofattendanceTp;
    }
    
    private NameTp buildSupervisorName(int idx) {
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
        interestTp.setName(sourcesOfInterest.getName());
        if (sourcesOfInterest.isFreeText()) {
            applicationTp.setOtherSourceofInterest(programmeDetails.getSourcesOfInterestText());
        }
        return interestTp;
    }

    private QualificationDetailsTp buildQualificationDetails() {
        QualificationDetailsTp resultList = xmlFactory.createQualificationDetailsTp();
        
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
                qualificationTp.setName(qualification.getQualificationType().getName());
                qualificationsTp.setQualification(qualificationTp);
                
                InstitutionTp institutionTp = xmlFactory.createInstitutionTp();
                // TODO: we allow free text
                //institutionTp.setCode("");
                institutionTp.setCode("CO0031");
                institutionTp.setName(qualification.getQualificationInstitution());
                
                CountryTp countryTp = xmlFactory.createCountryTp();
                countryTp.setCode(qualification.getInstitutionCountry().getCode());
                countryTp.setName(qualification.getInstitutionCountry().getName());
                institutionTp.setCountry(countryTp);
                
                qualificationsTp.setInstitution(institutionTp);
                
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        }
        return resultList;
    }
    
    private EmploymentDetailsTp buildEmployer() {
        EmploymentDetailsTp resultList = xmlFactory.createEmploymentDetailsTp();
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
                
                resultList.getEmployer().add(appointmentTp);
            }
        }
        return resultList;
    }
    
    private RefereeListTp buildReferee() {
        RefereeListTp resultList = xmlFactory.createRefereeListTp();
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
                contactDtlsTp.setLandline(StringUtils.deleteWhitespace(referee.getPhoneNumber()));
                
                AddressTp addressTp = xmlFactory.createAddressTp();
                addressTp.setAddressLine1(referee.getAddressLocation().getAddress1());
                addressTp.setAddressLine2(referee.getAddressLocation().getAddress2());
                addressTp.setAddressLine3(referee.getAddressLocation().getAddress3());
                addressTp.setAddressLine4(referee.getAddressLocation().getAddress4());
                addressTp.setAddressLine5(referee.getAddressLocation().getAddress5());
                addressTp.setCountry(referee.getAddressLocation().getCountry().getCode());
                contactDtlsTp.setAddressDtls(addressTp);
                
                refereeTp.setContactDetails(contactDtlsTp);
                
                resultList.getReferee().add(refereeTp);
            }
        }
        return resultList;
    }
    
    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EnglishLanguageQualificationDetailsTp resultList = xmlFactory.createEnglishLanguageQualificationDetailsTp();
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
            
            resultList.getEnglishLanguageQualification().add(englishLanguageTp);
        }
        return resultList;
    }
    
    private XMLGregorianCalendar buildXmlDate(Date date) {
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return datatypeFactory.newXMLGregorianCalendar(gc);
        }
        return null;
    }
    
    private XMLGregorianCalendar buildXmlDateYearOnly(String date) {
        if (date != null) {
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(Integer.valueOf(date));
            return xmlCalendar;
        } 
        return null;
    }
}
