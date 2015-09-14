package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_EMPLOYMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PRIZE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.AddressApplication;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDemographic;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationPrize;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDemographicDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailUserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPrizeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

@Service
@Transactional
public class ApplicationSectionService {

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private DocumentService documentService;

    public void updateProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
        }

        application.setPreviousApplication(programDetailDTO.getPreviousApplication());

        PrismOpportunityType prismOpportunityType = programDetailDTO.getOpportunityType();
        if (prismOpportunityType == null) {
            ResourceParent parent = application.getParentResource();
            if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
                ImportedEntitySimple opportunityType = ((ResourceOpportunity) parent).getOpportunityType();
                setOpportunityType(application, programDetail, opportunityType);
            }
        } else {
            ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class, prismOpportunityType.name());
            setOpportunityType(application, programDetail, opportunityType);
        }

        ImportedEntitySimple studyOption = importedEntityService.getById(ImportedEntitySimple.class, programDetailDTO.getStudyOption().getId());
        programDetail.setStudyOption(studyOption);
        programDetail.setStartDate(programDetailDTO.getStartDate());
        programDetail.setLastUpdatedTimestamp(DateTime.now());

        application.setProgramDetail(programDetail);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
    }

    public void updatePersonalDetail(Integer applicationId, ApplicationPersonalDetailDTO personalDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        Institution institution = application.getInstitution();

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = new ApplicationPersonalDetail();
        }

        updateUserDetail(personalDetailDTO, userService.getCurrentUser(), application);

        ImportedEntitySimple title = personalDetailDTO.getTitle() != null ? importedEntityService.getById(ImportedEntitySimple.class, personalDetailDTO
                .getTitle().getId()) : null;
        ImportedEntitySimple gender = importedEntityService.getById(ImportedEntitySimple.class, personalDetailDTO.getGender().getId());
        ImportedEntitySimple country = personalDetailDTO.getCountry() != null ? importedEntityService.getById(ImportedEntitySimple.class, personalDetailDTO
                .getCountry().getId()) : null;
        ImportedEntitySimple firstNationality = importedEntityService.getById(ImportedEntitySimple.class, personalDetailDTO.getFirstNationality().getId());
        ImportedEntitySimple residenceCountry = personalDetailDTO.getDomicile() != null ? importedEntityService.getById(ImportedEntitySimple.class,
                personalDetailDTO.getDomicile().getId()) : null;

        personalDetail.setTitle(title);
        personalDetail.setGender(gender);

        LocalDate dateOfBirth = personalDetailDTO.getDateOfBirth();
        personalDetail.setDateOfBirth(dateOfBirth);

        Integer ageAtCreation = application.getCreatedTimestamp().getYear() - dateOfBirth.getYear();
        ImportedAgeRange ageRange = importedEntityService.getAgeRange(institution, ageAtCreation);
        personalDetail.setAgeRange(ageRange);

        personalDetail.setCountry(country);
        personalDetail.setFirstNationality(firstNationality);
        personalDetail.setFirstLanguageLocale(personalDetailDTO.getFirstLanguageLocale());
        personalDetail.setDomicile(residenceCountry);
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());
        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));

        ApplicationDemographicDTO demographicDTO = personalDetailDTO.getDemographic();
        ApplicationDemographic demographic = new ApplicationDemographic();
        if (demographicDTO != null) {
            demographic.setEthnicity(demographicDTO.getEthnicity() != null ? importedEntityService.getById(ImportedEntitySimple.class, demographicDTO
                    .getEthnicity().getId()) : null);
            demographic.setDisability(demographicDTO.getDisability() != null ? importedEntityService.getById(ImportedEntitySimple.class, demographicDTO
                    .getDisability().getId()) : null);
        }
        personalDetail.setDemographic(demographic);

        personalDetail.setLastUpdatedTimestamp(DateTime.now());
        application.setPersonalDetail(personalDetail);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL);
    }

    public void updateAddress(Integer applicationId, ApplicationAddressDTO addressDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationAddress address = application.getAddress();
        if (address == null) {
            address = new ApplicationAddress();
        }

        AddressApplicationDTO currentAddressDTO = addressDTO.getCurrentAddress();
        AddressApplication currentAddress = address.getCurrentAddress();
        if (currentAddress == null) {
            currentAddress = new AddressApplication();
            address.setCurrentAddress(currentAddress);
        }
        copyAddress(currentAddress, currentAddressDTO);

        AddressApplicationDTO contactAddressDTO = addressDTO.getContactAddress();
        AddressApplication contactAddress = address.getContactAddress();
        if (contactAddress == null) {
            contactAddress = new AddressApplication();
            address.setContactAddress(contactAddress);
        }
        copyAddress(contactAddress, contactAddressDTO);
        address.setLastUpdatedTimestamp(DateTime.now());

        application.setAddress(address);

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDRESS);
    }

    public ApplicationQualification updateQualification(Integer applicationId, Integer qualificationId, ApplicationQualificationDTO qualificationDTO)
            throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification;
        if (qualificationId == null) {
            qualification = new ApplicationQualification();
        } else {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        }

        // FIXME - implement all of the possible scenarios (e.g. suggested user,
        // no known department, etc)
        ResourceOpportunityDTO programDTO = qualificationDTO.getProgram();
        Program newProgram = (Program) resourceService.createResource(systemService.getSystem().getUser(), actionService.getById(DEPARTMENT_CREATE_PROGRAM), programDTO)
                .getResource();

        User user = application.getUser();
        Program oldProgram = qualification.getProgram();
        if (oldProgram != null && !oldProgram.getId().equals(newProgram.getId())) {
            userService.deleteUserProgram(user, oldProgram);
        }

        qualification.setProgram(newProgram);
        userService.createOrUpdateUserProgram(user, qualification.getProgram());

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

        if (qualificationId == null) {
            application.getQualifications().add(qualification);
        }

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualification(Integer applicationId, Integer qualificationId) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class,
                ImmutableMap.of("application", application, "id", qualificationId));
        Program program = qualification.getProgram();

        application.getQualifications().remove(qualification);
        entityService.flush();

        User user = application.getUser();
        if (userService.getUserProgramRelationCount(user, program).equals(0)) {
            userService.deleteUserProgram(user, program);
        }

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public ApplicationEmploymentPosition updateEmploymentPosition(Integer applicationId, Integer employmentPositionId,
            ApplicationEmploymentPositionDTO employmentPositionDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationEmploymentPosition employmentPosition;
        if (employmentPositionId == null) {
            employmentPosition = new ApplicationEmploymentPosition();
        } else {
            employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                    ImmutableMap.of("application", application, "id", employmentPositionId));
        }

        employmentPosition.setEmployerName(employmentPositionDTO.getEmployerName());

        AddressApplicationDTO employerAddressDTO = employmentPositionDTO.getEmployerAddress();
        AddressApplication employerAddress = employmentPosition.getEmployerAddress();
        if (employerAddress == null) {
            employerAddress = new AddressApplication();
            employmentPosition.setEmployerAddress(employerAddress);
        }
        copyAddress(employerAddress, employerAddressDTO);

        employmentPosition.setPosition(employmentPositionDTO.getPosition());
        employmentPosition.setRemit(employmentPositionDTO.getRemit());
        employmentPosition.setStartDate(employmentPositionDTO.getStartDate());
        employmentPosition.setCurrent(BooleanUtils.isTrue(employmentPositionDTO.getCurrent()));
        employmentPosition.setEndDate(employmentPositionDTO.getEndDate());
        employmentPosition.setLastUpdatedTimestamp(DateTime.now());

        if (employmentPositionId == null) {
            application.getEmploymentPositions().add(employmentPosition);
        }

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

    public ApplicationPrize updatePrize(Integer applicationId, Integer prizeId, ApplicationPrizeDTO prizeDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationPrize prize;
        if (prizeId == null) {
            prize = new ApplicationPrize();
        } else {
            prize = entityService.getByProperties(ApplicationPrize.class, ImmutableMap.of("application", application, "id", prizeId));
        }

        prize.setProvider(prizeDTO.getProvider());
        prize.setTitle(prizeDTO.getTitle());
        prize.setDescription(prizeDTO.getDescription());
        prize.setAwardDate(prizeDTO.getAwardDate());
        prize.setLastUpdatedTimestamp(DateTime.now());

        if (prizeId == null) {
            application.getPrizes().add(prize);
        }

        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PRIZE);
        return prize;
    }

    public void deletePrize(Integer applicationId, Integer prizeId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationPrize prize = entityService.getByProperties(ApplicationPrize.class, ImmutableMap.of("application", application, "id", prizeId));
        application.getPrizes().remove(prize);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PRIZE);
    }

    public ApplicationReferee updateReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationReferee referee;
        if (refereeId == null) {
            referee = new ApplicationReferee();
        } else {
            referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        }

        User oldUser = referee.getUser();
        UserDTO userDTO = refereeDTO.getUser();
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        referee.setUser(newUser);

        referee.setRefereeType(refereeDTO.getRefereeType());

        referee.setJobEmployer(refereeDTO.getJobEmployer());
        referee.setJobTitle(refereeDTO.getJobTitle());

        AddressApplicationDTO addressApplicationDTO = refereeDTO.getAddress();
        AddressApplication addressApplication = referee.getAddress();

        if (addressApplication == null) {
            addressApplication = new AddressApplication();
            referee.setAddress(addressApplication);
        }

        copyAddress(addressApplication, addressApplicationDTO);
        referee.setPhone(refereeDTO.getPhone());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));
        referee.setLastUpdatedTimestamp(DateTime.now());

        if (refereeId == null) {
            application.getReferees().add(referee);
        }

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
        }

        Document cv = documentDTO.getCv() != null ? documentService.getById(documentDTO.getCv().getId(), PrismFileCategory.DOCUMENT) : null;
        Document coveringLetter = documentDTO.getCoveringLetter() != null ? documentService.getById(documentDTO.getCoveringLetter().getId(),
                PrismFileCategory.DOCUMENT) : null;
        document.setCv(cv);
        document.setCoveringLetter(coveringLetter);
        document.setLastUpdatedTimestamp(DateTime.now());

        application.setDocument(document);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformation(Integer applicationId, ApplicationAdditionalInformationDTO additionalInformationDTO) throws Exception {
        Application application = applicationService.getById(applicationId);

        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new ApplicationAdditionalInformation();
        }

        additionalInformation.setConvictionsText(Strings.emptyToNull(additionalInformationDTO.getConvictionsText()));
        additionalInformation.setLastUpdatedTimestamp(DateTime.now());

        application.setAdditionalInformation(additionalInformation);
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

    private void copyAddress(AddressApplication to, AddressApplicationDTO from) {
        ImportedEntitySimple currentAddressDomicile = importedEntityService.getById(ImportedEntitySimple.class, from.getDomicile().getId());
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
        List<PrismActionEnhancement> permittedEnhancements = Lists.newArrayList(APPLICATION_VIEW_EDIT_AS_CREATOR, APPLICATION_VIEW_EDIT_AS_APPROVER);

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

    private void setOpportunityType(Application application, ApplicationProgramDetail programDetail, ImportedEntitySimple opportunityType) {
        programDetail.setOpportunityType(opportunityType);
        application.setOpportunityCategories(PrismOpportunityType.valueOf(opportunityType.getName()).getCategory().name());
    }

}
