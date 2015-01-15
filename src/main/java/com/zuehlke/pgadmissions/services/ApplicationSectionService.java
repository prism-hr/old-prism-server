package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.*;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.imported.*;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SUGGESTED_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

@Service
@Transactional
public class ApplicationSectionService {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    public void updateProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws DeduplicationException, InstantiationException,
            IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        Institution institution = application.getInstitution();

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
            application.setProgramDetail(programDetail);
        }

        application.setPreviousApplication(programDetailDTO.getPreviousApplication());

        ApplicationStudyDetailDTO studyDetailDTO = programDetailDTO.getStudyDetail();
        if (studyDetailDTO != null) {
            application.setStudyDetail(new ApplicationStudyDetail().withStudyLocation(studyDetailDTO.getStudyLocation())
                    .withStudyDivision(studyDetailDTO.getStudyDivision()).withStudyArea(studyDetailDTO.getStudyArea())
                    .withStudyApplicationId(studyDetailDTO.getStudyApplicationId()).withStudyStartDate(studyDetailDTO.getStudyStartDate()));
        }

        StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, institution, programDetailDTO.getStudyOption().name());
        ReferralSource referralSource = importedEntityService.getById(ReferralSource.class, institution, programDetailDTO.getReferralSource());
        programDetail.setStudyOption(studyOption);
        programDetail.setStartDate(programDetailDTO.getStartDate());
        programDetail.setReferralSource(referralSource);
        programDetail.setLastUpdatedTimestamp(DateTime.now());

        List<String> primaryThemes = programDetailDTO.getPrimaryThemes();
        application.setPrimaryTheme(primaryThemes.isEmpty() ? null : Joiner.on("|").join(primaryThemes));

        List<String> secondaryThemes = programDetailDTO.getSecondaryThemes();
        application.setSecondaryTheme(secondaryThemes.isEmpty() ? null : Joiner.on("|").join(secondaryThemes));

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
    }

    public ApplicationSupervisor updateSupervisor(Integer applicationId, Integer supervisorId, ApplicationSupervisorDTO supervisorDTO)
            throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationSupervisor supervisor;
        if (supervisorId == null) {
            supervisor = new ApplicationSupervisor();
            application.getSupervisors().add(supervisor);
        } else {
            supervisor = entityService.getByProperties(ApplicationSupervisor.class, ImmutableMap.of("application", application, "id", supervisorId));
        }

        AssignedUserDTO userDTO = supervisorDTO.getUser();
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), application.getLocale());

        supervisor.setUser(user);
        supervisor.setAcceptedSupervision(supervisorDTO.getAcceptedSupervision());
        supervisor.setLastUpdatedTimestamp(DateTime.now());

        List<CommentAssignedUser> assignees = Lists.newArrayList();
        if (application.isSubmitted()) {
            assignees.add(new CommentAssignedUser().withUser(user).withRole(roleService.getById(APPLICATION_SUGGESTED_SUPERVISOR)).withRoleTransitionType(CREATE));
        }

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_SUPERVISOR, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return supervisor;
    }

    public void deleteSupervisor(Integer applicationId, Integer supervisorId) throws DeduplicationException, InstantiationException, IllegalAccessException,
            BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationSupervisor supervisor = entityService.getByProperties(ApplicationSupervisor.class,
                ImmutableMap.of("application", application, "id", supervisorId));
        User user = supervisor.getUser();
        application.getSupervisors().remove(supervisor);

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_SUPERVISOR,
                new CommentAssignedUser().withUser(user).withRole(roleService.getById(APPLICATION_SUGGESTED_SUPERVISOR)).withRoleTransitionType(DELETE));
    }

    public void updatePersonalDetail(Integer applicationId, ApplicationPersonalDetailDTO personalDetailDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        Institution institution = application.getInstitution();

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(personalDetail);
        }

        updateUserDetail(personalDetailDTO, userService.getCurrentUser(), application);

        Title title = importedEntityService.getById(Title.class, institution, personalDetailDTO.getTitle());
        Gender gender = importedEntityService.getById(Gender.class, institution, personalDetailDTO.getGender());
        Country country = importedEntityService.getById(Country.class, institution, personalDetailDTO.getCountry());
        Language firstNationality = importedEntityService.getById(Language.class, institution, personalDetailDTO.getFirstNationality());
        Language secondNationality = personalDetailDTO.getSecondNationality() != null ? importedEntityService.<Language> getById(Language.class, institution,
                personalDetailDTO.getSecondNationality()) : null;
        Domicile residenceCountry = importedEntityService.getById(Domicile.class, institution, personalDetailDTO.getDomicile());

        personalDetail.setTitle(title);
        personalDetail.setGender(gender);
        personalDetail.setDateOfBirth(personalDetailDTO.getDateOfBirth());
        personalDetail.setCountry(country);
        personalDetail.setFirstNationality(firstNationality);
        personalDetail.setSecondNationality(secondNationality);
        personalDetail.setFirstLanguageLocale(personalDetailDTO.getFirstLanguageLocale());
        personalDetail.setDomicile(residenceCountry);
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());
        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));

        Integer ethnicityId = personalDetailDTO.getEthnicity();
        if (ethnicityId != null) {
            Ethnicity ethnicity = importedEntityService.getById(Ethnicity.class, institution, ethnicityId);
            personalDetail.setEthnicity(ethnicity);
        }

        Integer disabilityId = personalDetailDTO.getDisability();
        if (disabilityId != null) {
            Disability disability = importedEntityService.getById(Disability.class, institution, personalDetailDTO.getDisability());
            personalDetail.setDisability(disability);
        }

        updateLanguageQualification(personalDetailDTO, institution, personalDetail);
        updatePassport(personalDetailDTO, personalDetail);
        personalDetail.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL);
    }

    public void updateAddress(Integer applicationId, ApplicationAddressDTO addressDTO) throws DeduplicationException, InstantiationException,
            IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
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
        address.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDRESS);
    }

    public ApplicationQualification updateQualification(Integer applicationId, Integer qualificationId, ApplicationQualificationDTO qualificationDTO)
            throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification;
        if (qualificationId == null) {
            qualification = new ApplicationQualification();
            application.getQualifications().add(qualification);
        } else {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        }

        Institution institution = application.getInstitution();
        ImportedInstitutionDTO importedInstitutionDTO = qualificationDTO.getInstitution();
        ImportedInstitution importedInstitution;
        if (importedInstitutionDTO.getId() == null) {
            Domicile domicile = importedEntityService.getById(Domicile.class, institution, importedInstitutionDTO.getDomicile());
            importedInstitution = new ImportedInstitution().withInstitution(institution).withDomicile(domicile).withEnabled(true).withCode("TEMPORARY_CODE")
                    .withName(importedInstitutionDTO.getName());
            entityService.save(importedInstitution);
            importedInstitution.setCode("CUSTOM_" + Strings.padStart(importedInstitution.getId().toString(), 10, '0'));
        } else {
            importedInstitution = importedEntityService.getById(ImportedInstitution.class, institution, importedInstitutionDTO.getId());
        }
        QualificationType qualificationType = importedEntityService.getById(QualificationType.class, institution, qualificationDTO.getType());

        qualification.setInstitution(importedInstitution);
        qualification.setType(qualificationType);
        qualification.setTitle(Strings.emptyToNull(qualificationDTO.getTitle()));
        qualification.setSubject(qualificationDTO.getSubject());
        qualification.setLanguage(qualificationDTO.getLanguage());
        qualification.setStartDate(qualificationDTO.getStartDate());
        qualification.setCompleted(BooleanUtils.isTrue(qualificationDTO.getCompleted()));
        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setAwardDate(qualificationDTO.getAwardDate());

        FileDTO upload = qualificationDTO.getDocument();
        if (upload != null) {
            Document qualificationDocument = documentService.getById(upload.getId(), FileCategory.DOCUMENT);
            qualification.setDocument(qualificationDocument);
        }
        qualification.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualification(Integer applicationId, Integer qualificationId) throws DeduplicationException, InstantiationException,
            IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class,
                ImmutableMap.of("application", application, "id", qualificationId));
        application.getQualifications().remove(qualification);

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public ApplicationEmploymentPosition updateEmploymentPosition(Integer applicationId, Integer employmentPositionId,
            ApplicationEmploymentPositionDTO employmentPositionDTO) throws DeduplicationException, InstantiationException, IllegalAccessException,
            BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

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
        employmentPosition.setCurrent(BooleanUtils.isTrue(employmentPositionDTO.getCurrent()));
        employmentPosition.setEndDate(employmentPositionDTO.getEndDate());
        employmentPosition.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
        return employmentPosition;
    }

    public void deleteEmploymentPosition(Integer applicationId, Integer employmentPositionId) throws DeduplicationException, InstantiationException,
            IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        ApplicationEmploymentPosition employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                ImmutableMap.of("application", application, "id", employmentPositionId));
        application.getEmploymentPositions().remove(employmentPosition);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
    }

    public ApplicationFunding updateFunding(Integer applicationId, Integer fundingId, ApplicationFundingDTO fundingDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationFunding funding;
        if (fundingId == null) {
            funding = new ApplicationFunding();
            application.getFundings().add(funding);
        } else {
            funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        }

        FundingSource fundingSource = importedEntityService.getById(FundingSource.class, application.getInstitution(), fundingDTO.getFundingSource());

        funding.setFundingSource(fundingSource);
        funding.setSponsor(fundingDTO.getSponsor());
        funding.setDescription(fundingDTO.getDescription());
        funding.setValue(fundingDTO.getValue());
        funding.setAwardDate(fundingDTO.getAwardDate());
        funding.setTerms(fundingDTO.getTerms());

        FileDTO fileDTO = fundingDTO.getDocument();
        if (fileDTO != null) {
            Document qualificationDocument = documentService.getById(fileDTO.getId(), FileCategory.DOCUMENT);
            funding.setDocument(qualificationDocument);
        }
        funding.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
        return funding;
    }

    public void deleteFunding(Integer applicationId, Integer fundingId) throws DeduplicationException, InstantiationException, IllegalAccessException,
            BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        ApplicationFunding funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        application.getFundings().remove(funding);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
    }

    public ApplicationPrize updatePrize(Integer applicationId, Integer prizeId, ApplicationPrizeDTO prizeDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationPrize prize;
        if (prizeId == null) {
            prize = new ApplicationPrize();
            application.getPrizes().add(prize);
        } else {
            prize = entityService.getByProperties(ApplicationPrize.class, ImmutableMap.of("application", application, "id", prizeId));
        }

        prize.setProvider(prizeDTO.getProvider());
        prize.setTitle(prizeDTO.getTitle());
        prize.setDescription(prizeDTO.getDescription());
        prize.setAwardDate(prizeDTO.getAwardDate());
        prize.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
        return prize;
    }

    public void deletePrize(Integer applicationId, Integer prizeId) throws DeduplicationException, InstantiationException, IllegalAccessException,
            BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        ApplicationPrize prize = entityService.getByProperties(ApplicationPrize.class, ImmutableMap.of("application", application, "id", prizeId));
        application.getPrizes().remove(prize);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
    }

    public ApplicationReferee updateReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationReferee referee;
        if (refereeId == null) {
            referee = new ApplicationReferee();
            application.getReferees().add(referee);
        } else {
            referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        }

        User oldUser = referee.getUser();
        AssignedUserDTO userDTO = refereeDTO.getUser();
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), application.getLocale());
        referee.setUser(newUser);

        referee.setRefereeType(refereeDTO.getRefereeType());

        referee.setJobEmployer(refereeDTO.getJobEmployer());
        referee.setJobTitle(refereeDTO.getJobTitle());

        AddressDTO addressDTO = refereeDTO.getAddress();
        Address address = referee.getAddress();

        if (address == null) {
            address = new Address();
            referee.setAddress(address);
        }
        copyAddress(application.getInstitution(), address, addressDTO);
        referee.setPhone(refereeDTO.getPhone());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));
        referee.setLastUpdatedTimestamp(DateTime.now());

        List<CommentAssignedUser> assignees = Lists.newArrayList();
        if (application.isSubmitted()) {
            assignees.add(new CommentAssignedUser().withUser(newUser).withRole(roleService.getById(APPLICATION_REFEREE)).withRoleTransitionType(CREATE));
            if (newUser.getId().equals(oldUser.getId())) {
                assignees.add(new CommentAssignedUser().withUser(oldUser).withRole(roleService.getById(APPLICATION_REFEREE)).withRoleTransitionType(DELETE));
            }
        }

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return referee;
    }

    public void deleteReferee(Integer applicationId, Integer refereeId) throws DeduplicationException, InstantiationException, IllegalAccessException,
            BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);
        ApplicationReferee referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        application.getReferees().remove(referee);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE);
    }

    public void updateDocument(Integer applicationId, ApplicationDocumentDTO documentDTO) throws DeduplicationException, InstantiationException,
            IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationDocument document = application.getDocument();
        if (document == null) {
            document = new ApplicationDocument();
            application.setDocument(document);
        }

        Document personalStatement = documentDTO.getPersonalStatement() != null ? documentService.getById(documentDTO.getPersonalStatement().getId(),
                FileCategory.DOCUMENT) : null;
        Document cv = documentDTO.getCv() != null ? documentService.getById(documentDTO.getCv().getId(), FileCategory.DOCUMENT) : null;
        Document researchStatement = documentDTO.getResearchStatement() != null ? documentService.getById(documentDTO.getResearchStatement().getId(),
                FileCategory.DOCUMENT) : null;
        Document coveringLetter = documentDTO.getCoveringLetter() != null ? documentService.getById(documentDTO.getCoveringLetter().getId(),
                FileCategory.DOCUMENT) : null;
        document.setCv(cv);
        document.setPersonalStatement(personalStatement);
        document.setResearchStatement(researchStatement);
        document.setCoveringLetter(coveringLetter);
        document.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformation(Integer applicationId, ApplicationAdditionalInformationDTO additionalInformationDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        Application application = applicationService.getById(applicationId);

        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new ApplicationAdditionalInformation();
            application.setAdditionalInformation(additionalInformation);
        }

        additionalInformation.setConvictionsText(Strings.emptyToNull(additionalInformationDTO.getConvictionsText()));
        additionalInformation.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION);
    }

    private void updateUserDetail(ApplicationPersonalDetailDTO personalDetailDTO, User userCurrent, Application application) {
        User userCreator = application.getUser();
        if (userCurrent.getId().equals(userCreator.getId())) {
            ApplicationPersonalDetailUserDTO userDTO = personalDetailDTO.getUser();

            userCurrent.setFirstName(userDTO.getFirstName());
            userCurrent.setLastName(userDTO.getLastName());
            userCurrent.setFullName(userCurrent.getFirstName() + " " + userCurrent.getLastName());
            userCurrent.setFirstName2(Strings.emptyToNull(userDTO.getFirstName2()));
            userCurrent.setFirstName3(Strings.emptyToNull(userDTO.getFirstName3()));
        }
    }

    private void updatePassport(ApplicationPersonalDetailDTO personalDetailDTO, ApplicationPersonalDetail personalDetail) {
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

    private void updateLanguageQualification(ApplicationPersonalDetailDTO personalDetailDTO, Institution institution, ApplicationPersonalDetail personalDetail) {
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
            languageQualification.setType(languageQualificationType);
            languageQualification.setExamDate(languageQualificationDTO.getExamDate());
            languageQualification.setOverallScore(languageQualificationDTO.getOverallScore());
            languageQualification.setReadingScore(languageQualificationDTO.getReadingScore());
            languageQualification.setWritingScore(languageQualificationDTO.getWritingScore());
            languageQualification.setSpeakingScore(languageQualificationDTO.getSpeakingScore());
            languageQualification.setListeningScore(languageQualificationDTO.getListeningScore());

            FileDTO upload = languageQualificationDTO.getDocument();
            if (upload != null) {
                Document languageQualificationDocument = documentService.getById(upload.getId(), FileCategory.DOCUMENT);
                languageQualification.setDocument(languageQualificationDocument);
            }
        }
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

    private void executeUpdate(Application application, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees)
            throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException {
        User userCurrent = userService.getCurrentUser();
        List<PrismActionEnhancement> userEnhancements = actionService.getPermittedActionEnhancements(application, userCurrent);
        List<PrismActionEnhancement> permittedEnhancements = Lists.newArrayList(APPLICATION_VIEW_EDIT_AS_CREATOR, APPLICATION_VIEW_EDIT_AS_ADMITTER);

        for (CommentAssignedUser assignee : assignees) {
            if (assignee.getRole().getId() == PrismRole.APPLICATION_REFEREE && assignee.getRoleTransitionType() == PrismRoleTransitionType.CREATE) {
                permittedEnhancements.add(APPLICATION_VIEW_EDIT_AS_RECRUITER);
            }
        }

        if (Collections.disjoint(userEnhancements, permittedEnhancements)) {
            Action action = actionService.getViewEditAction(application);
            actionService.throwWorkflowPermissionException(application, action);
        }

        if (application.isSubmitted()) {
            resourceService.executeUpdate(application, messageIndex, assignees);
        }
    }

}
