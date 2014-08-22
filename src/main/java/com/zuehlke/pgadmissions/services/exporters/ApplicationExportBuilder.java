package com.zuehlke.pgadmissions.services.exporters;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.zuehlke.pgadmissions.dto.ApplicationExportDTO;

@Component
public class ApplicationExportBuilder {

    private final ObjectFactory objectFactory = new ObjectFactory();
    
    @Value("${xml.export.source}")
    private String exportSource;

    @Value("${xml.export.not.provided}")
    private String exportNotProvided;

    @Value("${xml.export.refer.to.document}")
    private String exportReferToDocument;

    @Value("${xml.export.ip.not.provided}")
    private String exportIpNotProvided;

    @Value("${xml.export.other.provided}")
    private String exportOtherProvided;

    @Value("${xml.export.telephone.not.provided}")
    private String exportTelephoneNotProvided;

    @Autowired
    private ApplicationExportBuilderHelper applicationExportBuilderHelper;

    public SubmitAdmissionsApplicationRequest build(ApplicationExportDTO applicationExportDTO) throws DatatypeConfigurationException {
        SubmitAdmissionsApplicationRequest request = objectFactory.createSubmitAdmissionsApplicationRequest();
        request.setApplication(buildApplication(applicationExportDTO));
        return request;
    }

    private ApplicationTp buildApplication(ApplicationExportDTO applicationExportDTO) {
        ApplicationTp applicationTp = objectFactory.createApplicationTp();
        applicationTp.setSource(exportSource);
        applicationTp.setApplicant(buildApplicant(applicationExportDTO));
        applicationTp.setCourseApplication(buildCourseApplication(applicationExportDTO));
        return applicationTp;
    }

    private ApplicantTp buildApplicant(ApplicationExportDTO applicationExportDTO) {
        Application application = applicationExportDTO.getApplication();

        ApplicantTp applicant = objectFactory.createApplicantTp();
        applicant.setFullName(buildFullName(application));
        applicant.setSex(buildGender(application));
        applicant.setDateOfBirth(buildDateOfBirth(application));
        applicant.setNationality(buildFirstNationality(application));
        applicant.setSecondaryNationality(buildSecondNationality(application));
        applicant.setCountryOfBirth(buildCountry(application));
        applicant.setCountryOfDomicile(buildDomicile(application));
        applicant.setVisaRequired(BooleanUtils.toBoolean(application.getPersonalDetails().getVisaRequired()));

        if (BooleanUtils.isTrue(application.getPersonalDetails().getVisaRequired())) {
            applicant.setPassport(buildPassport(application));
        }

        applicant.setDisability(buildDisability(application));
        applicant.setEthnicity(buildEthnicity(application));
        applicant.setHomeAddress(buildHomeAddress(application));
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress(application));
        applicant.setCriminalConvictionDetails(applicationExportBuilderHelper.cleanString(application.getAdditionalInformation().getConvictionsText()));
        applicant.setCriminalConvictions(application.getAdditionalInformation().getConvictionsText() != null);
        applicant.setQualificationList(buildQualificationDetails(application));
        applicant.setEmployerList(buildEmployer(application));
        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(application.getPersonalDetails().getFirstLanguageEnglish()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification(application));
        applicant.setApplicantID(StringUtils.trimToNull(applicationExportDTO.getCreatorExportId()));

