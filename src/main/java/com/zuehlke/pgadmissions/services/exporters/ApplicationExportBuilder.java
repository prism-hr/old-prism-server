package com.zuehlke.pgadmissions.services.exporters;

import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;

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
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.User;

@Component
public class ApplicationExportBuilder {

    private static final String IP_ADDRESS_NOT_PROVIDED_VALUE = "127.0.0.1";

    private static final String NOT_PROVIDED_VALUE = "NOT PROVIDED";

    private static final String INSTITUTION_OTHER_CODE = "OTHER";

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private static final String SOURCE_IDENTIFIER = "PRISM";

    private static final String PHONE_NUMBER_NOT_PROVIDED_VALUE = "+44 (0) 0000 000 000";

    private ObjectFactory objectFactory;

    private DatatypeFactory datatypeFactory;

    private Application application;

    private String creatorInstitutionApplicantId;

    private String creatorIpAddress;

    private Comment offerRecommendationComment;

    private ProgramInstance exportProgramInstance;

    private User primarySupervisor;

    private List<ApplicationReferee> exportReferees;

    public SubmitAdmissionsApplicationRequest build(Application application, String creatorInstitutionApplicantId, String creatorIpAddress,
            Comment offerRecommendationComment, User primarySupervisor, ProgramInstance exportProgramInstance, List<ApplicationReferee> exportReferees)
            throws DatatypeConfigurationException {
        ObjectFactory xmlFactory = new ObjectFactory();
        datatypeFactory = DatatypeFactory.newInstance();
        this.application = application;
        this.creatorInstitutionApplicantId = creatorInstitutionApplicantId;
        this.creatorIpAddress = creatorIpAddress;
        this.offerRecommendationComment = offerRecommendationComment;
        this.primarySupervisor = primarySupervisor;
        this.exportReferees = exportReferees;
        SubmitAdmissionsApplicationRequest request = xmlFactory.createSubmitAdmissionsApplicationRequest();
        request.setApplication(buildApplication());
        return request;
    }

    private ApplicationTp buildApplication() {
        ApplicationTp applicationTp = objectFactory.createApplicationTp();
        applicationTp.setSource(SOURCE_IDENTIFIER);
        applicationTp.setApplicant(buildApplicant());
        applicationTp.setCourseApplication(buildCourseApplication());
        return applicationTp;
    }

    private ApplicantTp buildApplicant() {
        ApplicantTp applicant = objectFactory.createApplicantTp();
        applicant.setFullName(buildFullName());
        applicant.setSex(buildGender());
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildFirstNationality());
        applicant.setSecondaryNationality(buildSecondNationality());
        applicant.setCountryOfBirth(buildCountry());
        applicant.setCountryOfDomicile(buildDomicile());
        applicant.setVisaRequired(BooleanUtils.toBoolean(application.getPersonalDetails().getVisaRequired()));

        if (BooleanUtils.isTrue(application.getPersonalDetails().getVisaRequired())) {
            applicant.setPassport(buildPassport());
        }

