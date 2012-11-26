package com.zuehlke.pgadmissions.services.exporters;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicantTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.DomicileTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EmploymentDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EnglishLanguageQualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.LanguageBandScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationsinEnglishTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.RefereeListTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
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
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class SubmitAdmissionsApplicationRequestBuilder {

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private final static String SOURCE_IDENTIFIER = "PRISM";
    
    private final ObjectFactory xmlFactory;
    
    private final QualificationInstitutionDAO qualificationInstitutionDAO;
    
    private final DatatypeFactory datatypeFactory;
    
    private ApplicationForm applicationForm;
    
    public SubmitAdmissionsApplicationRequestBuilder(QualificationInstitutionDAO qualificationInstitutionDAO, ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        this.qualificationInstitutionDAO = qualificationInstitutionDAO;
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
        applicant.setEnglishIsFirstLanguage(applicationForm.getPersonalDetails().getEnglishFirstLanguage());
        
        if (StringUtils.isNotBlank(applicationForm.getApplicant().getUclUserId())) {
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
        nameTp.setTitle(personalDetails.getTitle().getDisplayValue());
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
        contactDtlsTp.setLandline(cleanPhoneNumber(personalDetails.getPhoneNumber()));
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
        contactDtlsTp.setLandline(cleanPhoneNumber(applicationForm.getPersonalDetails().getPhoneNumber()));
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
        applicationTp.setRefereeList(buildReferee());
        
        if (applicationForm.getStatus() == ApplicationFormStatus.WITHDRAWN) {
            applicationTp.setApplicationStatus("W");
        } else {
            applicationTp.setApplicationStatus("A");
        }
        
        applicationTp.setDepartmentalDecision(applicationForm.getStatus().displayValue().toUpperCase());

//      TODO: ATASSTatement
//      <v1_0:atasStatement>string</v1_0:atasStatement> // Project description
        
        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence() {
        Program program = applicationForm.getProgram();
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        ProgrammeOccurrenceTp occurrenceTp = xmlFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance());

        ProgramInstance activeInstance = null;
        for (ProgramInstance instance : program.getInstances()) {
            if (com.zuehlke.pgadmissions.utils.DateUtils.isToday(instance.getApplicationStartDate()) || instance.getApplicationStartDate().after(new Date())) {
                if (instance.getStudyOption().equalsIgnoreCase(programmeDetails.getStudyOption())) {
                    activeInstance = instance;                    
                    break;
                }
            }
        }
        
        if (activeInstance == null) {
            throw new IllegalArgumentException("No active program instance found");
        }
        
        if (StringUtils.isBlank(activeInstance.getIdentifier())) {
            throw new IllegalArgumentException("No identifer for program instance found");
        }
        
        occurrenceTp.setAcademicYear(buildXmlDateYearOnly(activeInstance.getAcademic_year()));
        occurrenceTp.setIdentifier(activeInstance.getIdentifier());
        occurrenceTp.setStartDate(buildXmlDate(activeInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(buildXmlDate(activeInstance.getApplicationDeadline()));
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
                
                if (qualification.getQualificationAwardDate() == null) {
                    // endDate is a mandatory fields. We do not collect an end date if it is the 
                    // current position. Just add a year in the future to now (Alastair)
                    qualificationsTp.setEndDate(buildXmlDate(DateUtils.addYears(new Date(), 1)));
                } else {
                    qualificationsTp.setEndDate(buildXmlDate(qualification.getQualificationAwardDate()));
                }
                
                qualificationsTp.setGrade(qualification.getQualificationGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getQualificationLanguage());
                qualificationsTp.setMainSubject(qualification.getQualificationSubject());
                
                QualificationTp qualificationTp = xmlFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getQualificationType().getCode());
                qualificationTp.setName(qualification.getQualificationType().getName());
                qualificationsTp.setQualification(qualificationTp);
                
                InstitutionTp institutionTp = xmlFactory.createInstitutionTp();
                
                QualificationInstitution appropriateInstitution = selectAppropriateInstitution(qualification.getQualificationInstitution());
                if (appropriateInstitution == null) {
                    institutionTp.setCode("OTHER");
                    institutionTp.setName(qualification.getQualificationInstitution());
                    CountryTp countryTp = xmlFactory.createCountryTp();
                    countryTp.setCode(qualification.getInstitutionCountry().getCode());
                    countryTp.setName(qualification.getInstitutionCountry().getName());
                    institutionTp.setCountry(countryTp);
                } else {
                    institutionTp.setCode(appropriateInstitution.getCode());
                    institutionTp.setName(appropriateInstitution.getName());
                    CountryTp countryTp = xmlFactory.createCountryTp();
                    countryTp.setCode(appropriateInstitution.getDomicileCode());
                    institutionTp.setCountry(countryTp);
                }
                qualificationsTp.setInstitution(institutionTp);
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        }
        return resultList;
    }
    
    private QualificationInstitution selectAppropriateInstitution(String name) {
        List<QualificationInstitution> institutionsByName = qualificationInstitutionDAO.getAllInstitutionByName(name);
        QualificationInstitution selectedInstitution = null;
        for (QualificationInstitution institution : institutionsByName) {
            if (institution.getEnabled()) {
                selectedInstitution = institution;
            }
        }
        if (selectedInstitution == null && !institutionsByName.isEmpty()) {
            institutionsByName.get(0);
        }
        return selectedInstitution;
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
                contactDtlsTp.setLandline(cleanPhoneNumber(referee.getPhoneNumber()));
                
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
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = xmlFactory.createEnglishLanguageQualificationDetailsTp();
        
        for (LanguageQualification languageQualifications : personalDetails.getLanguageQualifications()) {
            EnglishLanguageTp englishLanguageTp = xmlFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(buildXmlDate(languageQualifications.getDateOfExamination()));
            
            if (languageQualifications.getQualificationType() == LanguageQualificationEnum.OTHER) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.OTHER);
                englishLanguageTp.setOtherLanguageExam(languageQualifications.getOtherQualificationTypeName());
            } else if (languageQualifications.getQualificationType() == LanguageQualificationEnum.TOEFL) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.TOEFL);
                if (languageQualifications.getExamTakenOnline()) {
                    englishLanguageTp.setMethod("TOEFL_INTERNET");
                } else {
                    englishLanguageTp.setMethod("TOEFL_PAPER");
                }
            } else if (languageQualifications.getQualificationType() == LanguageQualificationEnum.IELTS_ACADEMIC) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.IELTS);
            } else {
                throw new IllegalArgumentException(String.format("QualificationType type [%s] could not be converted", languageQualifications.getQualificationType()));
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
            
            EnglishLanguageScoreTp essayScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            essayScoreTp.setName(LanguageBandScoreTp.ESSAY);
            essayScoreTp.setScore(String.valueOf(languageQualifications.getWritingScore()));
            
            EnglishLanguageScoreTp speakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            speakingScoreTp.setName(LanguageBandScoreTp.SPEAKING);
            speakingScoreTp.setScore(String.valueOf(languageQualifications.getSpeakingcore()));
            
            EnglishLanguageScoreTp listeningScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            listeningScoreTp.setName(LanguageBandScoreTp.LISTENING);
            listeningScoreTp.setScore(String.valueOf(languageQualifications.getListeningScore()));
            
            englishLanguageTp.getLanguageScore().addAll(Arrays.asList(overallScoreTp, readingScoreTp, writingScoreTp, essayScoreTp, speakingScoreTp, listeningScoreTp));
            
            englishLanguageQualificationDetailsTp.getEnglishLanguageQualification().add(englishLanguageTp);
        }
        return englishLanguageQualificationDetailsTp;
    }
    
    private String cleanPhoneNumber(String number) {
        return number.replaceAll("[^0-9()+ ]", ""); 
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
