package com.zuehlke.pgadmissions.services;

import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.FundingSource;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.Title;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationLanguageQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPassportDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;
import com.zuehlke.pgadmissions.rest.validation.validator.CompleteApplicationValidator;

@Service
@Transactional
public class ApplicationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private CompleteApplicationValidator completeApplicationValidator;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }

    public Application create(User user, ApplicationDTO applicationDTO) {
        Advert advert = advertService.getById(applicationDTO.getAdvertId());
        Application application = new Application().withUser(user).withParentResource(advert.getParentResource()).withDoRetain(false)
                .withCreatedTimestamp(new DateTime());

        Application previousApplication = getPreviousApplication(application);
        if (previousApplication != null) {
            applicationCopyHelper.copyApplicationFormData(application, previousApplication);
        }

        AdvertClosingDate closingDate = advert.getClosingDate();
        application.setClosingDate(closingDate == null ? null : closingDate.getClosingDate());

        return application;
    }

    public void save(Application application) {
        entityService.save(application);
    }

    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }

    // TODO: handle null response - study option expired
    public LocalDate getEarliestStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());

        if (studyOption == null) {
            return null;
        }

        LocalDate baseline = new LocalDate();
        LocalDate studyOptionStart = studyOption.getApplicationStartDate();

        LocalDate earliestStartDate = studyOptionStart.isBefore(baseline) ? baseline : studyOptionStart;
        return earliestStartDate.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    // TODO: handle null response - study option expired
    public LocalDate getRecommendedStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());

        if (studyOption == null) {
            return null;
        }

        LocalDate studyOptionStart = studyOption.getApplicationStartDate();
        LocalDate studyOptionClose = studyOption.getApplicationCloseDate();

        LocalDate recommendedStartDate = application.getRecommendedStartDate();

        if (recommendedStartDate.isAfter(studyOptionClose)) {
            recommendedStartDate = studyOptionClose;
        } else if (recommendedStartDate.isBefore(studyOptionStart)) {
            recommendedStartDate = studyOptionStart;
        }

        return recommendedStartDate.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    // TODO: handle null response - study option expired
    public LocalDate getLatestStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());
        return studyOption == null ? null : studyOption.getApplicationCloseDate().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    public String getApplicationExportReference(Application application) {
        return applicationDAO.getApplicationExportReference(application);
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return applicationDAO.getApplicationCreatorIpAddress(application);
    }

    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return applicationDAO.getPrimarySupervisor(offerRecommendationComment);
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return applicationDAO.getApplicationExportQualifications(application);
    }

    public List<ApplicationReferee> getApplicationExportReferees(Application application) {
        return applicationDAO.getApplicationExportReferees(application);
    }

    public List<Application> getUclApplicationsForExport() {
        return applicationDAO.getUclApplicationsForExport();
    }

    public void saveProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws DeduplicationException {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();
        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
            application.setProgramDetail(programDetail);
        }

        StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, institution, programDetailDTO.getStudyOption().name());
        ReferralSource referralSource = importedEntityService.getById(ReferralSource.class, institution, programDetailDTO.getReferralSource());
        programDetail.setStudyOption(studyOption);
        programDetail.setStartDate(programDetailDTO.getStartDate());
        programDetail.setReferralSource(referralSource);

        Iterator<ApplicationSupervisor> supervisorsIterator = application.getSupervisors().iterator();
        while (supervisorsIterator.hasNext()) {
            final ApplicationSupervisor supervisor = supervisorsIterator.next();
            Optional<ApplicationSupervisorDTO> supervisorDTO = Iterables.tryFind(programDetailDTO.getSupervisors(), new Predicate<ApplicationSupervisorDTO>() {
                @Override
                public boolean apply(ApplicationSupervisorDTO dto) {
                    return supervisor.getUser().getEmail().equals(dto.getUser().getEmail());
                }
            });
            if (supervisorDTO.isPresent()) {
                programDetailDTO.getSupervisors().remove(supervisorDTO.get());
                supervisor.setAware(supervisorDTO.get().getAware());
            } else {
                supervisorsIterator.remove();
            }
        }
        for (ApplicationSupervisorDTO supervisorDTO : programDetailDTO.getSupervisors()) {
            User user = userService.getOrCreateUser(supervisorDTO.getUser().getFirstName(), supervisorDTO.getUser().getLastName(), supervisorDTO.getUser()
                    .getEmail());
            ApplicationSupervisor supervisor = new ApplicationSupervisor().withAware(supervisorDTO.getAware()).withUser(user);
            application.getSupervisors().add(supervisor);
        }
    }

    public void savePersonalDetail(Integer applicationId, ApplicationPersonalDetailDTO personalDetailDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(personalDetail);
        }

        User user = application.getUser();
        user.setFirstName(personalDetailDTO.getUser().getFirstName());
        user.setFirstName2(Strings.emptyToNull(personalDetailDTO.getUser().getFirstName2()));
        user.setFirstName3(Strings.emptyToNull(personalDetailDTO.getUser().getFirstName3()));
        user.setLastName(personalDetailDTO.getUser().getLastName());

        Title title = importedEntityService.getById(Title.class, institution, personalDetailDTO.getTitle());
        Gender gender = importedEntityService.getById(Gender.class, institution, personalDetailDTO.getGender());
        Country country = importedEntityService.getById(Country.class, institution, personalDetailDTO.getCountry());
        Language firstNationality = importedEntityService.getById(Language.class, institution, personalDetailDTO.getFirstNationality());
        Language secondNationality = personalDetailDTO.getSecondNationality() != null ? importedEntityService.<Language>getById(Language.class, institution,
                personalDetailDTO.getSecondNationality()) : null;
        Domicile residenceCountry = importedEntityService.getById(Domicile.class, institution, personalDetailDTO.getResidenceCountry());
        Ethnicity ethnicity = importedEntityService.getById(Ethnicity.class, institution, personalDetailDTO.getEthnicity());
        Disability disability = importedEntityService.getById(Disability.class, institution, personalDetailDTO.getDisability());
        personalDetail.setTitle(title);
        personalDetail.setGender(gender);
        personalDetail.setDateOfBirth(personalDetailDTO.getDateOfBirth());
        personalDetail.setCountry(country);
        personalDetail.setFirstNationality(firstNationality);
        personalDetail.setSecondNationality(secondNationality);
        personalDetail.setFirstLanguageEnglish(personalDetailDTO.getFirstLanguageEnglish());
        personalDetail.setResidenceCountry(residenceCountry);
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());
        personalDetail.setPhoneNumber(personalDetailDTO.getPhoneNumber());
        personalDetail.setMessenger(Strings.emptyToNull(personalDetailDTO.getMessenger()));
        personalDetail.setEthnicity(ethnicity);
        personalDetail.setDisability(disability);

        ApplicationLanguageQualificationDTO languageQualificationDTO = personalDetailDTO.getLanguageQualification();
        if (languageQualificationDTO == null) {
            personalDetail.setLanguageQualification(null);
        } else {
            ApplicationLanguageQualification languageQualification = personalDetail.getLanguageQualification();
            if (languageQualification == null) {
                languageQualification = new ApplicationLanguageQualification();
                personalDetail.setLanguageQualification(languageQualification);
            }
            ImportedLanguageQualificationType languageQualificationType = importedEntityService.getById(ImportedLanguageQualificationType.class, institution,
                    languageQualificationDTO.getType());
            Document proofOfAward = entityService.getById(Document.class, languageQualificationDTO.getProofOfAward().getId());
            languageQualification.setType(languageQualificationType);
            languageQualification.setExamDate(languageQualificationDTO.getExamDate());
            languageQualification.setOverallScore(languageQualificationDTO.getOverallScore());
            languageQualification.setReadingScore(languageQualificationDTO.getReadingScore());
            languageQualification.setWritingScore(languageQualificationDTO.getWritingScore());
            languageQualification.setSpeakingScore(languageQualificationDTO.getSpeakingScore());
            languageQualification.setListeningScore(languageQualificationDTO.getListeningScore());
            languageQualification.setDocument(proofOfAward);
        }

        ApplicationPassportDTO passportDTO = personalDetailDTO.getPassport();
        if (passportDTO == null) {
            personalDetail.setPassport(null);
        } else {
            ApplicationPassport passport = personalDetail.getPassport();
            if (passport == null) {
                passport = new ApplicationPassport();
                personalDetail.setPassport(passport);
            }
            passport.setNumber(passportDTO.getNumber());
            passport.setName(passportDTO.getName());
            passport.setIssueDate(passportDTO.getIssueDate());
            passport.setExpiryDate(passportDTO.getExpiryDate());
        }
    }

    public void saveAddress(Integer applicationId, ApplicationAddressDTO addressDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();

        ApplicationAddress address = application.getAddress();
        if (address == null) {
            address = new ApplicationAddress();
            application.setAddress(address);
        }

        AddressDTO currentAddressDTO = addressDTO.getCurrentAddress();
        Address currentAddress = address.getCurrentAddress();
        if (currentAddress == null) {
            currentAddress = new Address();
            address.setCurrentAddress(currentAddress);
        }
        copyAddress(institution, currentAddress, currentAddressDTO);

        AddressDTO contactAddressDTO = addressDTO.getContactAddress();
        Address contactAddress = address.getContactAddress();
        if (contactAddress == null) {
            contactAddress = new Address();
            address.setContactAddress(contactAddress);
        }
        copyAddress(institution, contactAddress, contactAddressDTO);
    }

    public ApplicationQualification saveQualification(Integer applicationId, Integer qualificationId, ApplicationQualificationDTO qualificationDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();

        ApplicationQualification qualification;
        if (qualificationId != null) {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        } else {
            qualification = new ApplicationQualification();
            application.getQualifications().add(qualification);
        }

        ImportedInstitution importedInstitution = importedEntityService.getById(ImportedInstitution.class, institution, qualificationDTO.getInstitution()
                .getId());
        QualificationType qualificationType = importedEntityService.getById(QualificationType.class, institution, qualificationDTO.getType());
        Document qualificationDocument = entityService.getById(Document.class, qualificationDTO.getDocument().getId());
        qualification.setInstitution(importedInstitution);
        qualification.setType(qualificationType);
        qualification.setTitle(Strings.emptyToNull(qualificationDTO.getTitle()));
        qualification.setSubject(qualificationDTO.getSubject());
        qualification.setLanguage(qualificationDTO.getLanguage());
        qualification.setStartDate(qualificationDTO.getStartDate());
        qualification.setCompleted(qualificationDTO.getCompleted());
        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setAwardDate(qualificationDTO.getAwardDate());
        qualification.setDocument(qualificationDocument);
        return qualification;
    }

    public void deleteQualification(Integer applicationId, Integer qualificationId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class,
                ImmutableMap.of("application", application, "id", qualificationId));
        application.getQualifications().remove(qualification);
    }

    public ApplicationEmploymentPosition saveEmploymentPosition(Integer applicationId, Integer employmentPositionId,
                                                                ApplicationEmploymentPositionDTO employmentPositionDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationEmploymentPosition employmentPosition;
        if (employmentPositionId != null) {
            employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                    ImmutableMap.of("application", application, "id", employmentPositionId));
        } else {
            employmentPosition = new ApplicationEmploymentPosition();
            application.getEmploymentPositions().add(employmentPosition);
        }

        employmentPosition.setEmployerName(employmentPositionDTO.getEmployerName());

        AddressDTO employerAddressDTO = employmentPositionDTO.getEmployerAddress();
        Address employerAddress = employmentPosition.getEmployerAddress();
        if (employerAddress == null) {
            employerAddress = new Address();
            employmentPosition.setEmployerAddress(employerAddress);
        }
        copyAddress(application.getInstitution(), employerAddress, employerAddressDTO);

        employmentPosition.setPosition(employmentPositionDTO.getPosition());
        employmentPosition.setRemit(employmentPositionDTO.getRemit());
        employmentPosition.setStartDate(employmentPositionDTO.getStartDate());
        employmentPosition.setCurrent(employmentPositionDTO.getCurrent());
        employmentPosition.setEndDate(employmentPositionDTO.getEndDate());

        return employmentPosition;
    }

    public void deleteEmploymentPosition(Integer applicationId, Integer employmentPositionId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationEmploymentPosition employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                ImmutableMap.of("application", application, "id", employmentPositionId));
        application.getEmploymentPositions().remove(employmentPosition);
    }

    public ApplicationFunding saveFunding(Integer applicationId, Integer fundingId, ApplicationFundingDTO fundingDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationFunding funding;
        if (fundingId != null) {
            funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        } else {
            funding = new ApplicationFunding();
            application.getFundings().add(funding);
        }

        FundingSource fundingSource = importedEntityService.getById(FundingSource.class, application.getInstitution(), fundingDTO.getFundingSource());
        Document fundingDocument = entityService.getById(Document.class, fundingDTO.getDocument().getId());

        funding.setFundingSource(fundingSource);
        funding.setDescription(fundingDTO.getDescription());
        funding.setValue(fundingDTO.getValue());
        funding.setAwardDate(fundingDTO.getAwardDate());
        funding.setDocument(fundingDocument);

        return funding;
    }

    public void deleteFunding(Integer applicationId, Integer fundingId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationFunding funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        application.getFundings().remove(funding);
    }

    public ApplicationReferee saveReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) throws DeduplicationException {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationReferee referee;
        if (refereeId != null) {
            referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        } else {
            referee = new ApplicationReferee();
            application.getReferees().add(referee);
        }

        UserDTO userDTO = refereeDTO.getUser();
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        referee.setUser(user);

        referee.setJobEmployer(refereeDTO.getJobEmployer());
        referee.setJobTitle(refereeDTO.getJobTitle());

        AddressDTO addressDTO = refereeDTO.getAddress();
        Address address = referee.getAddress();
        if (address == null) {
            address = new Address();
            referee.setAddress(address);
        }
        copyAddress(application.getInstitution(), address, addressDTO);

        referee.setPhoneNumber(refereeDTO.getPhoneNumber());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));

        return referee;
    }

    public void deleteReferee(Integer applicationId, Integer refereeId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationReferee referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        application.getReferees().remove(referee);
    }

    public void saveAdditionalInformation(Integer applicationId, ApplicationAdditionalInformationDTO additionalInformationDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new ApplicationAdditionalInformation();
            application.setAdditionalInformation(additionalInformation);
        }

        additionalInformation.setConvictionsText(Strings.emptyToNull(additionalInformationDTO.getConvictionsText()));
    }

    public void validateApplicationCompleteness(Integer applicationId) {
        Application application = entityService.getById(Application.class, applicationId);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(completeApplicationValidator, application, errors);
        if (errors.hasErrors()) {
            throw new PrismValidationException("Application not completed", errors);
        }
    }

    public LocalDate resolveDueDateBaseline(Application application, Comment comment) {
        if (comment.isApplicationAssignReviewersComment()) {
            LocalDate closingDate = application.getClosingDate();

            if (closingDate != null) {
                application.setClosingDate(null);
                application.setPreviousClosingDate(closingDate);
                return closingDate;
            }
        }
        return null;
    }

    public void postProcessApplication(Application application, Comment comment) throws DeduplicationException {
        if (comment.isApplicationCreatedComment()) {
            applicationSummaryService.incrementApplicationCreatedCount(application);
        }

        if (comment.isProjectCreateComment()) {
            synchroniseProjectSupervisors(application);
        }

        if (comment.isApplicationProvideReferenceComment()) {
            synchroniseReferees(application, comment);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchroniseOfferRecommendation(application, comment);
        }

        if (comment.isRatingComment()) {
            applicationSummaryService.summariseApplication(application, comment);
        }

        if (comment.isTransitionComment()) {
            applicationSummaryService.summariseApplicationProcessing(application);
        }

        if (comment.isApplicationSubmittedComment()) {
            applicationSummaryService.incrementApplicationSubmittedCount(application);
        }

        if (comment.isApplicationApprovedComment()) {
            applicationSummaryService.incrementApplicationApprovedCount(application);
        }

        if (comment.isApplicationRejectedComment()) {
            applicationSummaryService.incrementApplicationRejectedCount(application);
        }

        if (comment.isApplicationWithdrawnComment()) {
            applicationSummaryService.incrementApplicationWithdrawnCount(application);
        }
        
        if (comment.isApplicationPurgeComment()) {
            purgeApplication(application, comment);
        }
    }
    
    private void purgeApplication(Application application, Comment comment) {
        logger.info("Purging application " + application.getCode());
        if (!application.getRetain()) {
            application.setProgramDetail(null);
            application.getSupervisors().clear();
            application.setPersonalDetail(null);
            application.setAddress(null);
            application.getQualifications().clear();
            application.getEmploymentPositions().clear();
            application.getFundings().clear();
            application.getReferees().clear();
            application.setDocument(null);
            application.setAdditionalInformation(null);
        }
        commentService.delete(application, comment);
    }
    
    private void synchroniseProjectSupervisors(Application application) {
        Role supervisorRole = roleService.getById(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
        List<User> supervisorUsers = roleService.getRoleUsers(application, supervisorRole);

        for (User supervisorUser : supervisorUsers) {
            application.getSupervisors().add(new ApplicationSupervisor().withUser(supervisorUser).withAware(true));
        }
    }

    private void synchroniseReferees(Application application, Comment comment) {
        ApplicationReferee referee = applicationDAO.getRefereeByUser(application, comment.getUser());
        referee.setComment(comment);
    }

    private void synchroniseOfferRecommendation(Application application, Comment comment) {
        application.setConfirmedStartDate(comment.getPositionProvisionalStartDate());
        application.setConfirmedPrimarySupervisor(roleService.getRoleUsers(application, PrismRole.APPLICATION_PRIMARY_SUPERVISOR).get(0));
        application.setConfirmedSecondarySupervisor(roleService.getRoleUsers(application, PrismRole.APPLICATION_SECONDARY_SUPERVISOR).get(0));
        application.setConfirmedOfferType(comment.getAppointmentConditions() == null ? PrismOfferType.UNCONDITIONAL : PrismOfferType.CONDITIONAL);
    }

    private void copyAddress(Institution institution, Address to, AddressDTO from) {
        Domicile currentAddressDomicile = importedEntityService.getById(Domicile.class, institution, from.getDomicile());
        to.setDomicile(currentAddressDomicile);
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(Strings.emptyToNull(from.getAddressLine2()));
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(Strings.emptyToNull(from.getAddressRegion()));
        to.setAddressCode(Strings.emptyToNull(from.getAddressCode()));
    }

    private Application getPreviousApplication(Application application) {
        Application previousApplication = applicationDAO.getPreviousSubmittedApplication(application);
        if (previousApplication == null) {
            previousApplication = applicationDAO.getPreviousUnsubmittedApplication(application);
        }
        return previousApplication;
    }

}
