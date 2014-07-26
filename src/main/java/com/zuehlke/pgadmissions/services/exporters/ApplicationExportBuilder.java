package com.zuehlke.pgadmissions.services.exporters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.owasp.esapi.ESAPI;

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
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ApplicationExportBuilder {

    private static final String NOT_PROVIDED_VALUE = "NOT PROVIDED";

    private static final String ADDRESS_LINE_EMPTY_VALUE = "-";

    private static final String INSTITUTION_OTHER_CODE = "OTHER";

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private static final String SOURCE_IDENTIFIER = "PRISM";

    private static final String PHONE_NUMBER_NOT_PROVIDED_VALUE = "+44 (0) 0000 000 000";

    private static final String LANGUAGE_QUALIFICATION_ADMISSIONS_NOTE = "Application predates mandatory language qualification. Please check qualifications for potential language certificates.";

    private final ObjectFactory xmlFactory;

    protected final DatatypeFactory datatypeFactory;

    private Application applicationForm;

    private boolean printLanguageQualificationAdmissionsNote = false;

    private Boolean isOverseasStudent;

    private User primarySupervisor;

    private static class NoActiveProgrameInstanceFoundException extends RuntimeException {
        private final ProgrammeOccurrenceTp occurrenceTp;
        private static final long serialVersionUID = 8359986556018188704L;

        public NoActiveProgrameInstanceFoundException(ProgrammeOccurrenceTp occurrenceTp, String message) {
            super(message);
            this.occurrenceTp = occurrenceTp;
        }

        @SuppressWarnings("unused")
        public ProgrammeOccurrenceTp getOccurrenceTp() {
            return occurrenceTp;
        }
    }

    private static class NoIdentifierForProgrameInstanceFoundException extends RuntimeException {
        private static final long serialVersionUID = 1820912139538020762L;
        private final ProgrammeOccurrenceTp occurrenceTp;

        public NoIdentifierForProgrameInstanceFoundException(ProgrammeOccurrenceTp occurrenceTp, String message) {
            super(message);
            this.occurrenceTp = occurrenceTp;
        }

        @SuppressWarnings("unused")
        public ProgrammeOccurrenceTp getOccurrenceTp() {
            return occurrenceTp;
        }
    }

    public ApplicationExportBuilder(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }

    public ApplicationExportBuilder() {
        this(null);
    }

    public ApplicationExportBuilder applicationForm(final Application applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }

    public ApplicationExportBuilder isOverseasStudent(Boolean isOverseasStudent) {
        this.isOverseasStudent = isOverseasStudent;
        return this;
    }

    public ApplicationExportBuilder primarySupervisor(User primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
        return this;
    }

    public SubmitAdmissionsApplicationRequest build() {
        printLanguageQualificationAdmissionsNote = false;
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
        applicant.setSex(buildGender());
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildFirstNationality());
        applicant.setSecondaryNationality(buildSecondNationality());
        applicant.setCountryOfBirth(buildCountry());
        applicant.setCountryOfDomicile(buildDomicile());
        applicant.setVisaRequired(BooleanUtils.toBoolean(applicationForm.getPersonalDetails().getVisaRequired()));
        if (BooleanUtils.isTrue(applicationForm.getPersonalDetails().getVisaRequired())) {
            applicant.setPassport(buildPassport());
        }
        applicant.setDisability(buildDisability());
        applicant.setEthnicity(buildEthnicity());
        applicant.setHomeAddress(buildHomeAddress());
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress());
        applicant.setCriminalConvictionDetails(cleanString(applicationForm.getAdditionalInformation().getConvictionsText()));
        applicant.setCriminalConvictions(applicationForm.getAdditionalInformation().getHasConvictions());
        applicant.setQualificationList(buildQualificationDetails());
        applicant.setEmployerList(buildEmployer());

        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(applicationForm.getPersonalDetails().getFirstLanguageEnglish()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification());

        if (BooleanUtils.isNotTrue(applicationForm.getPersonalDetails().getFirstLanguageEnglish())
                && BooleanUtils.isNotTrue(applicationForm.getPersonalDetails().getLanguageQualificationAvailable())) {
            printLanguageQualificationAdmissionsNote = true;
        }

        // FIXME set applicant UCL ID
        // applicant.setApplicantID(StringUtils.trimToNull(applicationForm.getApplicant().getUclUserId()));

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
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        User applicant = applicationForm.getUser();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
        nameTp.setTitle(personalDetails.getTitle().getName());
        return nameTp;
    }

    private GenderTp buildGender() {
        Gender gender = applicationForm.getPersonalDetails().getGender();
        return GenderTp.valueOf(gender.getCode());
    }

    private XMLGregorianCalendar buildDateOfBirth() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        return buildXmlDate(personalDetails.getDateOfBirth());
    }

    private NationalityTp buildFirstNationality() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        Language firstNationality = personalDetails.getFirstNationality();

        NationalityTp nationalityTp = xmlFactory.createNationalityTp();
        if (firstNationality == null) {
            throw new IllegalArgumentException("Candidate should have at least one nationality.");
        }

        nationalityTp.setCode(firstNationality.getCode());
        nationalityTp.setName(firstNationality.getName());
        return nationalityTp;
    }

    private NationalityTp buildSecondNationality() {
        Language secondNationality = applicationForm.getPersonalDetails().getSecondNationality();

        NationalityTp nationalityTp = xmlFactory.createNationalityTp();
        if (secondNationality == null) {
            return null;
        } else {
            nationalityTp.setCode(secondNationality.getCode());
            nationalityTp.setName(secondNationality.getName());
            return nationalityTp;
        }
    }

    private CountryTp buildCountry() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        CountryTp countryTp = xmlFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        countryTp.setName(personalDetails.getCountry().getName());
        return countryTp;
    }

    private PassportTp buildPassport() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        if (personalDetails.getPassportAvailable()) {
            PassportTp passportTp = xmlFactory.createPassportTp();
            passportTp.setName(personalDetails.getPassport().getName());
            passportTp.setNumber(personalDetails.getPassport().getNumber());
            passportTp.setExpiryDate(buildXmlDate(personalDetails.getPassport().getExpiryDate()));
            passportTp.setIssueDate(buildXmlDate(personalDetails.getPassport().getIssueDate()));
            return passportTp;
        } else {
            PassportTp passportTp = xmlFactory.createPassportTp();
            passportTp.setName(NOT_PROVIDED_VALUE);
            passportTp.setNumber(NOT_PROVIDED_VALUE);
            passportTp.setExpiryDate(buildXmlDate(new LocalDate().plusYears(1)));
            passportTp.setIssueDate(buildXmlDate(new LocalDate().minusYears(1)));
            return passportTp;
        }
    }

    private DisabilityTp buildDisability() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        DisabilityTp disabilityTp = xmlFactory.createDisabilityTp();
        disabilityTp.setCode(personalDetails.getDisability().getCode());
        disabilityTp.setName(personalDetails.getDisability().getName());
        return disabilityTp;
    }

    private EthnicityTp buildEthnicity() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EthnicityTp ethnicityTp = xmlFactory.createEthnicityTp();
        ethnicityTp.setCode(personalDetails.getEthnicity().getCode());
        ethnicityTp.setName(personalDetails.getEthnicity().getName());
        return ethnicityTp;
    }

    private ContactDtlsTp buildHomeAddress() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();

        AddressTp addressTp = xmlFactory.createAddressTp();
        Address currentAddress = applicationForm.getAddress().getCurrentAddress();
        addressTp.setAddressLine1(currentAddress.getAddressLine1());
        addressTp.setAddressLine2(currentAddress.getAddressLine2());
        addressTp.setAddressLine3(currentAddress.getAddressTown());
        addressTp.setAddressLine4(currentAddress.getAddressRegion());
        addressTp.setPostCode(currentAddress.getAddressCode());
        addressTp.setCountry(currentAddress.getDomicile().getCode());

        // postCode is mandatory but but PRISM did not collect addresses
        // in this format before.
        if (StringUtils.isBlank(addressTp.getPostCode())) {
            addressTp.setPostCode(ADDRESS_LINE_EMPTY_VALUE);
        }

        // addressLine3 is mandatory but PRISM did not collect addresses
        // in this format before.
        if (StringUtils.isBlank(addressTp.getAddressLine3())) {
            addressTp.setAddressLine3(ADDRESS_LINE_EMPTY_VALUE);
        }

        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(applicationForm.getUser().getEmail());
        contactDtlsTp.setLandline(cleanPhoneNumber(personalDetails.getPhoneNumber()));
        return contactDtlsTp;
    }

    private ContactDtlsTp buildCorrespondenceAddress() {
        Address contactAddress = applicationForm.getAddress().getContactAddress();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
        AddressTp addressTp = xmlFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getAddressLine1());
        addressTp.setAddressLine2(contactAddress.getAddressLine2());
        addressTp.setAddressLine3(contactAddress.getAddressTown());
        addressTp.setAddressLine4(contactAddress.getAddressRegion());
        addressTp.setPostCode(contactAddress.getAddressCode());
        addressTp.setCountry(contactAddress.getDomicile().getCode());

        // postCode is mandatory but but PRISM did not collect addresses
        // in this format before.
        if (StringUtils.isBlank(addressTp.getPostCode())) {
            addressTp.setPostCode(ADDRESS_LINE_EMPTY_VALUE);
        }

        // addressLine3 is mandatory but PRISM did not collect addresses
        // in this format before.
        if (StringUtils.isBlank(addressTp.getAddressLine3())) {
            addressTp.setAddressLine3(ADDRESS_LINE_EMPTY_VALUE);
        }

        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(applicationForm.getUser().getEmail());
        contactDtlsTp.setLandline(cleanPhoneNumber(applicationForm.getPersonalDetails().getPhoneNumber()));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication() {
        ApplicationProgramDetails programmeDetails = applicationForm.getProgramDetails();
        CourseApplicationTp applicationTp = xmlFactory.createCourseApplicationTp();
        applicationTp.setStartMonth(new DateTime(programmeDetails.getStartDate()));
        if (!programmeDetails.getSuggestedSupervisors().isEmpty()) {
            // Which supervisor to pick if there are multiple
            // Just send the first one. Confirmed by Alastair Knowles
            applicationTp.setProposedSupervisorName(buildProposedSupervisorName(0));
        }
        applicationTp.setAgreedSupervisorName(buildAgreedSupervisorName());

        applicationTp.setPersonalStatement(REFER_TO_ATTACHED_DOCUMENT);

        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationTp));
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedTimestamp()));
        applicationTp.setExternalApplicationID(applicationForm.getCode());

        // FIXME set ip address
        // applicationTp.setIpAddress(applicationForm.getIpAddressAsString());
        // else
        // applicationTp.setIpAddress(IP_ADDRESS_NOT_PROVIDED_VALUE);

        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee());

        switch (applicationForm.getState().getId()) {
        case APPLICATION_WITHDRAWN_PENDING_EXPORT:
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
            throw new IllegalArgumentException("Application is in wrong state " + applicationForm.getState().getId().name());
        }

        try {
            applicationTp.setProgramme(buildProgrammeOccurence());
        } catch (NoActiveProgrameInstanceFoundException exp) {
            throw new IllegalArgumentException(exp.getMessage(), exp);
        } catch (NoIdentifierForProgrameInstanceFoundException exp) {
            throw new IllegalArgumentException(exp.getMessage(), exp);
        }

        if (printLanguageQualificationAdmissionsNote && applicationForm.getState().getId() == PrismState.APPLICATION_APPROVED) {
            applicationTp.setDepartmentalOfferConditions(LANGUAGE_QUALIFICATION_ADMISSIONS_NOTE);
        }

        // FIXME get offer recommended comment (this class should be Spring component so it can access CommentService)
        Comment offerRecommendedComment = null; // applicationForm.getOfferRecommendedComment();
        if (offerRecommendedComment != null) {
            if (isOverseasStudent && BooleanUtils.isTrue(applicationForm.getProgram().getRequireProjectDefinition())) {
                applicationTp.setAtasStatement(offerRecommendedComment.getPositionDescription());
            }
        }

        if (offerRecommendedComment != null && applicationForm.getState().getId() == PrismState.APPLICATION_APPROVED) {
            String departmentalOfferConditions = "Recommended Offer Type: ";
            if (BooleanUtils.isTrue(offerRecommendedComment.getAppointmentConditions() != null)) {
                departmentalOfferConditions += "Conditional\n\nRecommended Conditions: ";
                departmentalOfferConditions += offerRecommendedComment.getAppointmentConditions() + "\n\n";
            } else {
                departmentalOfferConditions += "Unconditional\n\n";
            }
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String provisionalStartDateString = outputDateFormat.format(offerRecommendedComment.getPositionProvisionalStartDate());
            departmentalOfferConditions += "Recommended Start Date: " + provisionalStartDateString;
            applicationTp.setDepartmentalOfferConditions(departmentalOfferConditions);
        }

        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence() {
        Program program = applicationForm.getProgram();
        ApplicationProgramDetails programmeDetails = applicationForm.getProgramDetails();
        ProgrammeOccurrenceTp occurrenceTp = xmlFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance());

        ProgramInstance activeInstance = null;
        for (ProgramInstance instance : program.getProgramInstances()) {
            if (!instance.getApplicationStartDate().isBefore(new LocalDate())) {
                if (instance.getStudyOption().getId().equals(programmeDetails.getId())) {
                    activeInstance = instance;
                    break;
                }
            }
        }

        if (activeInstance == null) {
            occurrenceTp.setAcademicYear(buildXmlDateYearOnly(programmeDetails.getStartDate()));
            occurrenceTp.setIdentifier(NOT_PROVIDED_VALUE);
            occurrenceTp.setStartDate(buildXmlDate(programmeDetails.getStartDate()));
            occurrenceTp.setEndDate(buildXmlDate(programmeDetails.getStartDate().plusYears(1)));
            throw new NoActiveProgrameInstanceFoundException(occurrenceTp, String.format(
                    "No active program found for Program[code=%s], ProgrammeDetails[studyOption=%s]", program.getCode(), programmeDetails.getStudyOption()));
        }

        occurrenceTp.setAcademicYear(buildXmlDateYearOnly(activeInstance.getAcademicYear()));
        occurrenceTp.setIdentifier(activeInstance.getIdentifier());
        occurrenceTp.setStartDate(buildXmlDate(activeInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(buildXmlDate(activeInstance.getApplicationDeadline()));

        if (StringUtils.isBlank(activeInstance.getIdentifier())) {
            occurrenceTp.setIdentifier(NOT_PROVIDED_VALUE);
            throw new NoIdentifierForProgrameInstanceFoundException(occurrenceTp, String.format("No identifier for program instance found. Program[code=%s]",
                    program.getCode()));
        }

        return occurrenceTp;
    }

    private ModeofattendanceTp buildModeofattendance() {
        ApplicationProgramDetails programmeDetails = applicationForm.getProgramDetails();
        ModeofattendanceTp modeofattendanceTp = xmlFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOption().getCode());
        modeofattendanceTp.setName(programmeDetails.getStudyOption().getName());
        return modeofattendanceTp;
    }

    private NameTp buildProposedSupervisorName(int idx) {
        ApplicationProgramDetails programmeDetails = applicationForm.getProgramDetails();
        NameTp nameTp = xmlFactory.createNameTp();
        List<ApplicationSupervisor> suggestedSupervisors = programmeDetails.getSuggestedSupervisors();

        if (idx < suggestedSupervisors.size()) {
            ApplicationSupervisor suggestedSupervisor = suggestedSupervisors.get(idx);
            nameTp.setForename1(suggestedSupervisor.getUser().getFirstName());
            nameTp.setSurname(suggestedSupervisor.getUser().getLastName());
            return nameTp;
        } else {
            return null;
        }
    }

    private NameTp buildAgreedSupervisorName() {
        if (primarySupervisor != null) {
            NameTp nameTp = xmlFactory.createNameTp();
            nameTp.setForename1(primarySupervisor.getFirstName());
            nameTp.setSurname(primarySupervisor.getLastName());
            return nameTp;
        }
        return null;
    }

    private SourceOfInterestTp buildSourcesOfInterest(CourseApplicationTp applicationTp) {
        ApplicationProgramDetails programmeDetails = applicationForm.getProgramDetails();
        SourceOfInterestTp interestTp = xmlFactory.createSourceOfInterestTp();
        ReferralSource sourcesOfInterest = programmeDetails.getReferralSource();
        if (sourcesOfInterest == null) {
            return null;
        }
        interestTp.setCode(sourcesOfInterest.getCode());
        interestTp.setName(sourcesOfInterest.getName());
        return interestTp;
    }

    private QualificationDetailsTp buildQualificationDetails() {
        QualificationDetailsTp resultList = xmlFactory.createQualificationDetailsTp();

        List<ApplicationQualification> qualifications = applicationForm.getQualifications();
        if (!qualifications.isEmpty()) {
            for (ApplicationQualification qualification : qualifications) {
                QualificationsTp qualificationsTp = xmlFactory.createQualificationsTp();

                qualificationsTp.setStartDate(buildXmlDate(qualification.getStartDate()));
                qualificationsTp.setEndDate(buildXmlDate(qualification.getAwardDate()));

                qualificationsTp.setGrade(qualification.getGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getLanguage());
                qualificationsTp.setMainSubject(qualification.getSubject());

                QualificationTp qualificationTp = xmlFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getType().getCode());
                qualificationTp.setName(qualification.getType().getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = xmlFactory.createInstitutionTp();

                institutionTp.setCode(qualification.getInstitution().getCode());
                institutionTp.setName(qualification.getInstitution().getName());

                CountryTp countryTp = xmlFactory.createCountryTp();
                countryTp.setCode(qualification.getInstitution().getDomicile().getCode());
                countryTp.setName(qualification.getInstitution().getDomicile().getName());
                institutionTp.setCountry(countryTp);

                qualificationsTp.setInstitution(institutionTp);
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        } else {
            QualificationsTp qualificationsTp = xmlFactory.createQualificationsTp();
            qualificationsTp.setStartDate(buildXmlDate(new LocalDate().minusYears(1)));
            qualificationsTp.setEndDate(buildXmlDate(new LocalDate().plusYears(1)));

            qualificationsTp.setGrade(NOT_PROVIDED_VALUE);
            qualificationsTp.setLanguageOfInstruction(NOT_PROVIDED_VALUE);
            qualificationsTp.setMainSubject(NOT_PROVIDED_VALUE);

            QualificationTp qualificationTp = xmlFactory.createQualificationTp();
            qualificationTp.setCode("6");
            qualificationTp.setName("Other examinations and/or information");
            qualificationsTp.setQualification(qualificationTp);

            InstitutionTp institutionTp = xmlFactory.createInstitutionTp();
            institutionTp.setCode(INSTITUTION_OTHER_CODE);
            institutionTp.setName(NOT_PROVIDED_VALUE);
            CountryTp countryTp = xmlFactory.createCountryTp();
            countryTp.setCode("XK");
            countryTp.setName("United Kingdom");
            institutionTp.setCountry(countryTp);

            qualificationsTp.setInstitution(institutionTp);
            resultList.getQualificationDetail().add(qualificationsTp);
        }
        return resultList;
    }

    private EmploymentDetailsTp buildEmployer() {
        EmploymentDetailsTp resultList = xmlFactory.createEmploymentDetailsTp();
        List<ApplicationEmploymentPosition> employmentPositions = applicationForm.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (ApplicationEmploymentPosition employmentPosition : employmentPositions) {
                AppointmentTp appointmentTp = xmlFactory.createAppointmentTp();

                appointmentTp.setJobTitle(employmentPosition.getPosition());
                appointmentTp.setResponsibilities(cleanString(employmentPosition.getRemit()));
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

        // FIXME get referees to send to portico (this class should be Spring component)
        List<ApplicationReferee> referees = null; // applicationForm.getRefereesToSendToPortico();
        for (ApplicationReferee referee : referees) {
            RefereeTp refereeTp = xmlFactory.createRefereeTp();

            refereeTp.setPosition(referee.getJobTitle());

            NameTp nameTp = xmlFactory.createNameTp();
            nameTp.setForename1(referee.getUser().getFirstName());
            nameTp.setSurname(referee.getUser().getLastName());
            refereeTp.setName(nameTp);

            ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
            contactDtlsTp.setEmail(referee.getUser().getEmail());
            contactDtlsTp.setLandline(cleanPhoneNumber(referee.getPhoneNumber()));

            if (StringUtils.isBlank(referee.getPhoneNumber())) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            } else if (!ESAPI.validator().isValidInput("PhoneNumber", referee.getPhoneNumber(), "PhoneNumber", 25, false)) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            }

            AddressTp addressTp = xmlFactory.createAddressTp();
            addressTp.setAddressLine1(referee.getAddress().getAddressLine1());
            addressTp.setAddressLine2(referee.getAddress().getAddressLine2());
            addressTp.setAddressLine3(referee.getAddress().getAddressTown());
            addressTp.setAddressLine4(referee.getAddress().getAddressRegion());
            addressTp.setPostCode(referee.getAddress().getAddressCode());
            addressTp.setCountry(referee.getAddress().getDomicile().getCode());

            // postCode is mandatory but but PRISM did not collect addresses
            // in this format before.
            if (StringUtils.isBlank(addressTp.getPostCode())) {
                addressTp.setPostCode(ADDRESS_LINE_EMPTY_VALUE);
            }

            // addressLine3 is mandatory but PRISM did not collect addresses
            // in this format before.
            if (StringUtils.isBlank(addressTp.getAddressLine3())) {
                addressTp.setAddressLine3(ADDRESS_LINE_EMPTY_VALUE);
            }

            contactDtlsTp.setAddressDtls(addressTp);

            refereeTp.setContactDetails(contactDtlsTp);

            resultList.getReferee().add(refereeTp);
        }
        return resultList;
    }

    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification() {
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = xmlFactory.createEnglishLanguageQualificationDetailsTp();

        if (personalDetails.getLanguageQualificationAvailable()) {
            ApplicationLanguageQualification languageQualification = personalDetails.getLanguageQualification();
            EnglishLanguageTp englishLanguageTp = xmlFactory.createEnglishLanguageTp();
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

            // The web service does not allow scores in the format "6.0" it only
            // accepts "6" and the like.
            EnglishLanguageScoreTp overallScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            overallScoreTp.setName(LanguageBandScoreTp.OVERALL);
            overallScoreTp.setScore(languageQualification.getOverallScore().replace(".0", ""));

            EnglishLanguageScoreTp readingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            readingScoreTp.setName(LanguageBandScoreTp.READING);
            readingScoreTp.setScore(languageQualification.getReadingScore().replace(".0", ""));

            EnglishLanguageScoreTp writingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            writingScoreTp.setName(LanguageBandScoreTp.WRITING);
            writingScoreTp.setScore(languageQualification.getWritingScore().replace(".0", ""));

            EnglishLanguageScoreTp essayOrSpeakingScoreTp = null;
            if (StringUtils.equalsIgnoreCase("TOEFL_PAPER", englishLanguageTp.getMethod())) {
                essayOrSpeakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.ESSAY);
                essayOrSpeakingScoreTp.setScore(languageQualification.getWritingScore().replace(".0", ""));
            } else {
                essayOrSpeakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.SPEAKING);
                essayOrSpeakingScoreTp.setScore(languageQualification.getSpeakingScore().replace(".0", ""));
            }

            EnglishLanguageScoreTp listeningScoreTp = xmlFactory.createEnglishLanguageScoreTp();
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

    protected XMLGregorianCalendar buildXmlDate(LocalDate localDate) {
        return buildXmlDate(localDate.toDateTimeAtStartOfDay());
    }

    protected XMLGregorianCalendar buildXmlDate(DateTime dateTime) {
        if (dateTime != null) {
            return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        }
        return null;
    }

    protected XMLGregorianCalendar buildXmlDateYearOnly(String date) {
        if (date != null) {
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(Integer.valueOf(date));
            return xmlCalendar;
        }
        return null;
    }

    protected XMLGregorianCalendar buildXmlDateYearOnly(LocalDate date) {
        if (date != null) {
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(date.getYear());
            return xmlCalendar;
        }
        return null;
    }

}