        return applicant;
    }

    private DomicileTp buildDomicile(Application application) {
        DomicileTp domicileTp = objectFactory.createDomicileTp();
        domicileTp.setCode(application.getPersonalDetails().getResidenceCountry().getCode());
        domicileTp.setName(application.getPersonalDetails().getResidenceCountry().getName());
        return domicileTp;
    }

    private NameTp buildFullName(Application application) {
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

    private GenderTp buildGender(Application application) {
        Gender gender = application.getPersonalDetails().getGender();
        return GenderTp.valueOf(gender.getCode());
    }

    private XMLGregorianCalendar buildDateOfBirth(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        return applicationExportBuilderHelper.buildXmlDate(personalDetails.getDateOfBirth());
    }

    private NationalityTp buildFirstNationality(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        Language firstNationality = personalDetails.getFirstNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();
        nationalityTp.setCode(firstNationality.getCode());
        nationalityTp.setName(firstNationality.getName());
        return nationalityTp;
    }

    private NationalityTp buildSecondNationality(Application application) {
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

    private CountryTp buildCountry(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        CountryTp countryTp = objectFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        countryTp.setName(personalDetails.getCountry().getName());
        return countryTp;
    }

    private PassportTp buildPassport(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails.getPassportAvailable()) {
            PassportTp passportTp = objectFactory.createPassportTp();
            passportTp.setName(personalDetails.getPassport().getName());
            passportTp.setNumber(personalDetails.getPassport().getNumber());
            passportTp.setExpiryDate(applicationExportBuilderHelper.buildXmlDate(personalDetails.getPassport().getExpiryDate()));
            passportTp.setIssueDate(applicationExportBuilderHelper.buildXmlDate(personalDetails.getPassport().getIssueDate()));
            return passportTp;
        } else {
            PassportTp passportTp = objectFactory.createPassportTp();
            passportTp.setName(exportNotProvided);
            passportTp.setNumber(exportNotProvided);
            passportTp.setExpiryDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().plusYears(1)));
            passportTp.setIssueDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().minusYears(1)));
            return passportTp;
        }
    }

    private DisabilityTp buildDisability(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        DisabilityTp disabilityTp = objectFactory.createDisabilityTp();
        disabilityTp.setCode(personalDetails.getDisability().getCode());
        disabilityTp.setName(personalDetails.getDisability().getName());
        return disabilityTp;
    }

    private EthnicityTp buildEthnicity(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        EthnicityTp ethnicityTp = objectFactory.createEthnicityTp();
        ethnicityTp.setCode(personalDetails.getEthnicity().getCode());
        ethnicityTp.setName(personalDetails.getEthnicity().getName());
        return ethnicityTp;
    }

    private ContactDtlsTp buildHomeAddress(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        Address currentAddress = application.getAddress().getCurrentAddress();
        addressTp.setAddressLine1(currentAddress.getAddressLine1());
        addressTp.setAddressLine2(currentAddress.getAddressLine2());
        addressTp.setAddressLine3(currentAddress.getAddressTown());
        addressTp.setAddressLine4(currentAddress.getAddressRegion());

        String addressCode = currentAddress.getAddressCode();
        addressTp.setPostCode(addressCode == null ? exportNotProvided : addressCode);

        addressTp.setCountry(currentAddress.getDomicile().getCode());
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(applicationExportBuilderHelper.cleanPhoneNumber(personalDetails.getPhoneNumber()));
        return contactDtlsTp;
    }

    private ContactDtlsTp buildCorrespondenceAddress(Application application) {
        Address contactAddress = application.getAddress().getContactAddress();
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getAddressLine1());
        addressTp.setAddressLine2(contactAddress.getAddressLine2());
        addressTp.setAddressLine3(contactAddress.getAddressTown());
        addressTp.setAddressLine4(contactAddress.getAddressRegion());

        String addressCode = contactAddress.getAddressCode();
        addressTp.setPostCode(addressCode == null ? exportNotProvided : addressCode);

        addressTp.setCountry(contactAddress.getDomicile().getCode());
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(applicationExportBuilderHelper.cleanPhoneNumber(application.getPersonalDetails().getPhoneNumber()));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication(ApplicationExportDTO applicationExportDTO) {
        Application application = applicationExportDTO.getApplication();

        ApplicationProgramDetails programDetails = application.getProgramDetails();
        CourseApplicationTp applicationTp = objectFactory.createCourseApplicationTp();
        applicationTp.setStartMonth(new DateTime(programDetails.getStartDate()));
        applicationTp.setAgreedSupervisorName(buildAgreedSupervisorName(applicationExportDTO.getPrimarySupervisor()));
        applicationTp.setPersonalStatement(exportReferToDocument);
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(application, applicationTp));
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setExternalApplicationID(application.getCode());

        String creatorIpAddress = applicationExportDTO.getCreatorIpAddress();
        applicationTp.setIpAddress(creatorIpAddress == null ? exportIpNotProvided : creatorIpAddress);
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee(applicationExportDTO.getApplicationReferees()));

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

        applicationTp.setProgramme(buildProgrammeOccurence(applicationExportDTO));

        Comment offerRecommendationComment = applicationExportDTO.getOfferRecommendationComment();
        if (offerRecommendationComment != null) {
            applicationTp.setAtasStatement(offerRecommendationComment.getPositionDescription());

            String conditions = offerRecommendationComment.getAppointmentConditions();
            String offerSummary = "Recommended conditions: " + conditions == null ? "none" : conditions + "\n\n" + "Preferred Start Date: "
                    + offerRecommendationComment.getPositionProvisionalStartDate().toString("d MMMM yyyy");

            applicationTp.setDepartmentalOfferConditions(offerSummary);
        }

        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence(ApplicationExportDTO applicationExportDTO) {
        Application application = applicationExportDTO.getApplication();

        Program program = application.getProgram();
        ProgrammeOccurrenceTp occurrenceTp = objectFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance(application));

        ProgramInstance exportProgramInstance = applicationExportDTO.getExportProgramInstance();
        occurrenceTp.setAcademicYear(applicationExportBuilderHelper.buildXmlDateYearOnly(exportProgramInstance.getAcademicYear()));
        String exportInstanceIdentifier = exportProgramInstance.getIdentifier();
        occurrenceTp.setIdentifier(exportInstanceIdentifier == null ? exportNotProvided : exportInstanceIdentifier);
        occurrenceTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationDeadline()));
        return occurrenceTp;
    }

    private ModeofattendanceTp buildModeofattendance(Application application) {
        ApplicationProgramDetails programmeDetails = application.getProgramDetails();
        ModeofattendanceTp modeofattendanceTp = objectFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOption().getCode());
        modeofattendanceTp.setName(programmeDetails.getStudyOption().getName());
        return modeofattendanceTp;
    }

    private NameTp buildAgreedSupervisorName(User primarySupervisor) {
        if (primarySupervisor != null) {
            NameTp nameTp = objectFactory.createNameTp();
            nameTp.setForename1(primarySupervisor.getFirstName());
            nameTp.setSurname(primarySupervisor.getLastName());
            return nameTp;
        }
        return null;
    }

    private SourceOfInterestTp buildSourcesOfInterest(Application application, CourseApplicationTp applicationTp) {
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

    private QualificationDetailsTp buildQualificationDetails(Application application) {
        QualificationDetailsTp resultList = objectFactory.createQualificationDetailsTp();

        Set<ApplicationQualification> qualifications = application.getQualifications();
        if (!qualifications.isEmpty()) {
            for (ApplicationQualification qualification : qualifications) {
                QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();

                qualificationsTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(qualification.getStartDate()));
                qualificationsTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(qualification.getAwardDate()));

                qualificationsTp.setGrade(qualification.getGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getLanguage());
                qualificationsTp.setMainSubject(qualification.getSubject());

                QualificationTp qualificationTp = objectFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getType().getCode());
                qualificationTp.setName(qualification.getType().getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = objectFactory.createInstitutionTp();

                String institutionCode = qualification.getInstitution().getCode();
                institutionTp.setCode(institutionCode.startsWith("CUST") ? exportOtherProvided : institutionCode);
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
            qualificationsTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().minusYears(1)));
            qualificationsTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().plusYears(1)));

            qualificationsTp.setGrade(exportNotProvided);
            qualificationsTp.setLanguageOfInstruction(exportNotProvided);
            qualificationsTp.setMainSubject(exportNotProvided);

            QualificationTp qualificationTp = objectFactory.createQualificationTp();
            qualificationTp.setCode("6");
            qualificationTp.setName("Other examinations and/or information");
            qualificationsTp.setQualification(qualificationTp);

            InstitutionTp institutionTp = objectFactory.createInstitutionTp();
            institutionTp.setCode(exportOtherProvided);
            institutionTp.setName(exportNotProvided);
            CountryTp countryTp = objectFactory.createCountryTp();
            countryTp.setCode("XK");
            countryTp.setName("United Kingdom");
            institutionTp.setCountry(countryTp);

            qualificationsTp.setInstitution(institutionTp);
            resultList.getQualificationDetail().add(qualificationsTp);
        }
        return resultList;
    }

    private EmploymentDetailsTp buildEmployer(Application application) {
        EmploymentDetailsTp resultList = objectFactory.createEmploymentDetailsTp();
        Set<ApplicationEmploymentPosition> employmentPositions = application.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (ApplicationEmploymentPosition employmentPosition : employmentPositions) {
                AppointmentTp appointmentTp = objectFactory.createAppointmentTp();

                appointmentTp.setJobTitle(employmentPosition.getPosition());
                appointmentTp.setResponsibilities(applicationExportBuilderHelper.cleanString(employmentPosition.getRemit()));
                appointmentTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(employmentPosition.getStartDate()));
                appointmentTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(employmentPosition.getEndDate()));

                EmployerTp employerTp = objectFactory.createEmployerTp();
                employerTp.setName(employmentPosition.getEmployerName());
                appointmentTp.setEmployer(employerTp);

                resultList.getEmployer().add(appointmentTp);
            }
        }
        return resultList;
    }

    private RefereeListTp buildReferee(List<ApplicationReferee> exportReferees) {
        RefereeListTp resultList = objectFactory.createRefereeListTp();
        for (int i = 0; i < 2; i++) {
            ApplicationReferee referee = exportReferees.get(i);
            RefereeTp refereeTp = objectFactory.createRefereeTp();
            refereeTp.setPosition(referee.getJobTitle());
            NameTp nameTp = objectFactory.createNameTp();
            nameTp.setForename1(referee.getUser().getFirstName());
            nameTp.setSurname(referee.getUser().getLastName());
            refereeTp.setName(nameTp);

            ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
            contactDtlsTp.setEmail(referee.getUser().getEmail());
            contactDtlsTp.setLandline(applicationExportBuilderHelper.cleanPhoneNumber(referee.getPhoneNumber()));

            if (StringUtils.isBlank(referee.getPhoneNumber())) {
                contactDtlsTp.setLandline(exportTelephoneNotProvided);
            } else if (!ESAPI.validator().isValidInput("PhoneNumber", referee.getPhoneNumber(), "PhoneNumber", 25, false)) {
                contactDtlsTp.setLandline(exportTelephoneNotProvided);
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

    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = objectFactory.createEnglishLanguageQualificationDetailsTp();

        if (personalDetails.getLanguageQualificationAvailable()) {
            ApplicationLanguageQualification languageQualification = personalDetails.getLanguageQualification();
            EnglishLanguageTp englishLanguageTp = objectFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(applicationExportBuilderHelper.buildXmlDate(languageQualification.getExamDate()));

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

}
