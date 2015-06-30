package com.zuehlke.pgadmissions.services.builders;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PREFERRED_START_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_IP_PLACEHOLDER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NONE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OTHER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PHONE_MOCK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_REFER_TO_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
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
import com.zuehlke.pgadmissions.domain.application.Address;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationExportDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationExportBuilder {

    @Value("${xml.export.source}")
    private String exportSource;

    @Inject
    private ImportedEntityService importedEntityService;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private PropertyLoader propertyLoader;

    @Autowired
    private ApplicationExportBuilderHelper applicationExportBuilderHelper;

    public SubmitAdmissionsApplicationRequest build(ApplicationExportDTO applicationExportDTO) throws Exception {
        SubmitAdmissionsApplicationRequest request = objectFactory.createSubmitAdmissionsApplicationRequest();
        request.setApplication(buildApplication(applicationExportDTO));
        return request;
    }

    public ApplicationExportBuilder localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        return this;
    }

    private ApplicationTp buildApplication(ApplicationExportDTO applicationExportDTO) throws Exception {
        ApplicationTp applicationTp = objectFactory.createApplicationTp();
        applicationTp.setSource(exportSource);
        applicationTp.setApplicant(buildApplicant(applicationExportDTO));
        applicationTp.setCourseApplication(buildCourseApplication(applicationExportDTO));
        return applicationTp;
    }

    private ApplicantTp buildApplicant(ApplicationExportDTO applicationExportDTO) throws Exception {
        Application application = applicationExportDTO.getApplication();

        ApplicantTp applicant = objectFactory.createApplicantTp();
        applicant.setFullName(buildFullName(application));
        applicant.setSex(buildGender(application));
        applicant.setDateOfBirth(buildDateOfBirth(application));
        applicant.setNationality(buildFirstNationality(application));
        applicant.setSecondaryNationality(buildSecondNationality(application));
        applicant.setCountryOfBirth(buildCountry(application));
        applicant.setCountryOfDomicile(buildDomicile(application));
        applicant.setVisaRequired(BooleanUtils.toBoolean(application.getPersonalDetail().getVisaRequired()));

        if (BooleanUtils.isTrue(application.getPersonalDetail().getVisaRequired())) {
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
        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(application.getPersonalDetail().getFirstLanguageLocale()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification(application));
        applicant.setApplicantID(StringUtils.trimToNull(applicationExportDTO.getCreatorExportId()));

        return applicant;
    }

    private DomicileTp buildDomicile(Application application) {
        DomicileTp domicileTp = objectFactory.createDomicileTp();
        ImportedEntitySimple domicile = application.getPersonalDetail().getDomicile();
        domicileTp.setCode(getImportedEntityCode(application.getInstitution(), domicile));
        domicileTp.setName(domicile.getName());
        return domicileTp;
    }

    private NameTp buildFullName(Application application) {
        NameTp nameTp = objectFactory.createNameTp();
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        User applicant = application.getUser();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
        nameTp.setTitle(personalDetail.getTitle().getName());
        return nameTp;
    }

    private GenderTp buildGender(Application application) {
        return GenderTp.valueOf(getImportedEntityCode(application.getInstitution(), application.getPersonalDetail().getGender()));
    }

    private XMLGregorianCalendar buildDateOfBirth(Application application) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        return applicationExportBuilderHelper.buildXmlDate(personalDetail.getDateOfBirth());
    }

    private NationalityTp buildFirstNationality(Application application) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        ImportedEntitySimple firstNationality = personalDetail.getFirstNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();
        nationalityTp.setCode(getImportedEntityCode(application.getInstitution(), firstNationality));
        nationalityTp.setName(firstNationality.getName());
        return nationalityTp;
    }

    private NationalityTp buildSecondNationality(Application application) {
        ImportedEntitySimple secondNationality = application.getPersonalDetail().getSecondNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();

        if (secondNationality == null) {
            return null;
        } else {
            nationalityTp.setCode(getImportedEntityCode(application.getInstitution(), secondNationality));
            nationalityTp.setName(secondNationality.getName());
            return nationalityTp;
        }
    }

    private CountryTp buildCountry(Application application) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        CountryTp countryTp = objectFactory.createCountryTp();
        ImportedEntitySimple country = personalDetail.getCountry();
        countryTp.setCode(getImportedEntityCode(application.getInstitution(), country));
        countryTp.setName(country.getName());
        return countryTp;
    }

    private PassportTp buildPassport(Application application) throws Exception {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail.getPassport() == null) {
            PassportTp passportTp = objectFactory.createPassportTp();
            String notProvided = propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED);
            passportTp.setName(notProvided);
            passportTp.setNumber(notProvided);
            passportTp.setExpiryDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().plusYears(1)));
            passportTp.setIssueDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().minusYears(1)));
            return passportTp;
        } else {
            PassportTp passportTp = objectFactory.createPassportTp();
            passportTp.setName(personalDetail.getPassport().getName());
            passportTp.setNumber(personalDetail.getPassport().getNumber());
            passportTp.setExpiryDate(applicationExportBuilderHelper.buildXmlDate(personalDetail.getPassport().getExpiryDate()));
            passportTp.setIssueDate(applicationExportBuilderHelper.buildXmlDate(personalDetail.getPassport().getIssueDate()));
            return passportTp;
        }
    }

    private DisabilityTp buildDisability(Application application) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        DisabilityTp disabilityTp = objectFactory.createDisabilityTp();
        ImportedEntitySimple disability = personalDetail.getDisability();
        disabilityTp.setCode(getImportedEntityCode(application.getInstitution(), disability));
        disabilityTp.setName(disability.getName());
        return disabilityTp;
    }

    private EthnicityTp buildEthnicity(Application application) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        EthnicityTp ethnicityTp = objectFactory.createEthnicityTp();
        ImportedEntitySimple ethnicity = personalDetail.getEthnicity();
        ethnicityTp.setCode(getImportedEntityCode(application.getInstitution(), ethnicity));
        ethnicityTp.setName(ethnicity.getName());
        return ethnicityTp;
    }

    private ContactDtlsTp buildHomeAddress(Application application) throws Exception {
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        Address currentAddress = application.getAddress().getCurrentAddress();
        addressTp.setAddressLine1(currentAddress.getAddressLine1());
        addressTp.setAddressLine2(currentAddress.getAddressLine2());
        addressTp.setAddressLine3(currentAddress.getAddressTown());
        addressTp.setAddressLine4(currentAddress.getAddressRegion());

        String addressCode = currentAddress.getAddressCode();
        addressTp.setPostCode(addressCode == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : addressCode);

        addressTp.setCountry(getImportedEntityCode(application.getInstitution(), currentAddress.getDomicile()));
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(propertyLoader.load(SYSTEM_PHONE_MOCK));
        return contactDtlsTp;
    }

    private ContactDtlsTp buildCorrespondenceAddress(Application application) throws Exception {
        Address contactAddress = application.getAddress().getContactAddress();
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        addressTp.setAddressLine1(contactAddress.getAddressLine1());
        addressTp.setAddressLine2(contactAddress.getAddressLine2());
        addressTp.setAddressLine3(contactAddress.getAddressTown());
        addressTp.setAddressLine4(contactAddress.getAddressRegion());

        String addressCode = contactAddress.getAddressCode();
        addressTp.setPostCode(addressCode == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : addressCode);

        addressTp.setCountry(getImportedEntityCode(application.getInstitution(), contactAddress.getDomicile()));
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(application.getUser().getEmail());
        contactDtlsTp.setLandline(propertyLoader.load(SYSTEM_PHONE_MOCK));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication(ApplicationExportDTO applicationExportDTO) throws Exception {
        Application application = applicationExportDTO.getApplication();

        CourseApplicationTp applicationTp = objectFactory.createCourseApplicationTp();
        LocalDate confirmedStartDate = application.getConfirmedStartDate();
        applicationTp.setStartMonth(confirmedStartDate == null ? null : confirmedStartDate.toDateTimeAtStartOfDay());
        applicationTp.setAgreedSupervisorName(buildAgreedSupervisorName(applicationExportDTO.getPrimarySupervisor()));
        applicationTp.setPersonalStatement(propertyLoader.load(SYSTEM_REFER_TO_DOCUMENT));
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(application, applicationTp));
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setExternalApplicationID(application.getCode());

        String creatorIpAddress = applicationExportDTO.getCreatorIpAddress();
        applicationTp.setIpAddress(creatorIpAddress == null ? propertyLoader.load(SYSTEM_IP_PLACEHOLDER) : creatorIpAddress);
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(application.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee(applicationExportDTO.getApplicationReferences()));

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
            CommentPositionDetail positionDetail = offerRecommendationComment.getPositionDetail();
            applicationTp.setAtasStatement(positionDetail == null ? null : positionDetail.getPositionDescription());

            CommentOfferDetail offerDetail = offerRecommendationComment.getOfferDetail();
            if (offerDetail != null) {
                String none = propertyLoader.load(SYSTEM_NONE);
                LocalDate positionProvisionalStartDate = offerDetail.getPositionProvisionalStartDate();
                String conditions = offerDetail.getAppointmentConditions();

                String offerSummary = propertyLoader.load(APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION) + ": " + (conditions == null ? none : conditions)
                        + "\n\n" + propertyLoader.load(APPLICATION_PREFERRED_START_DATE) + ": "
                        + (positionProvisionalStartDate == null ? none : positionProvisionalStartDate.toString(propertyLoader.load(SYSTEM_DATE_FORMAT)));
                applicationTp.setDepartmentalOfferConditions(offerSummary);
            }
        }

        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence(ApplicationExportDTO applicationExportDTO) throws Exception {
        Application application = applicationExportDTO.getApplication();

        Program program = application.getProgram();
        ProgrammeOccurrenceTp occurrenceTp = objectFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getImportedCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance(application));

        ResourceStudyOptionInstance exportProgramInstance = applicationExportDTO.getExportProgramInstance();
        occurrenceTp.setAcademicYear(applicationExportBuilderHelper.buildXmlDateYearOnly(exportProgramInstance.getBusinessYear()));
        String exportInstanceIdentifier = exportProgramInstance.getIdentifier();
        occurrenceTp.setIdentifier(exportInstanceIdentifier == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : exportInstanceIdentifier);
        occurrenceTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationCloseDate()));
        return occurrenceTp;
    }

    private ModeofattendanceTp buildModeofattendance(Application application) {
        ApplicationProgramDetail programmeDetails = application.getProgramDetail();
        ModeofattendanceTp modeofattendanceTp = objectFactory.createModeofattendanceTp();
        ImportedEntitySimple studyOption = programmeDetails.getStudyOption();
        modeofattendanceTp.setCode(getImportedEntityCode(application.getInstitution(), studyOption));
        modeofattendanceTp.setName(studyOption.getName());
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
        ApplicationProgramDetail programmeDetails = application.getProgramDetail();
        SourceOfInterestTp interestTp = objectFactory.createSourceOfInterestTp();
        ImportedEntitySimple sourceOfInterest = programmeDetails.getReferralSource();
        if (sourceOfInterest == null) {
            return null;
        }
        interestTp.setCode(getImportedEntityCode(application.getInstitution(), sourceOfInterest));
        interestTp.setName(sourceOfInterest.getName());
        return interestTp;
    }

    private QualificationDetailsTp buildQualificationDetails(Application application) throws Exception {
        QualificationDetailsTp resultList = objectFactory.createQualificationDetailsTp();

        Set<ApplicationQualification> qualifications = application.getQualifications();
        if (!qualifications.isEmpty()) {
            for (ApplicationQualification qualification : qualifications) {
                QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();

                qualificationsTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(qualification.getStartDate()));
                qualificationsTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(qualification.getAwardDate()));

                qualificationsTp.setGrade(qualification.getGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getLanguage());

                ImportedProgram importedProgram = qualification.getProgram();
                qualificationsTp.setMainSubject(importedProgram.getName());

                QualificationTp qualificationTp = objectFactory.createQualificationTp();
                ImportedEntitySimple qualificationType = importedProgram.getQualificationType();
                qualificationTp.setCode(getImportedEntityCode(application.getInstitution(), qualificationType));
                qualificationTp.setName(qualificationType.getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = objectFactory.createInstitutionTp();
                ImportedInstitution institution = qualification.getProgram().getInstitution();
                String institutionCode = getImportedEntityCode(application.getInstitution(), institution);
                institutionTp.setCode(institutionCode.startsWith("CUST") ? "OTHER" : institutionCode);
                institutionTp.setName(qualification.getProgram().getName());

                CountryTp countryTp = objectFactory.createCountryTp();
                ImportedEntitySimple domicile = institution.getDomicile();
                countryTp.setCode(getImportedEntityCode(application.getInstitution(), domicile));
                countryTp.setName(domicile.getName());
                institutionTp.setCountry(countryTp);

                qualificationsTp.setInstitution(institutionTp);
                resultList.getQualificationDetail().add(qualificationsTp);
            }
        } else {
            QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();
            qualificationsTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().minusYears(1)));
            qualificationsTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(new LocalDate().plusYears(1)));

            String notProvided = propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED);

            qualificationsTp.setGrade(notProvided);
            qualificationsTp.setLanguageOfInstruction(notProvided);
            qualificationsTp.setMainSubject(notProvided);

            QualificationTp qualificationTp = objectFactory.createQualificationTp();
            qualificationTp.setCode("6");
            qualificationTp.setName("Other examinations and/or information");
            qualificationsTp.setQualification(qualificationTp);

            InstitutionTp institutionTp = objectFactory.createInstitutionTp();
            institutionTp.setCode(propertyLoader.load(SYSTEM_OTHER));
            institutionTp.setName(notProvided);
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

    private RefereeListTp buildReferee(List<ApplicationReferenceDTO> exportReferees) throws Exception {
        int referenceCount = exportReferees.size();
        RefereeListTp resultList = objectFactory.createRefereeListTp();

        for (int i = 0; i < referenceCount; i++) {
            ApplicationReferenceDTO reference = exportReferees.get(i);
            RefereeTp refereeTp = objectFactory.createRefereeTp();
            refereeTp.setPosition(reference.getJobTitle());
            NameTp nameTp = objectFactory.createNameTp();
            nameTp.setForename1(reference.getUser().getFirstName());
            nameTp.setSurname(reference.getUser().getLastName());
            refereeTp.setName(nameTp);

            ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
            contactDtlsTp.setEmail(reference.getUser().getEmail());
            contactDtlsTp.setLandline(propertyLoader.load(SYSTEM_PHONE_MOCK));

            AddressTp addressTp = objectFactory.createAddressTp();
            addressTp.setAddressLine1(reference.getAddressLine1());
            addressTp.setAddressLine2(reference.getAddressLine2());
            addressTp.setAddressLine3(reference.getAddressTown());
            addressTp.setAddressLine4(reference.getAddressRegion());
            addressTp.setPostCode(reference.getAddressCode());
            addressTp.setCountry(reference.getAddressDomicile());
            contactDtlsTp.setAddressDtls(addressTp);
            refereeTp.setContactDetails(contactDtlsTp);
            resultList.getReferee().add(refereeTp);
        }
        return resultList;
    }

    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification(Application application) throws Exception {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = objectFactory.createEnglishLanguageQualificationDetailsTp();

        if (personalDetail.getLanguageQualificationAvailable()) {
            ApplicationLanguageQualification languageQualification = personalDetail.getLanguageQualification();
            EnglishLanguageTp englishLanguageTp = objectFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(applicationExportBuilderHelper.buildXmlDate(languageQualification.getExamDate()));

            String languageQualificationTypeCode = getImportedEntityCode(application.getInstitution(), languageQualification.getType());
            if (languageQualificationTypeCode.startsWith("OTHER")) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.OTHER);
                englishLanguageTp.setOtherLanguageExam(propertyLoader.load(SYSTEM_REFER_TO_DOCUMENT));
            } else if (languageQualificationTypeCode.startsWith("TOEFL")) {
                englishLanguageTp.setLanguageExam(QualificationsinEnglishTp.TOEFL);
                englishLanguageTp.setMethod(languageQualificationTypeCode);
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

            EnglishLanguageScoreTp essayOrSpeakingScoreTp;
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

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> String getImportedEntityCode(Institution institution,
            ImportedEntity<?> importedEntity) {
        V mapping = importedEntityService.getEnabledImportedEntityMapping(institution, (T) importedEntity);
        return mapping == null ? null : mapping.getCode();
    }

}
