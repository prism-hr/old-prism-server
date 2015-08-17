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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;

import java.util.Arrays;
import java.util.List;

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
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceStudyOptionInstanceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationDemographicRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationLanguageQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationOfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExport;
import com.zuehlke.pgadmissions.rest.representation.user.UserInstitutionIdentityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedInstitutionResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationExportBuilder {

    @Value("${xml.export.source}")
    private String exportSource;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private PropertyLoader propertyLoader;

    @Autowired
    private ApplicationExportBuilderHelper applicationExportBuilderHelper;

    public SubmitAdmissionsApplicationRequest build(ApplicationRepresentationExport applicationExport) throws Exception {
        SubmitAdmissionsApplicationRequest request = objectFactory.createSubmitAdmissionsApplicationRequest();
        request.setApplication(buildApplication(applicationExport));
        return request;
    }

    public ApplicationExportBuilder localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        return this;
    }

    private ApplicationTp buildApplication(ApplicationRepresentationExport applicationExport) throws Exception {
        ApplicationTp applicationTp = objectFactory.createApplicationTp();
        applicationTp.setSource(exportSource);
        applicationTp.setApplicant(buildApplicant(applicationExport));
        applicationTp.setCourseApplication(buildCourseApplication(applicationExport));
        return applicationTp;
    }

    private ApplicantTp buildApplicant(ApplicationRepresentationExport applicationExport) throws Exception {

        ApplicantTp applicant = objectFactory.createApplicantTp();
        applicant.setFullName(buildFullName(applicationExport));
        applicant.setSex(buildGender(applicationExport));
        applicant.setDateOfBirth(buildDateOfBirth(applicationExport));
        applicant.setNationality(buildFirstNationality(applicationExport));
        applicant.setSecondaryNationality(buildSecondNationality(applicationExport));
        applicant.setCountryOfBirth(buildCountry(applicationExport));
        applicant.setCountryOfDomicile(buildDomicile(applicationExport));

        boolean visaRequired = BooleanUtils.toBoolean(applicationExport.getPersonalDetail().getVisaRequired());
        applicant.setVisaRequired(visaRequired);
        if (visaRequired) {
            applicant.setPassport(buildPassport(applicationExport));
        }

        ApplicationDemographicRepresentation demographic = applicationExport.getPersonalDetail().getDemographic();
        if (demographic != null) {
            applicant.setEthnicity(buildEthnicity(demographic.getEthnicity()));
            applicant.setDisability(buildDisability(demographic.getDisability()));
        }

        UserRepresentationSimple user = applicationExport.getUser();
        ApplicationAddressRepresentation address = applicationExport.getAddress();
        applicant.setHomeAddress(buildHomeAddress(address.getCurrentAddress(), user));
        applicant.setCorrespondenceAddress(buildHomeAddress(address.getContactAddress(), user));

        String convictionsText = applicationExport.getAdditionalInformation().getConvictionsText();
        applicant.setCriminalConvictionDetails(applicationExportBuilderHelper.cleanString(convictionsText));
        applicant.setCriminalConvictions(convictionsText != null);
        applicant.setQualificationList(buildQualificationDetails(applicationExport));
        applicant.setEmployerList(buildEmployer(applicationExport));
        applicant.setEnglishIsFirstLanguage(BooleanUtils.toBoolean(applicationExport.getPersonalDetail().getFirstLanguageLocale()));
        applicant.setEnglishLanguageQualificationList(buildEnglishLanguageQualification(applicationExport));

        UserInstitutionIdentityRepresentation identity = applicationExport.getUserInstitutionIdentity();
        applicant.setApplicantID(StringUtils.trimToNull(identity == null ? null : identity.getIdentifier()));

        return applicant;
    }

    private DomicileTp buildDomicile(ApplicationRepresentationExport applicationExport) {
        DomicileTp domicileTp = objectFactory.createDomicileTp();
        ImportedEntityResponse domicile = applicationExport.getPersonalDetail().getDomicile();
        domicileTp.setCode(getImportedEntityCode(domicile));
        domicileTp.setName(domicile.getName());
        return domicileTp;
    }

    private NameTp buildFullName(ApplicationRepresentationExport applicationExport) {
        NameTp nameTp = objectFactory.createNameTp();
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
        UserRepresentationSimple applicant = applicationExport.getUser();
        nameTp.setSurname(applicant.getLastName());
        nameTp.setForename1(applicant.getFirstName());
        nameTp.setForename2(applicant.getFirstName2());
        nameTp.setForename3(applicant.getFirstName3());
        nameTp.setTitle(personalDetail.getTitle().getName());
        return nameTp;
    }

    private GenderTp buildGender(ApplicationRepresentationExport applicationExport) {
        return GenderTp.valueOf(getImportedEntityCode(applicationExport.getPersonalDetail().getGender()));
    }

    private XMLGregorianCalendar buildDateOfBirth(ApplicationRepresentationExport applicationExport) {
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
        return applicationExportBuilderHelper.buildXmlDate(personalDetail.getDateOfBirth());
    }

    private NationalityTp buildFirstNationality(ApplicationRepresentationExport applicationExport) {
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
        ImportedEntityResponse firstNationality = personalDetail.getFirstNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();
        nationalityTp.setCode(getImportedEntityCode(firstNationality));
        nationalityTp.setName(firstNationality.getName());
        return nationalityTp;
    }

    private NationalityTp buildSecondNationality(ApplicationRepresentationExport applicationExport) {
        ImportedEntityResponse secondNationality = applicationExport.getPersonalDetail().getSecondNationality();
        NationalityTp nationalityTp = objectFactory.createNationalityTp();

        if (secondNationality == null) {
            return null;
        } else {
            nationalityTp.setCode(getImportedEntityCode(secondNationality));
            nationalityTp.setName(secondNationality.getName());
            return nationalityTp;
        }
    }

    private CountryTp buildCountry(ApplicationRepresentationExport applicationExport) {
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
        CountryTp countryTp = objectFactory.createCountryTp();
        ImportedEntityResponse country = personalDetail.getCountry();
        countryTp.setCode(getImportedEntityCode(country));
        countryTp.setName(country.getName());
        return countryTp;
    }

    private PassportTp buildPassport(ApplicationRepresentationExport applicationExport) throws Exception {
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
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

    private EthnicityTp buildEthnicity(ImportedEntityResponse ethnicity) {
        EthnicityTp ethnicityTp = objectFactory.createEthnicityTp();
        ethnicityTp.setCode(getImportedEntityCode(ethnicity));
        ethnicityTp.setName(ethnicity.getName());
        return ethnicityTp;
    }

    private DisabilityTp buildDisability(ImportedEntityResponse disability) {
        DisabilityTp disabilityTp = objectFactory.createDisabilityTp();
        disabilityTp.setCode(getImportedEntityCode(disability));
        disabilityTp.setName(disability.getName());
        return disabilityTp;
    }

    private ContactDtlsTp buildHomeAddress(AddressApplicationRepresentation address, UserRepresentationSimple user) throws Exception {
        ContactDtlsTp contactDtlsTp = objectFactory.createContactDtlsTp();
        AddressTp addressTp = objectFactory.createAddressTp();
        addressTp.setAddressLine1(address.getAddressLine1());
        addressTp.setAddressLine2(address.getAddressLine2());
        addressTp.setAddressLine3(address.getAddressTown());
        addressTp.setAddressLine4(address.getAddressRegion());

        String addressCode = address.getAddressCode();
        addressTp.setPostCode(addressCode == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : addressCode);

        addressTp.setCountry(getImportedEntityCode(address.getDomicile()));
        contactDtlsTp.setAddressDtls(addressTp);
        contactDtlsTp.setEmail(user.getEmail());
        contactDtlsTp.setLandline(propertyLoader.load(SYSTEM_PHONE_MOCK));
        return contactDtlsTp;
    }

    private CourseApplicationTp buildCourseApplication(ApplicationRepresentationExport applicationExport) throws Exception {

        CourseApplicationTp applicationTp = objectFactory.createCourseApplicationTp();
        ApplicationOfferRepresentation offer = applicationExport.getOfferRecommendation();

        LocalDate confirmedStartDate = offer.getPositionProvisionalStartDate();

        applicationTp.setStartMonth(confirmedStartDate == null ? null : confirmedStartDate.toDateTimeAtStartOfDay());
        applicationTp.setAgreedSupervisorName(buildAgreedSupervisorName(applicationExport.getAssignedSupervisors()));
        applicationTp.setPersonalStatement(propertyLoader.load(SYSTEM_REFER_TO_DOCUMENT));
        applicationTp.setSourcesOfInterest(buildSourcesOfInterest(applicationExport));
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(applicationExport.getSubmittedTimestamp()));
        applicationTp.setExternalApplicationID(applicationExport.getCode());

        applicationTp.setIpAddress(propertyLoader.load(SYSTEM_IP_PLACEHOLDER));
        applicationTp.setCreationDate(applicationExportBuilderHelper.buildXmlDate(applicationExport.getSubmittedTimestamp()));
        applicationTp.setRefereeList(buildReferee(applicationExport.getReferees()));

        switch (applicationExport.getState().getStateGroup()) {
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
            throw new Error("Application in state " + applicationExport.getState().getState().name() + " cannot be exported");
        }

        applicationTp.setProgramme(buildProgrammeOccurence(applicationExport));

        if (offer != null) {
            applicationTp.setAtasStatement(offer.getPositionDescription());

            String none = propertyLoader.load(SYSTEM_NONE);
            LocalDate positionProvisionalStartDate = offer.getPositionProvisionalStartDate();
            String conditions = offer.getAppointmentConditions();

            String offerSummary = propertyLoader.load(APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION) + ": " + (conditions == null ? none : conditions)
                    + "\n\n" + propertyLoader.load(APPLICATION_PREFERRED_START_DATE) + ": "
                    + (positionProvisionalStartDate == null ? none : positionProvisionalStartDate.toString(propertyLoader.load(SYSTEM_DATE_FORMAT)));
            applicationTp.setDepartmentalOfferConditions(offerSummary);
        }

        return applicationTp;
    }

    private ProgrammeOccurrenceTp buildProgrammeOccurence(ApplicationRepresentationExport applicationExport) throws Exception {
        ResourceRepresentationSimple program = applicationExport.getProgram();
        ProgrammeOccurrenceTp occurrenceTp = objectFactory.createProgrammeOccurrenceTp();
        occurrenceTp.setCode(program.getImportedCode());
        occurrenceTp.setModeOfAttendance(buildModeofattendance(applicationExport));

        ResourceStudyOptionInstanceRepresentation exportProgramInstance = applicationExport.getResourceStudyOptionInstance();
        occurrenceTp.setAcademicYear(applicationExportBuilderHelper.buildXmlDateYearOnly(exportProgramInstance.getBusinessYear()));
        String exportInstanceIdentifier = exportProgramInstance.getIdentifier();
        occurrenceTp.setIdentifier(exportInstanceIdentifier == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : exportInstanceIdentifier);
        occurrenceTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationStartDate()));
        occurrenceTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(exportProgramInstance.getApplicationCloseDate()));
        return occurrenceTp;
    }

    private ModeofattendanceTp buildModeofattendance(ApplicationRepresentationExport applicationExport) {
        ApplicationProgramDetailRepresentation programmeDetail = applicationExport.getProgramDetail();
        ModeofattendanceTp modeofattendanceTp = objectFactory.createModeofattendanceTp();
        ImportedEntityResponse studyOption = programmeDetail.getStudyOption();
        modeofattendanceTp.setCode(getImportedEntityCode(studyOption));
        modeofattendanceTp.setName(studyOption.getName());
        return modeofattendanceTp;
    }

    private NameTp buildAgreedSupervisorName(List<ApplicationAssignedSupervisorRepresentation> supervisors) {
        for (ApplicationAssignedSupervisorRepresentation supervisor : supervisors) {
            if (supervisor.getRole().equals(APPLICATION_PRIMARY_SUPERVISOR)) {
                NameTp nameTp = objectFactory.createNameTp();
                UserRepresentationSimple user = supervisor.getUser();
                nameTp.setForename1(user.getFirstName());
                nameTp.setSurname(user.getLastName());
                return nameTp;
            }
        }
        return null;
    }

    private SourceOfInterestTp buildSourcesOfInterest(ApplicationRepresentationExport applicationExport) {
        ApplicationProgramDetailRepresentation programDetails = applicationExport.getProgramDetail();
        SourceOfInterestTp interestTp = objectFactory.createSourceOfInterestTp();
        ImportedEntityResponse sourceOfInterest = programDetails.getReferralSource();
        if (sourceOfInterest != null) {
            interestTp.setCode(getImportedEntityCode(sourceOfInterest));
            interestTp.setName(sourceOfInterest.getName());
            return interestTp;
        }
        return null;
    }

    private QualificationDetailsTp buildQualificationDetails(ApplicationRepresentationExport applicationExport) throws Exception {
        QualificationDetailsTp resultList = objectFactory.createQualificationDetailsTp();

        List<ApplicationQualificationRepresentation> qualifications = applicationExport.getQualifications();
        if (!qualifications.isEmpty()) {
            for (ApplicationQualificationRepresentation qualification : qualifications) {
                QualificationsTp qualificationsTp = objectFactory.createQualificationsTp();

                qualificationsTp.setStartDate(applicationExportBuilderHelper.buildXmlDate(qualification.getStartDate()));
                qualificationsTp.setEndDate(applicationExportBuilderHelper.buildXmlDate(qualification.getAwardDate()));

                qualificationsTp.setGrade(qualification.getGrade());
                qualificationsTp.setLanguageOfInstruction(qualification.getLanguage());

                ImportedProgramResponse importedProgram = qualification.getProgram();
                qualificationsTp.setMainSubject(importedProgram.getName());

                QualificationTp qualificationTp = objectFactory.createQualificationTp();
                ImportedEntityResponse qualificationType = importedProgram.getQualificationType();
                qualificationTp.setCode(getImportedEntityCode(qualificationType));
                qualificationTp.setName(qualificationType.getName());
                qualificationsTp.setQualification(qualificationTp);

                InstitutionTp institutionTp = objectFactory.createInstitutionTp();
                ImportedInstitutionResponse institution = qualification.getProgram().getInstitution();
                String institutionCode = getImportedEntityCode(institution);
                institutionTp.setCode(institutionCode.startsWith("CUST") ? "OTHER" : institutionCode);
                institutionTp.setName(qualification.getProgram().getName());

                CountryTp countryTp = objectFactory.createCountryTp();
                ImportedEntityResponse domicile = institution.getDomicile();
                countryTp.setCode(getImportedEntityCode(domicile));
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

    private EmploymentDetailsTp buildEmployer(ApplicationRepresentationExport applicationExport) {
        EmploymentDetailsTp resultList = objectFactory.createEmploymentDetailsTp();
        List<ApplicationEmploymentPositionRepresentation> employmentPositions = applicationExport.getEmploymentPositions();
        if (!employmentPositions.isEmpty()) {
            for (ApplicationEmploymentPositionRepresentation employmentPosition : employmentPositions) {
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

    private RefereeListTp buildReferee(List<ApplicationRefereeRepresentation> exportReferees) throws Exception {
        int referenceCount = exportReferees.size();
        RefereeListTp resultList = objectFactory.createRefereeListTp();

        for (int i = 0; i < referenceCount; i++) {
            ApplicationRefereeRepresentation reference = exportReferees.get(i);
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
            AddressApplicationRepresentation address = reference.getAddress();
            addressTp.setAddressLine1(address.getAddressLine1());
            addressTp.setAddressLine2(address.getAddressLine2());
            addressTp.setAddressLine3(address.getAddressTown());
            addressTp.setAddressLine4(address.getAddressRegion());
            addressTp.setPostCode(address.getAddressCode());
            addressTp.setCountry(getImportedEntityCode(address.getDomicile()));
            contactDtlsTp.setAddressDtls(addressTp);
            refereeTp.setContactDetails(contactDtlsTp);
            resultList.getReferee().add(refereeTp);
        }
        return resultList;
    }

    private EnglishLanguageQualificationDetailsTp buildEnglishLanguageQualification(ApplicationRepresentationExport applicationExport) throws Exception {
        ApplicationPersonalDetailRepresentation personalDetail = applicationExport.getPersonalDetail();
        EnglishLanguageQualificationDetailsTp englishLanguageQualificationDetailsTp = objectFactory.createEnglishLanguageQualificationDetailsTp();

        ApplicationLanguageQualificationRepresentation languageQualification = personalDetail.getLanguageQualification();

        if (languageQualification != null) {
            EnglishLanguageTp englishLanguageTp = objectFactory.createEnglishLanguageTp();
            englishLanguageTp.setDateTaken(applicationExportBuilderHelper.buildXmlDate(languageQualification.getExamDate()));

            String languageQualificationTypeCode = getImportedEntityCode(languageQualification.getType());
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

    public <T extends ImportedEntityResponse> String getImportedEntityCode(T importedEntity) {
        return importedEntity == null ? null : importedEntity.getCode();
    }

}
