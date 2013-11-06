package com.zuehlke.pgadmissions.services.exporters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.owasp.esapi.ESAPI;

import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.AddressTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ApplicantTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.AppointmentTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ContactDtlsTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.CountryTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.DisabilityTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.DomicileTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EmployerTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EmploymentDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EnglishLanguageQualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.EthnicityTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.GenderTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.InstitutionTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.LanguageBandScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ModeofattendanceTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.NameTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.NationalityTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.PassportTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ProgrammeOccurrenceTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.QualificationDetailsTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.QualificationTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.QualificationsinEnglishTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.RefereeListTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.RefereeTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.SourceOfInterestTp;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class SubmitAdmissionsApplicationRequestBuilder {

    private static final String IP_ADDRESS_NOT_PROVIDED_VALUE = "127.0.0.1";

    private static final String NOT_PROVIDED_VALUE = "NOT PROVIDED";

    private static final String ADDRESS_LINE_EMPTY_VALUE = "-";

    private static final String INSTITUTION_OTHER_CODE = "OTHER";

    private static final String REFER_TO_ATTACHED_DOCUMENT = "Refer to attached document.";

    private static final String SOURCE_IDENTIFIER = "PRISM";

    private static final String PHONE_NUMBER_NOT_PROVIDED_VALUE = "+44 (0) 0000 000 000";

    private static final String LANGUAGE_QUALIFICATION_ADMISSIONS_NOTE = "Application predates mandatory language qualification. Please check qualifications for potential language certificates.";

    private final ObjectFactory xmlFactory;

    protected final DatatypeFactory datatypeFactory;

    private ApplicationForm applicationForm;

    private boolean printLanguageQualificationAdmissionsNote = false;

    private Boolean isOverseasStudent;

    private static class NoActiveProgrameInstanceFoundException extends RuntimeException {
        private final ProgrammeOccurrenceTp occurrenceTp;
        private static final long serialVersionUID = 8359986556018188704L;

        public NoActiveProgrameInstanceFoundException(ProgrammeOccurrenceTp occurrenceTp, String message) {
            super(message);
            this.occurrenceTp = occurrenceTp;
        }

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

        public ProgrammeOccurrenceTp getOccurrenceTp() {
            return occurrenceTp;
        }
    }

    public SubmitAdmissionsApplicationRequestBuilder(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
    }

    public SubmitAdmissionsApplicationRequestBuilder() {
        this(null);
    }

    public SubmitAdmissionsApplicationRequestBuilder applicationForm(final ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }

    public SubmitAdmissionsApplicationRequestBuilder isOverseasStudent(Boolean isOverseasStudent) {
        this.isOverseasStudent = isOverseasStudent;
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

        applicant.setApplicantID(StringUtils.trimToNull(applicationForm.getApplicant().getUclUserId()));

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
        RegisteredUser applicant = applicationForm.getApplicant();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
        // Workaround until a new web service has been released by UCL.
        switch (personalDetails.getGender()) {
        case FEMALE:
        case INDETERMINATE_GENDER:
            nameTp.setTitle(Title.MRS.getDisplayValue());
            break;
        default:
            nameTp.setTitle(Title.MR.getDisplayValue());
            break;
        }
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
        PassportInformation passportInformation = personalDetails.getPassportInformation();
        if (passportInformation != null) {
            PassportTp passportTp = xmlFactory.createPassportTp();
            passportTp.setName(passportInformation.getNameOnPassport());
            passportTp.setNumber(passportInformation.getPassportNumber());
            passportTp.setExpiryDate(buildXmlDate(passportInformation.getPassportExpiryDate()));
            passportTp.setIssueDate(buildXmlDate(passportInformation.getPassportIssueDate()));
            return passportTp;
        } else {
            PassportTp passportTp = xmlFactory.createPassportTp();
            passportTp.setName(NOT_PROVIDED_VALUE);
            passportTp.setNumber(NOT_PROVIDED_VALUE);
            passportTp.setExpiryDate(buildXmlDate(DateUtils.addYears(new Date(), 1)));
            passportTp.setIssueDate(buildXmlDate(DateUtils.addYears(new Date(), -1)));
            return passportTp;
        }
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
        addressTp.setPostCode(currentAddress.getAddress5());
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
        contactDtlsTp.setEmail(applicationForm.getApplicant().getEmail());
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
        addressTp.setPostCode(contactAddress.getAddress5());
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
        contactDtlsTp.setEmail(applicationForm.getApplicant().getEmail());
        contactDtlsTp.setLandline(cleanPhoneNumber(applicationForm.getPersonalDetails().getPhoneNumber()));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication() {
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        CourseApplicationTp applicationTp = xmlFactory.createCourseApplicationTp();
        applicationTp.setStartMonth(new DateTime(programmeDetails.getStartDate()));

        NameTp supervisorName = buildAgreedSupervisorName();
        if (supervisorName == null && !programmeDetails.getSuggestedSupervisors().isEmpty()) {
            // Which supervisor to pick if there are multiple
            // Just send the first one. Confirmed by Alastair Knowles
            supervisorName = buildSuggestedSupervisorName(0);
        }
        applicationTp.setSupervisorName(supervisorName);
        applicationTp.setPersonalStatement(REFER_TO_ATTACHED_DOCUMENT);

        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationTp));
        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setIpAddress(applicationForm.getIpAddressAsString());
        applicationTp.setExternalApplicationID(applicationForm.getApplication().getApplicationNumber());

        if (StringUtils.isBlank(applicationForm.getIpAddressAsString())) {
            applicationTp.setIpAddress(IP_ADDRESS_NOT_PROVIDED_VALUE);
        }

        applicationTp.setCreationDate(buildXmlDate(applicationForm.getSubmittedDate()));
        applicationTp.setRefereeList(buildReferee());

        switch (applicationForm.getStatus()) {
        case WITHDRAWN:
            applicationTp.setApplicationStatus("WITHDRAWN");
            break;
        case APPROVED:
            applicationTp.setApplicationStatus("ACTIVE");
            applicationTp.setDepartmentalDecision("OFFER");
            break;
        case REJECTED:
            applicationTp.setApplicationStatus("ACTIVE");
            applicationTp.setDepartmentalDecision("REJECT");
            break;
        default:
            throw new IllegalArgumentException("Application is in wrong state " + applicationForm.getStatus().displayValue());
        }

        try {
            applicationTp.setProgramme(buildProgrammeOccurence());
        } catch (NoActiveProgrameInstanceFoundException exp) {
            throw new IllegalArgumentException(exp.getMessage(), exp);
        } catch (NoIdentifierForProgrameInstanceFoundException exp) {
            throw new IllegalArgumentException(exp.getMessage(), exp);
        }

        if (printLanguageQualificationAdmissionsNote && applicationForm.getStatus() == ApplicationFormStatus.APPROVED) {
            applicationTp.setDepartmentalOfferConditions(LANGUAGE_QUALIFICATION_ADMISSIONS_NOTE);
        }

        ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();

        if (latestApprovalRound != null) {
            if (isOverseasStudent && BooleanUtils.isTrue(applicationForm.getProgram().getAtasRequired())) {
                applicationTp.setAtasStatement(latestApprovalRound.getProjectAbstract());
            }

            if (applicationForm.getStatus() == ApplicationFormStatus.APPROVED) {
                if (BooleanUtils.isTrue(latestApprovalRound.getRecommendedConditionsAvailable())) {
                    applicationTp.setDepartmentalOfferConditions("Conditional Offer: " + latestApprovalRound.getRecommendedConditions());
                } else {
                    applicationTp.setDepartmentalOfferConditions("Unconditional Offer");
                }
            }
        }

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
            occurrenceTp.setAcademicYear(buildXmlDateYearOnly(programmeDetails.getStartDate()));
            occurrenceTp.setIdentifier(NOT_PROVIDED_VALUE);
            occurrenceTp.setStartDate(buildXmlDate(programmeDetails.getStartDate()));
            occurrenceTp.setEndDate(buildXmlDate(DateUtils.addYears(programmeDetails.getStartDate(), 1)));
            throw new NoActiveProgrameInstanceFoundException(occurrenceTp, String.format(
                    "No active program found for Program[code=%s], ProgrammeDetails[studyOption=%s]", program.getCode(), programmeDetails.getStudyOption()));
        }

        occurrenceTp.setAcademicYear(buildXmlDateYearOnly(activeInstance.getAcademic_year()));
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
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        ModeofattendanceTp modeofattendanceTp = xmlFactory.createModeofattendanceTp();
        modeofattendanceTp.setCode(programmeDetails.getStudyOptionCode());
        modeofattendanceTp.setName(programmeDetails.getStudyOption());
        return modeofattendanceTp;
    }

    private NameTp buildSuggestedSupervisorName(int idx) {
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

                // TODO: This might be null because we've changed this to a mandatory field in mid flight.
                // Talk to Alastair about this when we go live!
                // Sending a null value will be rejected by the web service.
                qualificationsTp.setEndDate(buildXmlDate(qualification.getQualificationAwardDate()));

                qualificationsTp.setGrade(qualification.getQualificationGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getQualificationLanguage());
                qualificationsTp.setMainSubject(qualification.getQualificationSubject());

                QualificationTp qualificationTp = xmlFactory.createQualificationTp();
                qualificationTp.setCode(qualification.getQualificationType().getCode());
                qualificationTp.setName(qualification.getQualificationType().getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = xmlFactory.createInstitutionTp();

                institutionTp.setCode(qualification.getQualificationInstitutionCode());
                institutionTp.setName(qualification.getQualificationInstitution());

                CountryTp countryTp = xmlFactory.createCountryTp();
                countryTp.setCode(qualification.getInstitutionCountry().getCode());
                countryTp.setName(qualification.getInstitutionCountry().getName());
                institutionTp.setCountry(countryTp);

                qualificationsTp.setInstitution(institutionTp);
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        } else {
            QualificationsTp qualificationsTp = xmlFactory.createQualificationsTp();
            qualificationsTp.setStartDate(buildXmlDate(DateUtils.addYears(new Date(), -1)));
            qualificationsTp.setEndDate(buildXmlDate(DateUtils.addYears(new Date(), 1)));

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
        for (Referee referee : applicationForm.getRefereesToSendToPortico()) {
            RefereeTp refereeTp = xmlFactory.createRefereeTp();

            refereeTp.setPosition(referee.getJobTitle());

            NameTp nameTp = xmlFactory.createNameTp();
            nameTp.setForename1(referee.getFirstname());
            nameTp.setSurname(referee.getLastname());
            refereeTp.setName(nameTp);

            ContactDtlsTp contactDtlsTp = xmlFactory.createContactDtlsTp();
            contactDtlsTp.setEmail(referee.getEmail());
            contactDtlsTp.setLandline(cleanPhoneNumber(referee.getPhoneNumber()));

            if (StringUtils.isBlank(referee.getPhoneNumber())) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            } else if (!ESAPI.validator().isValidInput("PhoneNumber", referee.getPhoneNumber(), "PhoneNumber", 25, false)) {
                contactDtlsTp.setLandline(PHONE_NUMBER_NOT_PROVIDED_VALUE);
            }

            AddressTp addressTp = xmlFactory.createAddressTp();
            addressTp.setAddressLine1(referee.getAddressLocation().getAddress1());
            addressTp.setAddressLine2(referee.getAddressLocation().getAddress2());
            addressTp.setAddressLine3(referee.getAddressLocation().getAddress3());
            addressTp.setAddressLine4(referee.getAddressLocation().getAddress4());
            addressTp.setPostCode(referee.getAddressLocation().getAddress5());
            addressTp.setCountry(referee.getAddressLocation().getDomicile().getCode());

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
                throw new IllegalArgumentException(String.format("QualificationType type [%s] could not be converted",
                        languageQualifications.getQualificationType()));
            }

            // The web service does not allow scores in the format "6.0" it only accepts "6" and the like.
            EnglishLanguageScoreTp overallScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            overallScoreTp.setName(LanguageBandScoreTp.OVERALL);
            overallScoreTp.setScore(languageQualifications.getOverallScore().replace(".0", ""));

            EnglishLanguageScoreTp readingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            readingScoreTp.setName(LanguageBandScoreTp.READING);
            readingScoreTp.setScore(languageQualifications.getReadingScore().replace(".0", ""));

            EnglishLanguageScoreTp writingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            writingScoreTp.setName(LanguageBandScoreTp.WRITING);
            writingScoreTp.setScore(languageQualifications.getWritingScore().replace(".0", ""));

            EnglishLanguageScoreTp essayOrSpeakingScoreTp = null;
            if (StringUtils.equalsIgnoreCase("TOEFL_PAPER", englishLanguageTp.getMethod())) {
                essayOrSpeakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.ESSAY);
                essayOrSpeakingScoreTp.setScore(languageQualifications.getWritingScore().replace(".0", ""));
            } else {
                essayOrSpeakingScoreTp = xmlFactory.createEnglishLanguageScoreTp();
                essayOrSpeakingScoreTp.setName(LanguageBandScoreTp.SPEAKING);
                essayOrSpeakingScoreTp.setScore(languageQualifications.getSpeakingScore().replace(".0", ""));
            }

            EnglishLanguageScoreTp listeningScoreTp = xmlFactory.createEnglishLanguageScoreTp();
            listeningScoreTp.setName(LanguageBandScoreTp.LISTENING);
            listeningScoreTp.setScore(languageQualifications.getListeningScore().replace(".0", ""));

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
    
    private NameTp buildAgreedSupervisorName() {
        if (applicationForm.getLatestApprovalRound() != null) {
            ApprovalRound approvalRound = applicationForm.getLatestApprovalRound();
            Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
            if (primarySupervisor != null) {
                NameTp nameTp = xmlFactory.createNameTp();
                nameTp.setForename1(primarySupervisor.getUser().getFirstName());
                nameTp.setSurname(primarySupervisor.getUser().getLastName());
                return nameTp;
            }
        }
        return null;
    }

    protected XMLGregorianCalendar buildXmlDate(Date date) {
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return datatypeFactory.newXMLGregorianCalendar(gc);
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

    protected XMLGregorianCalendar buildXmlDateYearOnly(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(cal.get(Calendar.YEAR));
            return xmlCalendar;
        }
        return null;
    }

}
