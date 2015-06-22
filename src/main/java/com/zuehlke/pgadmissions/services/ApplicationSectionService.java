package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_EMPLOYMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_STUDY_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationAssignmentSection;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationPrize;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationStudyDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedCountry;
import com.zuehlke.pgadmissions.domain.imported.ImportedDisability;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEthnicity;
import com.zuehlke.pgadmissions.domain.imported.ImportedFundingSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedGender;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedNationality;
import com.zuehlke.pgadmissions.domain.imported.ImportedOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedReferralSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedStudyOption;
import com.zuehlke.pgadmissions.domain.imported.ImportedTitle;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationLanguageQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPassportDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailUserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPrizeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationStudyDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramDTO;

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

    public void updateProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        Institution institution = application.getInstitution();

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
            application.setProgramDetail(programDetail);
        }

        application.setPreviousApplication(programDetailDTO.getPreviousApplication());

        PrismOpportunityType prismOpportunityType = programDetailDTO.getOpportunityType();
        if (prismOpportunityType == null) {
            ResourceOpportunity parent = (ResourceOpportunity) application.getParentResource();
            programDetail.setOpportunityType(parent.getOpportunityType());
        } else {
            ImportedOpportunityType opportunityType = importedEntityService.getByName(ImportedOpportunityType.class, prismOpportunityType.name());
            programDetail.setOpportunityType(opportunityType);
        }

        ImportedStudyOption studyOption = importedEntityService.getByName(ImportedStudyOption.class, programDetailDTO.getStudyOption().name());
        ImportedReferralSource referralSource = importedEntityService.getById(institution, ImportedReferralSource.class, programDetailDTO.getReferralSource());
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

    public void updateStudyDetail(Integer applicationId, ApplicationStudyDetailDTO studyDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        application.setStudyDetail(new ApplicationStudyDetail().withStudyLocation(studyDetailDTO.getStudyLocation())
                .withStudyDivision(studyDetailDTO.getStudyDivision()).withStudyArea(studyDetailDTO.getStudyArea())
                .withStudyApplicationId(studyDetailDTO.getStudyApplicationId()).withStudyStartDate(studyDetailDTO.getStudyStartDate()));
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_STUDY_DETAIL);
    }

    public ApplicationSupervisor updateSupervisor(Integer applicationId, Integer supervisorId, ApplicationSupervisorDTO supervisorDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationSupervisor supervisor;
        if (supervisorId == null) {
            supervisor = new ApplicationSupervisor();
            application.getSupervisors().add(supervisor);
        } else {
            supervisor = entityService.getByProperties(ApplicationSupervisor.class, ImmutableMap.of("application", application, "id", supervisorId));
        }

        AssignedUserDTO userDTO = supervisorDTO.getUser();
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());

        supervisor.setUser(newUser);
        supervisor.setAcceptedSupervision(supervisorDTO.getAcceptedSupervision());
        supervisor.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_SUPERVISOR);
        return supervisor;
    }

    public void deleteSupervisor(Integer applicationId, Integer supervisorId) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationSupervisor supervisor = entityService.getByProperties(ApplicationSupervisor.class,
                ImmutableMap.of("application", application, "id", supervisorId));
        application.getSupervisors().remove(supervisor);

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_SUPERVISOR);
    }

    public void updatePersonalDetail(Integer applicationId, ApplicationPersonalDetailDTO personalDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        Institution institution = application.getInstitution();

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(personalDetail);
        }

        updateUserDetail(personalDetailDTO, userService.getCurrentUser(), application);

        ImportedTitle title = importedEntityService.getById(institution, ImportedTitle.class, personalDetailDTO.getTitle());
        ImportedGender gender = importedEntityService.getById(institution, ImportedGender.class, personalDetailDTO.getGender());
        ImportedCountry country = importedEntityService.getById(institution, ImportedCountry.class, personalDetailDTO.getCountry());
        ImportedNationality firstNationality = importedEntityService.getById(institution, ImportedNationality.class, personalDetailDTO.getFirstNationality());
        ImportedNationality secondNationality = personalDetailDTO.getSecondNationality() != null ? importedEntityService.<ImportedNationality> getById(
                institution, ImportedNationality.class,
                personalDetailDTO.getSecondNationality()) : null;
        ImportedDomicile residenceCountry = importedEntityService.getById(institution, ImportedDomicile.class, personalDetailDTO.getDomicile());

        personalDetail.setTitle(title);
        personalDetail.setGender(gender);

        LocalDate dateOfBirth = personalDetailDTO.getDateOfBirth();
        personalDetail.setDateOfBirth(dateOfBirth);

        Integer ageAtCreation = application.getCreatedTimestamp().getYear() - dateOfBirth.getYear();
        ImportedAgeRange ageRange = importedEntityService.getAgeRange(institution, ageAtCreation);
        personalDetail.setAgeRange(ageRange);

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
            ImportedEthnicity ethnicity = importedEntityService.getById(institution, ImportedEthnicity.class, ethnicityId);
            personalDetail.setEthnicity(ethnicity);
        }

        Integer disabilityId = personalDetailDTO.getDisability();
        if (disabilityId != null) {
            ImportedDisability disability = importedEntityService.getById(institution, ImportedDisability.class, personalDetailDTO.getDisability());
            personalDetail.setDisability(disability);
        }

        updateLanguageQualification(personalDetailDTO, institution, personalDetail);
        updatePassport(personalDetailDTO, personalDetail);
        personalDetail.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL);
    }

    public void updateAddress(Integer applicationId, ApplicationAddressDTO addressDTO) throws Exception {
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
            throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification;
        if (qualificationId == null) {
            qualification = new ApplicationQualification();
            application.getQualifications().add(qualification);
        } else {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        }

        Institution institution = application.getInstitution();

        ImportedProgramDTO importedProgramDTO = qualificationDTO.getProgram();
        ImportedProgram importedProgram = importedEntityService.getOrCreateImportedProgram(institution, importedProgramDTO);
        qualification.setProgram(importedProgram);

        qualification.setLanguage(qualificationDTO.getLanguage());
        qualification.setStartDate(qualificationDTO.getStartDate());
        qualification.setCompleted(BooleanUtils.isTrue(qualificationDTO.getCompleted()));
        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setAwardDate(qualificationDTO.getAwardDate());

        FileDTO upload = qualificationDTO.getDocument();
        if (upload != null) {
            Document qualificationDocument = documentService.getById(upload.getId(), PrismFileCategory.DOCUMENT);
            qualification.setDocument(qualificationDocument);
        }
        qualification.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualification(Integer applicationId, Integer qualificationId) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class,
                ImmutableMap.of("application", application, "id", qualificationId));
        application.getQualifications().remove(qualification);

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public ApplicationEmploymentPosition updateEmploymentPosition(Integer applicationId, Integer employmentPositionId,
            ApplicationEmploymentPositionDTO employmentPositionDTO) throws Exception {
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

    public void deleteEmploymentPosition(Integer applicationId, Integer employmentPositionId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationEmploymentPosition employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                ImmutableMap.of("application", application, "id", employmentPositionId));
        application.getEmploymentPositions().remove(employmentPosition);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
    }

    public ApplicationFunding updateFunding(Integer applicationId, Integer fundingId, ApplicationFundingDTO fundingDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationFunding funding;
        if (fundingId == null) {
            funding = new ApplicationFunding();
            application.getFundings().add(funding);
        } else {
            funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        }

        ImportedFundingSource fundingSource = importedEntityService.getById(application.getInstitution(), ImportedFundingSource.class,
                fundingDTO.getFundingSource());

        funding.setFundingSource(fundingSource);
        funding.setSponsor(fundingDTO.getSponsor());
        funding.setDescription(fundingDTO.getDescription());
        funding.setValue(fundingDTO.getValue());
        funding.setAwardDate(fundingDTO.getAwardDate());
        funding.setTerms(fundingDTO.getTerms());

        FileDTO fileDTO = fundingDTO.getDocument();
        if (fileDTO != null) {
            Document qualificationDocument = documentService.getById(fileDTO.getId(), PrismFileCategory.DOCUMENT);
            funding.setDocument(qualificationDocument);
        }
        funding.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
        return funding;
    }

    public void deleteFunding(Integer applicationId, Integer fundingId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationFunding funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        application.getFundings().remove(funding);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
    }

    public ApplicationPrize updatePrize(Integer applicationId, Integer prizeId, ApplicationPrizeDTO prizeDTO) throws Exception {
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

    public void deletePrize(Integer applicationId, Integer prizeId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationPrize prize = entityService.getByProperties(ApplicationPrize.class, ImmutableMap.of("application", application, "id", prizeId));
        application.getPrizes().remove(prize);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_FUNDING);
    }

    public ApplicationReferee updateReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) throws Exception {
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
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
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

        List<CommentAssignedUser> assignees = getUserAssignmentsCreate(application, oldUser, newUser, APPLICATION_REFEREE);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return referee;
    }

    public void deleteReferee(Integer applicationId, Integer refereeId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationReferee referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        application.getReferees().remove(referee);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, getUserAssignmentDelete(referee.getUser(), APPLICATION_REFEREE));
    }

    public void updateDocument(Integer applicationId, ApplicationDocumentDTO documentDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationDocument document = application.getDocument();
        if (document == null) {
            document = new ApplicationDocument();
            application.setDocument(document);
        }

        Document personalStatement = documentDTO.getPersonalStatement() != null ? documentService.getById(documentDTO.getPersonalStatement().getId(),
                PrismFileCategory.DOCUMENT) : null;
        Document cv = documentDTO.getCv() != null ? documentService.getById(documentDTO.getCv().getId(), PrismFileCategory.DOCUMENT) : null;
        Document researchStatement = documentDTO.getResearchStatement() != null ? documentService.getById(documentDTO.getResearchStatement().getId(),
                PrismFileCategory.DOCUMENT) : null;
        Document coveringLetter = documentDTO.getCoveringLetter() != null ? documentService.getById(documentDTO.getCoveringLetter().getId(),
                PrismFileCategory.DOCUMENT) : null;
        document.setCv(cv);
        document.setPersonalStatement(personalStatement);
        document.setResearchStatement(researchStatement);
        document.setCoveringLetter(coveringLetter);
        document.setLastUpdatedTimestamp(DateTime.now());

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformation(Integer applicationId, ApplicationAdditionalInformationDTO additionalInformationDTO) throws Exception {
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

    public void reassignApplicationSections(User oldUser, User newUser) {
        for (Application application : oldUser.getApplications()) {
            reassigApplicationSections(application.getSupervisors(), oldUser, newUser);
            reassigApplicationSections(application.getReferees(), oldUser, newUser);
        }
    }

    private <T extends ApplicationAssignmentSection> void reassigApplicationSections(Set<T> assignmentSections, User oldUser, User newUser) {
        for (T assignmentSection : assignmentSections) {
            assignmentSection.setUser(newUser);
            ApplicationAssignmentSection duplicateAssignmentSection = entityService.getDuplicateEntity(assignmentSection);
            if (duplicateAssignmentSection != null) {
                assignmentSection.setUser(oldUser);
                entityService.delete(assignmentSection);
            }
        }
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

            ImportedLanguageQualificationType languageQualificationType = importedEntityService.getById(institution, ImportedLanguageQualificationType.class,
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
                Document languageQualificationDocument = documentService.getById(upload.getId(), PrismFileCategory.DOCUMENT);
                languageQualification.setDocument(languageQualificationDocument);
            }
        }
    }

    private void copyAddress(Institution institution, Address to, AddressDTO from) {
        ImportedDomicile currentAddressDomicile = importedEntityService.getById(institution, ImportedDomicile.class, from.getDomicile());
        to.setDomicile(currentAddressDomicile);
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(Strings.emptyToNull(from.getAddressLine2()));
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(Strings.emptyToNull(from.getAddressRegion()));
        to.setAddressCode(Strings.emptyToNull(from.getAddressCode()));
    }

    private void executeUpdate(Application application, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) throws Exception {
        User userCurrent = userService.getCurrentUser();
        List<PrismActionEnhancement> userEnhancements = actionService.getPermittedActionEnhancements(application, userCurrent);
        List<PrismActionEnhancement> permittedEnhancements = Lists.newArrayList(APPLICATION_VIEW_EDIT_AS_CREATOR, APPLICATION_VIEW_EDIT_AS_ADMITTER);

        if (Collections.disjoint(userEnhancements, permittedEnhancements)) {
            Action action = actionService.getViewEditAction(application);
            throw new WorkflowPermissionException(application, action);
        }

        if (application.isSubmitted()) {
            resourceService.executeUpdate(application, messageIndex, assignees);
        }
    }

    private List<CommentAssignedUser> getUserAssignmentsCreate(Application application, User oldUser, User newUser, PrismRole roleId) {
        List<CommentAssignedUser> assignees = Lists.newArrayList();
        if (application.isSubmitted()) {
            Role role = roleService.getById(roleId);
            if (oldUser == null) {
                assignees.add(new CommentAssignedUser().withUser(newUser).withRole(role).withRoleTransitionType(CREATE));
            } else if (!Objects.equal(newUser.getId(), oldUser.getId())) {
                assignees.add(new CommentAssignedUser().withUser(newUser).withRole(role).withRoleTransitionType(CREATE));
                assignees.add(new CommentAssignedUser().withUser(oldUser).withRole(role).withRoleTransitionType(DELETE));
            }
        }
        return assignees;
    }

    private CommentAssignedUser getUserAssignmentDelete(User user, PrismRole roleId) {
        Role role = roleService.getById(roleId);
        return new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(DELETE);
    }

}