        applicant.setDisability(buildDisability());
        applicant.setEthnicity(buildEthnicity());
        applicant.setHomeAddress(buildHomeAddress());
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress());
        applicant.setCriminalConvictionDetails(cleanString(application.getAdditionalInformation().getConvictionsText()));
        applicant.setCriminalConvictions(application.getAdditionalInformation().getConvictionsText() != null);
        applicant.setQualificationList(buildQualificationDetails());
        applicant.setEmployerList(buildEmployer());
        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(application.getPersonalDetails().getFirstLanguageEnglish()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification());
        applicant.setApplicantID(StringUtils.trimToNull(creatorInstitutionApplicantId));

        return applicant;
    }

    private DomicileTp buildDomicile() {
        DomicileTp domicileTp = objectFactory.createDomicileTp();
        domicileTp.setCode(application.getPersonalDetails().getResidenceCountry().getCode());
        domicileTp.setName(application.getPersonalDetails().getResidenceCountry().getName());
        return domicileTp;
    }

    private NameTp buildFullName() {
        NameTp nameTp = objectFactory.createNameTp();
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        User applicant = application.getUser();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
        nameTp.setTitle(personalDetails.getTitle().getName());
        return nameTp;
    }

    private GenderTp buildGender() {
        Gender gender = application.getPersonalDetails().getGender();
        return GenderTp.valueOf(gender.getCode());
    }

    private XMLGregorianCalendar buildDateOfBirth() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        return buildXmlDate(personalDetails.getDateOfBirth());
    }

    private NationalityTp buildFirstNationality() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        Language firstNationality = personalDetails.getFirstNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();
        nationalityTp.setCode(firstNationality.getCode());
        nationalityTp.setName(firstNationality.getName());
        return nationalityTp;
    }

    private NationalityTp buildSecondNationality() {
        Language secondNationality = application.getPersonalDetails().getSecondNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();

        if (secondNationality == null) {
            return null;
        } else {
            nationalityTp.setCode(secondNationality.getCode());
            nationalityTp.setName(secondNationality.getName());
            return nationalityTp;
        }
    }

    private CountryTp buildCountry() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        CountryTp countryTp = objectFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        countryTp.setName(personalDetails.getCountry().getName());
        return countryTp;
    }

    private PassportTp buildPassport() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails.getPassportAvailable()) {
            PassportTp passportTp = objectFactory.createPassportTp();
            passportTp.setName(personalDetails.getPassport().getName());
            passportTp.setNumber(personalDetails.getPassport().getNumber());
            passportTp.setExpiryDate(buildXmlDate(personalDetails.getPassport().getExpiryDate()));
            passportTp.setIssueDate(buildXmlDate(personalDetails.getPassport().getIssueDate()));
            return passportTp;
        } else {
            PassportTp passportTp = objectFactory.createPassportTp();
            passportTp.setName(NOT_PROVIDED_VALUE);
            passportTp.setNumber(NOT_PROVIDED_VALUE);
            passportTp.setExpiryDate(buildXmlDate(new LocalDate().plusYears(1)));
            passportTp.setIssueDate(buildXmlDate(new LocalDate().minusYears(1)));
            return passportTp;
        }
    }

    private DisabilityTp buildDisability() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        DisabilityTp disabilityTp = objectFactory.createDisabilityTp();
        disabilityTp.setCode(personalDetails.getDisability().getCode());
        disabilityTp.setName(personalDetails.getDisability().getName());
        return disabilityTp;
    }

    private EthnicityTp buildEthnicity() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        EthnicityTp ethnicityTp = objectFactory.createEthnicityTp();
        ethnicityTp.setCode(personalDetails.getEthnicity().getCode());
        ethnicityTp.setName(personalDetails.getEthnicity().getName());
        return ethnicityTp;
    }

    private ContactDtlsTp buildHomeAddress() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        Address currentAddress = application.getAddress().getCurrentAddress();
        addressTp.setAddressLine1(currentAddress.getAddressLine1());
        addressTp.setAddressLine2(currentAddress.getAddressLine2());
        addressTp.setAddressLine3(currentAddress.getAddressTown());
        addressTp.setAddressLine4(currentAddress.getAddressRegion());
        addressTp.setPostCode(currentAddress.getAddressCode());
        addressTp.setCountry(currentAddress.getDomicile().getCode());
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(cleanPhoneNumber(personalDetails.getPhoneNumber()));
        return contactDtlsTp;
    }

    private ContactDtlsTp buildCorrespondenceAddress() {
        Address contactAddress = application.getAddress().getContactAddress();
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getAddressLine1());
        addressTp.setAddressLine2(contactAddress.getAddressLine2());
        addressTp.setAddressLine3(contactAddress.getAddressTown());
        addressTp.setAddressLine4(contactAddress.getAddressRegion());
        addressTp.setPostCode(contactAddress.getAddressCode());
        addressTp.setCountry(contactAddress.getDomicile().getCode());
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(cleanPhoneNumber(application.getPersonalDetails().getPhoneNumber()));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication() {
        ApplicationProgramDetails programDetails = application.getProgramDetails();
        CourseApplicationTp applicationTp = objectFactory.createCourseApplicationTp();
        applicationTp.setStartMonth(new DateTime(programDetails.getStartDate()));
        applicationTp.setAgreedSupervisorName(buildAgreedSupervisorName());
        applicationTp.setPersonalStatement(REFER_TO_ATTACHED_DOCUMENT);
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationTp));
        applicationTp.setCreationDate(buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setExternalApplicationID(application.getCode());
        applicationTp.setIpAddress(creatorIpAddress == null ? IP_ADDRESS_NOT_PROVIDED_VALUE : creatorIpAddress);
        applicationTp.setCreationDate(buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee());

        switch (application.getState().getStateGroup().getId()) {
        case APPLICATION_WITHDRAWN:
            applicationTp.setApplicationStatus("WITHDRAWN");
            break;
        case APPLICATION_APPROVED:
            applicationTp.setApplicationStatus("ACTIVE");
            applicationTp.setDepartmentalDecision("OFFER");
            break;
        case APPLICATION_REJECTED:
            applicationTp.setApplicationStatus("ACTIVE");
            applicationTp.setDepartmentalDecision("REJECT");
            break;
        default:
            throw new Error("Application in state " + application.getState().getId().name() + " cannot be exported");
        }

        applicationTp.setProgramme(buildProgrammeOccurence());

        if (offerRecommendationComment != null) {
            applicationTp.setAtasStatement(offerRecommendationComment.getPositionDescription());

            String conditions = offerRecommendationComment.getAppointmentConditions();
            String offerSummary = "Recommended conditions: " + conditions == null ? "none" : conditions + "\n\n" + "Preferred Start Date: "
                    + offerRecommendationComment.getPositionProvisionalStartDate().toString("d MMMM yyyy");

            applicationTp.setDepartmentalOfferConditions(offerSummary);
        }

        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence() {
        Program program = application.getProgram();
        ProgrammeOccurrenceTp occurrenceTp = objectFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance());
        occurrenceTp.setAcademicYear(buildXmlDateYearOnly(exportProgramInstance.getAcademicYear()));
        String exportInstanceIdentifier = exportProgramInstance.getIdentifier();
        occurrenceTp.setIdentifier(exportInstanceIdentifier == null ? NOT_PROVIDED_VALUE : exportInstanceIdentifier);
        occurrenceTp.setStartDate(buildXmlDate(exportProgramInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(buildXmlDate(exportProgramInstance.getApplicationDeadline()));
        return occurrenceTp;
    }

    private ModeofattendanceTp buildModeofattendance() {
        ApplicationProgramDetails programmeDetails = application.getProgramDetails();
        ModeofattendanceTp modeofattendanceTp = objectFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOption().getCode());
        modeofattendanceTp.setName(programmeDetails.getStudyOption().getName());
        return modeofattendanceTp;
    }

    private NameTp buildAgreedSupervisorName() {
        if (primarySupervisor != null) {
            NameTp nameTp = objectFactory.createNameTp();
            nameTp.setForename1(primarySupervisor.getFirstName());
            nameTp.setSurname(primarySupervisor.getLastName());
            return nameTp;
        }
        return null;
    }

    private SourceOfInterestTp buildSourcesOfInterest(CourseApplicationTp applicationTp) {
        ApplicationProgramDetails programmeDetails = application.getProgramDetails();
        SourceOfInterestTp interestTp = objectFactory.createSourceOfInterestTp();
        ReferralSource sourcesOfInterest = programmeDetails.getReferralSource();
        if (sourcesOfInterest == null) {
            return null;
        }
        interestTp.setCode(sourcesOfInterest.getCode());
        interestTp.setName(sourcesOfInterest.getName());
        return interestTp;
    }

    private QualificationDetailsTp buildQualificationDetails() {
        QualificationDetailsTp resultList = objectFactory.createQualificationDetailsTp();

        List<ApplicationQualification> qualifications = application.getQualifications();
        if (!qualifications.isEmpty()) {
            for (ApplicationQualification qualification : qualifications) {
                QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();

                qualificationsTp.setStartDate(buildXmlDate(qualification.getStartDate()));
                qualificationsTp.setEndDate(buildXmlDate(qualification.getAwardDate()));

                qualificationsTp.setGrade(qualification.getGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getLanguage());
                qualificationsTp.setMainSubject(qualification.getSubject());

                QualificationTp qualificationTp = objectFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getType().getCode());
                qualificationTp.setName(qualification.getType().getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = objectFactory.createInstitutionTp();

                institutionTp.setCode(qualification.getInstitution().getCode());
                institutionTp.setName(qualification.getInstitution().getName());

                CountryTp countryTp = objectFactory.createCountryTp();
                countryTp.setCode(qualification.getInstitution().getDomicile().getCode());
                countryTp.setName(qualification.getInstitution().getDomicile().getName());
                institutionTp.setCountry(countryTp);

                qualificationsTp.setInstitution(institutionTp);
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        } else {
            QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();
            qualificationsTp.setStartDate(buildXmlDate(new LocalDate().minusYears(1)));
            qualificationsTp.setEndDate(buildXmlDate(new LocalDate().plusYears(1)));

            qualificationsTp.setGrade(NOT_PROVIDED_VALUE);
            qualificationsTp.setLanguageOfInstruction(NOT_PROVIDED_VALUE);
            qualificationsTp.setMainSubject(NOT_PROVIDED_VALUE);

            QualificationTp qualificationTp = objectFactory.createQualificationTp();
            qualificationTp.setCode("6");
            qualificationTp.setName("Other examinations and/or information");
            qualificationsTp.setQualification(qualificationTp);

            InstitutionTp institutionTp = objectFactory.createInstitutionTp();
            institutionTp.setCode(INSTITUTION_OTHER_CODE);
            institutionTp.setName(NOT_PROVIDED_VALUE);
            CountryTp countryTp = objectFactory.createCountryTp();
            countryTp.setCode("XK");
            countryTp.setName("United Kingdom");
            institutionTp.setCountry(countryTp);

            qualificationsTp.setInstitution(institutionTp);
            resultList.getQualificationDetail().add(qualificationsTp);
        }
        return resultList;
    }

    private EmploymentDetailsTp buildEmployer() {
        EmploymentDetailsTp resultList = objectFactory.createEmploymentDetailsTp();
        List<ApplicationEmploymentPosition> employmentPositions = application.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (ApplicationEmploymentPosition employmentPosition : employmentPositions) {
                AppointmentTp appointmentTp = objectFactory.createAppointmentTp();

                appointmentTp.setJobTitle(employmentPosition.getPosition());
                appointmentTp.setResponsibilities(cleanString(employmentPosition.getRemit()));
                appointmentTp.setStartDate(buildXmlDate(employmentPosition.getStartDate()));
                appointmentTp.setEndDate(buildXmlDate(employmentPosition.getEndDate()));

                EmployerTp employerTp = objectFactory.createEmployerTp();
                employerTp.setName(employmentPosition.getEmployerName());
                appointmentTp.setEmployer(employerTp);

                resultList.getEmployer().add(appointmentTp);
            }
        }
        return resultList;
    }

    private RefereeListTp buildReferee() {
        RefereeListTp resultList = objectFactory.createRefereeListTp();
        for (ApplicationReferee referee : exportReferees) {
            RefereeTp refereeTp = objectFactory.createRefereeTp();
            refereeTp.setPosition(referee.getJobTitle());
            NameTp nameTp = objectFactory.createNameTp();
            nameTp.setForename1(referee.getUser().getFirstName());
            nameTp.setSurname(referee.getUser().getLastName());
            refereeTp.setName(nameTp);

            ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
            contactDtlsTp.setEmail(referee.getUser().getEmail());
            contactDtlsTp.setLandline(cleanPhoneNumber(referee.getPhoneNumber()));

            if (StringUtils.isBlank(referee.getPhoneNumber())) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            } else if (!ESAPI.validator().isValidInput("PhoneNumber", referee.getPhoneNumber(), "PhoneNumber", 25, false)) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            }

            AddressTp addressTp = objectFactory.createAddressTp();
            addressTp.setAddressLine1(referee.getAddress().getAddressLine1());
            addressTp.setAddressLine2(referee.getAddress().getAddressLine2());
            addressTp.setAddressLine3(referee.getAddress().getAddressTown());
            addressTp.setAddressLine4(referee.getAddress().getAddressRegion());
            addressTp.setPostCode(referee.getAddress().getAddressCode());
            addressTp.setCountry(referee.getAddress().getDomicile().getCode());
            contactDtlsTp.setAddressDtls(addressTp);
            refereeTp.setContactDetails(contactDtlsTp);
            resultList.getReferee().add(refereeTp);
        }
        return resultList;
    }

    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification() {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = objectFactory.createEnglishLanguageQualificationDetailsTp();

        if (personalDetails.getLanguageQualificationAvailable()) {
            ApplicationLanguageQualification languageQualification = personalDetails.getLanguageQualification();
            EnglishLanguageTp englishLanguageTp = objectFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(buildXmlDate(languageQualification.getExamDate()));

            if (languageQualification.getType().getCode().startsWith("CUST")) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.OTHER);
                englishLanguageTp.setOtherLanguageExam(languageQualification.getType().getName());
            } else if (languageQualification.getType().getCode().startsWith("TOEFL")) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.TOEFL);
                englishLanguageTp.setMethod(languageQualification.getType().getCode());
            } else {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.IELTS);
            }

            EnglishLanguageScoreTp overallScoreTp = objectFactory.createEnglishLanguageScoreTp();
            overallScoreTp.setName(LanguageBandScoreTp.OVERALL);
            overallScoreTp.setScore(languageQualification.getOverallScore().replace(".0", ""));

            EnglishLanguageScoreTp readingScoreTp = objectFactory.createEnglishLanguageScoreTp();
            readingScoreTp.setName(LanguageBandScoreTp.READING);
            readingScoreTp.setScore(languageQualification.getReadingScore().replace(".0", ""));

            EnglishLanguageScoreTp writingScoreTp = objectFactory.createEnglishLanguageScoreTp();
            writingScoreTp.setName(LanguageBandScoreTp.WRITING);
            writingScoreTp.setScore(languageQualification.getWritingScore().replace(".0", ""));

            EnglishLanguageScoreTp essayOrSpeakingScoreTp = null;
            if (StringUtils.equalsIgnoreCase("TOEFL_PAPER", englishLanguageTp.getMethod())) {
                essayOrSpeakingScoreTp = objectFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.ESSAY);
                essayOrSpeakingScoreTp.setScore(languageQualification.getWritingScore().replace(".0", ""));
            } else {
                essayOrSpeakingScoreTp = objectFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.SPEAKING);
                essayOrSpeakingScoreTp.setScore(languageQualification.getSpeakingScore().replace(".0", ""));
            }

            EnglishLanguageScoreTp listeningScoreTp = objectFactory.createEnglishLanguageScoreTp();
            listeningScoreTp.setName(LanguageBandScoreTp.LISTENING);
            listeningScoreTp.setScore(languageQualification.getListeningScore().replace(".0", ""));

            englishLanguageTp.getLanguageScore()
                    .addAll(Arrays.asList(overallScoreTp, readingScoreTp, writingScoreTp, essayOrSpeakingScoreTp, listeningScoreTp));
            englishLanguageQualificationDetailsTp.getEnglishLanguageQualification().add(englishLanguageTp);
        }
        return englishLanguageQualificationDetailsTp;
    }

    private String cleanString(String text) {
        if (text != null) {
            return text.replaceAll("[^\\x20-\\x7F|\\x80-\\xFD|\\n|\\r]", "");
        }
        return null;
    }

    private String cleanPhoneNumber(String number) {
        return number.replaceAll("[^0-9()+ ]", "");
    }

    private XMLGregorianCalendar buildXmlDate(LocalDate localDate) {
        return buildXmlDate(localDate.toDateTimeAtStartOfDay());
    }

    private XMLGregorianCalendar buildXmlDate(DateTime dateTime) {
        if (dateTime != null) {
            return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
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
