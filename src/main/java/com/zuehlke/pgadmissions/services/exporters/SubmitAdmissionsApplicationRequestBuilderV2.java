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

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ApplicantTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.DomicileTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EmploymentDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EnglishLanguageQualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.LanguageBandScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationsinEnglishTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.RefereeListTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class SubmitAdmissionsApplicationRequestBuilderV2 {

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

    public SubmitAdmissionsApplicationRequestBuilderV2(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }

    public SubmitAdmissionsApplicationRequestBuilderV2() {
        this(null);
    }

    public SubmitAdmissionsApplicationRequestBuilderV2 applicationForm(final Application applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }

    public SubmitAdmissionsApplicationRequestBuilderV2 isOverseasStudent(Boolean isOverseasStudent) {
        this.isOverseasStudent = isOverseasStudent;
        return this;
    }

    public SubmitAdmissionsApplicationRequestBuilderV2 primarySupervisor(User primarySupervisor) {
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
        applicant.setSex(buildSex());
        applicant.setDateOfBirth(buildDateOfBirth());
        applicant.setNationality(buildFirstNationality());
        applicant.setSecondaryNationality(buildSecondNationality());
        applicant.setCountryOfBirth(buildCountry());
        applicant.setCountryOfDomicile(buildDomicile());
        applicant.setVisaRequired(BooleanUtils.toBoolean(applicationForm.getPersonalDetails().getRequiresVisa()));
        if (BooleanUtils.isTrue(applicationForm.getPersonalDetails().getRequiresVisa())) {
            applicant.setPassport(buildPassport());
        }
        applicant.setDisability(buildDisability());
        applicant.setEthnicity(buildEthnicity());
        applicant.setHomeAddress(buildHomeAddress());
        applicant.setCorrespondenceAddress(buildCorrespondenceAddress());
        applicant.setCriminalConvictionDetails(cleanString(applicationForm.getAdditionalInformation().getConvictionsText()));
        applicant.setCriminalConvictions(applicationForm.getAdditionalInformation().getConvictions());
        applicant.setQualificationList(buildQualificationDetails());
        applicant.setEmployerList(buildEmployer());

        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(applicationForm.getPersonalDetails().getEnglishFirstLanguage()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification());

        if (BooleanUtils.isNotTrue(applicationForm.getPersonalDetails().getEnglishFirstLanguage())
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
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        User applicant = applicationForm.getUser();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
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

    private NationalityTp buildFirstNationality() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
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
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        CountryTp countryTp = xmlFactory.createCountryTp();
        countryTp.setCode(personalDetails.getCountry().getCode());
        countryTp.setName(personalDetails.getCountry().getName());
        return countryTp;
    }

    private PassportTp buildPassport() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
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
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        DisabilityTp disabilityTp = xmlFactory.createDisabilityTp();
        disabilityTp.setCode(personalDetails.getDisability().getCode());
        disabilityTp.setName(personalDetails.getDisability().getName());
        return disabilityTp;
    }

    private EthnicityTp buildEthnicity() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EthnicityTp ethnicityTp = xmlFactory.createEthnicityTp();
        ethnicityTp.setCode(personalDetails.getEthnicity().getCode());
        ethnicityTp.setName(personalDetails.getEthnicity().getName());
        return ethnicityTp;
    }

    private ContactDtlsTp buildHomeAddress() {
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();

        AddressTp addressTp = xmlFactory.createAddressTp();
        Address currentAddress = applicationForm.getApplicationAddress().getCurrentAddress();
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
        Address contactAddress = applicationForm.getApplicationAddress().getContactAddress();
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
        ProgramDetails programmeDetails = applicationForm.getProgramDetails();
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
        applicationTp.setExternalApplicationID(applicationForm.getApplicationNumber());

        // FIXME set ip address
        // applicationTp.setIpAddress(applicationForm.getIpAddressAsString());
        // else
        // applicationTp.setIpAddress(IP_ADDRESS_NOT_PROVIDED_VALUE);

        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee());

        switch (applicationForm.getState().getId()) {
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
        OfferRecommendedComment offerRecommendedComment = null; // applicationForm.getOfferRecommendedComment();
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
        ProgramDetails programmeDetails = applicationForm.getProgramDetails();
        ProgrammeOccurrenceTp occurrenceTp = xmlFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance());

        ProgramInstance activeInstance = null;
        for (ProgramInstance instance : program.getInstances()) {
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
        ProgramDetails programmeDetails = applicationForm.getProgramDetails();
        ModeofattendanceTp modeofattendanceTp = xmlFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOption().getId());
        modeofattendanceTp.setName(programmeDetails.getStudyOption().getDisplayName());
        return modeofattendanceTp;
    }

    private NameTp buildProposedSupervisorName(int idx) {
        ProgramDetails programmeDetails = applicationForm.getProgramDetails();
        NameTp nameTp = xmlFactory.createNameTp();
        List<SuggestedSupervisor> suggestedSupervisors = programmeDetails.getSuggestedSupervisors();

        if (idx < suggestedSupervisors.size()) {
            SuggestedSupervisor suggestedSupervisor = suggestedSupervisors.get(idx);
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
        ProgramDetails programmeDetails = applicationForm.getProgramDetails();
        SourceOfInterestTp interestTp = xmlFactory.createSourceOfInterestTp();
        SourcesOfInterest sourcesOfInterest = programmeDetails.getSourceOfInterest();
        if (sourcesOfInterest == null) {
            return null;
        }
        interestTp.setCode(sourcesOfInterest.getCode());
        interestTp.setName(sourcesOfInterest.getName());
        if (sourcesOfInterest.isFreeText()) {
            applicationTp.setOtherSourceofInterest(programmeDetails.getSourceOfInterestText());
        }
        return interestTp;
    }

    private QualificationDetailsTp buildQualificationDetails() {
        QualificationDetailsTp resultList = xmlFactory.createQualificationDetailsTp();

        List<Qualification> qualifications = applicationForm.getQualifications();
        if (!qualifications.isEmpty()) {
            for (Qualification qualification : qualifications) {
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
        List<EmploymentPosition> employmentPositions = applicationForm.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (EmploymentPosition employmentPosition : employmentPositions) {
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
        List<Referee> referees = null; // applicationForm.getRefereesToSendToPortico();
        for (Referee referee : referees) {
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
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = xmlFactory.createEnglishLanguageQualificationDetailsTp();

        if (personalDetails.getLanguageQualificationAvailable()) {
            LanguageQualification languageQualification = personalDetails.getLanguageQualification();
            EnglishLanguageTp englishLanguageTp = xmlFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(buildXmlDate(languageQualification.getExamDate()));

            if (languageQualification.getQualificationType() == LanguageQualificationEnum.OTHER) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.OTHER);
                englishLanguageTp.setOtherLanguageExam(languageQualification.getQualificationTypeOther());
            } else if (languageQualification.getQualificationType() == LanguageQualificationEnum.TOEFL) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.TOEFL);
                if (languageQualification.getExamOnline()) {
                    englishLanguageTp.setMethod("TOEFL_INTERNET");
                } else {
                    englishLanguageTp.setMethod("TOEFL_PAPER");
                }
            } else if (languageQualification.getQualificationType() == LanguageQualificationEnum.IELTS_ACADEMIC) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.IELTS);
            } else {
                throw new IllegalArgumentException(String.format("QualificationType type [%s] could not be converted",
                        languageQualification.getQualificationType()));
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
