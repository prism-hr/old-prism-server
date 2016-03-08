package uk.co.alumeni.prism.services;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.joda.time.DateTime.now;
import static org.springframework.beans.BeanUtils.instantiate;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_AWARD;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_DOCUMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_EMPLOYMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_QUALIFICATION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDITIONAL_INFORMATION_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDRESS_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_AWARD_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_DOCUMENT_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_UPDATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static uk.co.alumeni.prism.domain.document.PrismFileCategory.DOCUMENT;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.ProfileDAO;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.UniqueEntity.EntitySignature;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationAdditionalInformation;
import uk.co.alumeni.prism.domain.application.ApplicationAddress;
import uk.co.alumeni.prism.domain.application.ApplicationAward;
import uk.co.alumeni.prism.domain.application.ApplicationDocument;
import uk.co.alumeni.prism.domain.application.ApplicationEmploymentPosition;
import uk.co.alumeni.prism.domain.application.ApplicationPersonalDetail;
import uk.co.alumeni.prism.domain.application.ApplicationQualification;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileAdditionalInformation;
import uk.co.alumeni.prism.domain.profile.ProfileAddress;
import uk.co.alumeni.prism.domain.profile.ProfileAdvertRelationSection;
import uk.co.alumeni.prism.domain.profile.ProfileAward;
import uk.co.alumeni.prism.domain.profile.ProfileDocument;
import uk.co.alumeni.prism.domain.profile.ProfileEmploymentPosition;
import uk.co.alumeni.prism.domain.profile.ProfileEntity;
import uk.co.alumeni.prism.domain.profile.ProfilePersonalDetail;
import uk.co.alumeni.prism.domain.profile.ProfileQualification;
import uk.co.alumeni.prism.domain.profile.ProfileReferee;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAdditionalInformation;
import uk.co.alumeni.prism.domain.user.UserAddress;
import uk.co.alumeni.prism.domain.user.UserAward;
import uk.co.alumeni.prism.domain.user.UserDocument;
import uk.co.alumeni.prism.domain.user.UserEmploymentPosition;
import uk.co.alumeni.prism.domain.user.UserPersonalDetail;
import uk.co.alumeni.prism.domain.user.UserQualification;
import uk.co.alumeni.prism.domain.user.UserReferee;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileAdditionalInformationDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileAddressDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileAwardDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileDocumentDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileEmploymentPositionDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfilePersonalDetailDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileQualificationDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileRefereeDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Service
@Transactional
public class ProfileService {

    @Inject
    private ProfileDAO profileDAO;

    @Inject
    private AddressService addressService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Inject
    private DocumentService documentService;

    @Inject
    private PrismService prismService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private UserService userService;

    public void fillApplication(Application application) {
        UserAccount userAccount = application.getUser().getUserAccount();
        fillApplicationPersonalDetail(application, userAccount);
        fillApplicationAddress(application, userAccount);
        fillApplicationQualifications(application, userAccount);
        fillApplicationAwards(application, userAccount);
        fillApplicationEmploymentPositions(application, userAccount);
        fillApplicationReferees(application, userAccount);
        fillApplicationDocument(application, userAccount);
        fillApplicationAdditionalInformation(application, userAccount);
    }

    public void updatePersonalDetailUser(ProfilePersonalDetailDTO personalDetailDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserPersonalDetail personalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);
        userAccount.setPersonalDetail(personalDetail);
        userAccountService.updateUserAccount(userAccount, PROFILE_PERSONAL_DETAIL_UPDATE);
    }

    public void updatePersonalDetailApplication(Integer applicationId, ProfilePersonalDetailDTO personalDetailDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationPersonalDetail applicationPersonalDetail = updatePersonalDetail(application, ApplicationPersonalDetail.class, personalDetailDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserPersonalDetail userPersonalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);

        LocalDate dateOfBirth = userPersonalDetail.getDateOfBirth();
        if (dateOfBirth != null) {
            applicationPersonalDetail.setAgeRange(prismService.getAgeRangeFromAge(application.getCreatedTimestamp().getYear() - dateOfBirth.getYear()));
        }

        userAccount.setPersonalDetail(userPersonalDetail);
        userAccountService.updateUserAccount(userAccount, PROFILE_PERSONAL_DETAIL_UPDATE);

        applicationPersonalDetail.setLastUpdatedTimestamp(DateTime.now());
        application.setPersonalDetail(applicationPersonalDetail);
    }

    public void updateAddressUser(ProfileAddressDTO addressDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserAddress address = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(address);
        userAccountService.updateUserAccount(userAccount, PROFILE_ADDRESS_UPDATE);
    }

    public void updateAddressApplication(Integer applicationId, ProfileAddressDTO addressDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationAddress address = updateAddress(application, ApplicationAddress.class, addressDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserAddress userAddress = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(userAddress);
        userAccountService.updateUserAccount(userAccount, PROFILE_ADDRESS_UPDATE);

        address.setLastUpdatedTimestamp(DateTime.now());
        application.setAddress(address);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDRESS);
    }

    public UserQualification updateQualificationUser(Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserQualification userQualification = updateQualification(userAccount, UserQualification.class, qualificationId, qualificationDTO);
        userAccountService.updateUserAccount(userAccount, PROFILE_QUALIFICATION_UPDATE);
        return userQualification;
    }

    public ApplicationQualification updateQualificationApplication(Integer applicationId, Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationQualification qualification = updateQualification(application, ApplicationQualification.class, qualificationId, qualificationDTO);
        qualificationDTO.setDocument(cloneDocument(qualificationDTO.getDocument()));

        UserAccount userAccount = application.getUser().getUserAccount();
        profileDAO.deleteUserProfileSection(UserQualification.class, ApplicationQualification.class, qualificationId);
        UserQualification userQualification = entityService.getDuplicateEntity(UserQualification.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("advert", qualification.getAdvert()).addProperty("startYear",
                        qualification.getStartYear()));

        userQualification = userQualification == null ? new UserQualification() : userQualification;
        updateQualification(userAccount, userQualification, qualificationDTO);
        userQualification.setApplicationQualification(qualification);

        userAccountService.updateUserAccount(userAccount, PROFILE_QUALIFICATION_UPDATE);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualificationUser(Integer qualificationId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteQualification(userAccount, UserQualification.class, qualificationId);
        userAccountService.updateUserAccount(userAccount, PROFILE_QUALIFICATION_UPDATE);
    }

    public void deleteQualificationApplication(Integer applicationId, Integer qualificationId) {
        Application application = applicationService.getById(applicationId);
        if (profileDAO.deleteUserProfileSection(UserQualification.class, ApplicationQualification.class, qualificationId)) {
            userAccountService.updateUserAccount(userService.getCurrentUser().getUserAccount(), PROFILE_QUALIFICATION_UPDATE);
        }

        deleteQualification(application, ApplicationQualification.class, qualificationId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public UserAward updateAwardUser(Integer awardId, ProfileAwardDTO awardDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserAward userQualification = updateAward(userAccount, UserAward.class, awardId, awardDTO);
        userAccountService.updateUserAccount(userAccount, PROFILE_QUALIFICATION_UPDATE);
        return userQualification;
    }

    public ApplicationAward updateAwardApplication(Integer applicationId, Integer awardId, ProfileAwardDTO awardDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationAward award = updateAward(application, ApplicationAward.class, awardId, awardDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        profileDAO.deleteUserProfileSection(UserQualification.class, ApplicationQualification.class, awardId);
        UserAward userAward = entityService.getDuplicateEntity(UserAward.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("name", award.getName()).addProperty("awardYear",
                        award.getAwardYear()).addProperty("awardMonth", award.getAwardMonth()));

        userAward = userAward == null ? new UserAward() : userAward;
        updateAward(userAccount, userAward, awardDTO);
        userAward.setApplicationAward(award);

        userAccountService.updateUserAccount(userAccount, PROFILE_QUALIFICATION_UPDATE);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return award;
    }

    public void deleteAwardUser(Integer awardId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteAward(userAccount, UserAward.class, awardId);
        userAccountService.updateUserAccount(userAccount, PROFILE_AWARD_UPDATE);
    }

    public void deleteAwardApplication(Integer applicationId, Integer awardId) {
        Application application = applicationService.getById(applicationId);
        if (profileDAO.deleteUserProfileSection(UserAward.class, ApplicationAward.class, awardId)) {
            userAccountService.updateUserAccount(userService.getCurrentUser().getUserAccount(), PROFILE_QUALIFICATION_UPDATE);
        }

        deleteAward(application, ApplicationAward.class, awardId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_AWARD);
    }

    public UserEmploymentPosition updateEmploymentPositionUser(Integer employmentPositionId, ProfileEmploymentPositionDTO employmentPositionDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserEmploymentPosition employmentPosition = updateEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId,
                employmentPositionDTO);
        userAccountService.updateUserAccount(userAccount, PROFILE_EMPLOYMENT_POSITION_UPDATE);
        return employmentPosition;
    }

    public ApplicationEmploymentPosition updateEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId,
            ProfileEmploymentPositionDTO employmentPositionDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationEmploymentPosition employmentPosition = updateEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId,
                employmentPositionDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        profileDAO.deleteUserProfileSection(UserEmploymentPosition.class, ApplicationEmploymentPosition.class, employmentPositionId);
        UserEmploymentPosition userEmploymentPosition = entityService.getDuplicateEntity(UserEmploymentPosition.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("advert", employmentPosition.getAdvert()).addProperty("startYear",
                        employmentPosition.getStartYear()));

        userEmploymentPosition = userEmploymentPosition == null ? new UserEmploymentPosition() : userEmploymentPosition;
        updateEmploymentPosition(userAccount, userEmploymentPosition, employmentPositionDTO);
        userEmploymentPosition.setApplicationEmploymentPosition(employmentPosition);

        userAccountService.updateUserAccount(userAccount, PROFILE_EMPLOYMENT_POSITION_UPDATE);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
        return employmentPosition;
    }

    public void deleteEmploymentPositionUser(Integer employmentPositionId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId);
        userAccountService.updateUserAccount(userAccount, PROFILE_EMPLOYMENT_POSITION_UPDATE);
    }

    public void deleteEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId) {
        Application application = applicationService.getById(applicationId);
        if (profileDAO.deleteUserProfileSection(UserEmploymentPosition.class, ApplicationEmploymentPosition.class, employmentPositionId)) {
            userAccountService.updateUserAccount(userService.getCurrentUser().getUserAccount(), PROFILE_EMPLOYMENT_POSITION_UPDATE);
        }

        deleteEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
    }

    public UserReferee updateRefereeUser(Integer refereeId, ProfileRefereeDTO refereeDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserReferee userReferee = (UserReferee) updateReferee(userAccount, UserReferee.class, refereeId, refereeDTO).getReferee();
        userAccountService.updateUserAccount(userAccount, PROFILE_REFEREE_UPDATE);
        return userReferee;
    }

    public ApplicationReferee updateRefereeApplication(Integer applicationId, Integer refereeId, ProfileRefereeDTO refereeDTO) {
        Application application = applicationService.getById(applicationId);
        ProfileRefereeUpdateDTO refereeUpdateDTO = updateReferee(application, ApplicationReferee.class, refereeId, refereeDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        profileDAO.deleteUserProfileSection(UserReferee.class, ApplicationReferee.class, refereeId);
        UserReferee userReferee = entityService.getDuplicateEntity(UserReferee.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("user", refereeUpdateDTO.getReferee().getUser()));

        userReferee = userReferee == null ? new UserReferee() : userReferee;
        updateReferee(userAccount, userReferee, refereeDTO);
        ApplicationReferee referee = (ApplicationReferee) refereeUpdateDTO.getReferee();
        userReferee.setApplicationReferee(referee);

        userAccountService.updateUserAccount(userAccount, PROFILE_REFEREE_UPDATE);
        List<CommentAssignedUser> assignees = refereeUpdateDTO.getAssignments();
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return (ApplicationReferee) refereeUpdateDTO.getReferee();
    }

    public void deleteRefereeUser(Integer refereeId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteReferee(userAccount, UserReferee.class, refereeId);
        userAccountService.updateUserAccount(userAccount, PROFILE_REFEREE_UPDATE);
    }

    public void deleteRefereeApplication(Integer applicationId, Integer refereeId) {
        Application application = applicationService.getById(applicationId);
        if (profileDAO.deleteUserProfileSection(UserReferee.class, ApplicationReferee.class, refereeId)) {
            userAccountService.updateUserAccount(userService.getCurrentUser().getUserAccount(), PROFILE_REFEREE_UPDATE);
        }

        ApplicationReferee referee = deleteReferee(application, ApplicationReferee.class, refereeId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, getUserAssignmentDelete(referee.getUser(), APPLICATION_REFEREE));
    }

    public void updateDocumentUser(ProfileDocumentDTO documentDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserDocument document = updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccount.setDocument(document);
        userAccountService.updateUserAccount(userAccount, PROFILE_DOCUMENT_UPDATE);
    }

    public void updateDocumentApplication(Integer applicationId, ProfileDocumentDTO documentDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationDocument applicationDocument = updateDocument(application, ApplicationDocument.class, documentDTO);
        applicationDocument.setCoveringLetter(getDocument(documentDTO.getCoveringLetter()));

        documentDTO.setCv(cloneDocument(documentDTO.getCv()));
        documentDTO.setCoveringLetter(cloneDocument(documentDTO.getCoveringLetter()));

        UserAccount userAccount = application.getUser().getUserAccount();
        UserDocument userDocument = updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccount.setDocument(userDocument);
        userAccountService.updateUserAccount(userAccount, PROFILE_DOCUMENT_UPDATE);

        applicationDocument.setLastUpdatedTimestamp(DateTime.now());
        application.setDocument(applicationDocument);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformationUser(ProfileAdditionalInformationDTO additionalInformationDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserAdditionalInformation additionalInformation = updateAdditionalInformation(userAccount, UserAdditionalInformation.class, additionalInformationDTO);
        userAccount.setAdditionalInformation(additionalInformation);
        userAccountService.updateUserAccount(userAccount, PROFILE_ADDITIONAL_INFORMATION_UPDATE);
    }

    public void updateAdditionalInformationApplication(Integer applicationId, ProfileAdditionalInformationDTO additionalInformationDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationAdditionalInformation applicationAdditionalInformation = updateAdditionalInformation(application, ApplicationAdditionalInformation.class,
                additionalInformationDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserAdditionalInformation userAdditionalInformation = updateAdditionalInformation(userAccount, UserAdditionalInformation.class,
                additionalInformationDTO);
        userAccount.setAdditionalInformation(userAdditionalInformation);
        userAccountService.updateUserAccount(userAccount, PROFILE_ADDITIONAL_INFORMATION_UPDATE);

        applicationAdditionalInformation.setLastUpdatedTimestamp(DateTime.now());
        application.setAdditionalInformation(applicationAdditionalInformation);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION);
    }

    private void fillApplicationPersonalDetail(Application application, UserAccount userAccount) {
        UserPersonalDetail userPersonalDetail = userAccount.getPersonalDetail();
        if (userPersonalDetail != null) {
            ApplicationPersonalDetail applicationPersonalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(applicationPersonalDetail);
            applicationPersonalDetail.setAssociation(application);

            applicationPersonalDetail.setGender(userPersonalDetail.getGender());
            applicationPersonalDetail.setNationality(userPersonalDetail.getNationality());
            applicationPersonalDetail.setDomicile(userPersonalDetail.getDomicile());
            applicationPersonalDetail.setVisaRequired(userPersonalDetail.getVisaRequired());

            applicationPersonalDetail.setPhone(userPersonalDetail.getPhone());
            applicationPersonalDetail.setSkype(userPersonalDetail.getSkype());

            applicationPersonalDetail.setEthnicity(userPersonalDetail.getEthnicity());
            applicationPersonalDetail.setDisability(userPersonalDetail.getDisability());
            applicationPersonalDetail.setLastUpdatedTimestamp(new DateTime());
        }
    }

    private void fillApplicationAddress(Application application, UserAccount userAccount) {
        UserAddress userAddress = userAccount.getAddress();
        if (userAddress != null) {
            ApplicationAddress applicationAddress = new ApplicationAddress();
            application.setAddress(applicationAddress);
            applicationAddress.setAssociation(application);
            applicationAddress.setCurrentAddress(addressService.cloneAddress(userAddress.getCurrentAddress()));
            applicationAddress.setContactAddress(addressService.cloneAddress(userAddress.getContactAddress()));
            applicationAddress.setLastUpdatedTimestamp(new DateTime());
        }
    }

    private void fillApplicationQualifications(Application application, UserAccount userAccount) {
        for (UserQualification userQualification : userAccount.getQualifications()) {
            ApplicationQualification applicationQualification = new ApplicationQualification();
            application.getQualifications().add(applicationQualification);
            applicationQualification.setAssociation(application);
            fillQualification(applicationQualification, userQualification);
        }
    }

    private void fillApplicationAwards(Application application, UserAccount userAccount) {
        for (UserAward userAward : userAccount.getAwards()) {
            ApplicationAward applicationAward = new ApplicationAward();
            application.getAwards().add(applicationAward);
            applicationAward.setAssociation(application);
            fillAward(applicationAward, userAward);
        }
    }

    private void fillQualification(ApplicationQualification applicationQualification, UserQualification userQualification) {
        applicationQualification.setAdvert(userQualification.getAdvert());
        applicationQualification.setStartYear(userQualification.getStartYear());
        applicationQualification.setStartMonth(userQualification.getStartMonth());
        applicationQualification.setAwardYear(userQualification.getAwardYear());
        applicationQualification.setAwardMonth(userQualification.getAwardMonth());
        applicationQualification.setGrade(userQualification.getGrade());
        applicationQualification.setCompleted(userQualification.getCompleted());
        applicationQualification.setDocument(documentService.cloneDocument(userQualification.getDocument()));
        applicationQualification.setLastUpdatedTimestamp(new DateTime());
    }

    private void fillAward(ApplicationAward applicationAward, UserAward userAward) {
        applicationAward.setName(userAward.getName());
        applicationAward.setDescription(userAward.getDescription());
        applicationAward.setAwardYear(userAward.getAwardYear());
        applicationAward.setAwardMonth(userAward.getAwardMonth());
        applicationAward.setLastUpdatedTimestamp(new DateTime());
    }

    private void fillApplicationEmploymentPositions(Application application, UserAccount userAccount) {
        for (UserEmploymentPosition userEmploymentPosition : userAccount.getEmploymentPositions()) {
            ApplicationEmploymentPosition applicationEmploymentPosition = new ApplicationEmploymentPosition();
            application.getEmploymentPositions().add(applicationEmploymentPosition);
            applicationEmploymentPosition.setAssociation(application);
            copyEmploymentPosition(applicationEmploymentPosition, userEmploymentPosition);
        }
    }

    private void copyEmploymentPosition(ApplicationEmploymentPosition applicationEmploymentPosition, UserEmploymentPosition userEmploymentPosition) {
        applicationEmploymentPosition.setAdvert(userEmploymentPosition.getAdvert());
        applicationEmploymentPosition.setStartYear(userEmploymentPosition.getStartYear());
        applicationEmploymentPosition.setStartMonth(userEmploymentPosition.getStartMonth());
        applicationEmploymentPosition.setEndYear(userEmploymentPosition.getEndYear());
        applicationEmploymentPosition.setEndMonth(userEmploymentPosition.getEndMonth());
        applicationEmploymentPosition.setCurrent(userEmploymentPosition.getCurrent());
        applicationEmploymentPosition.setLastUpdatedTimestamp(new DateTime());
    }

    private void fillApplicationReferees(Application application, UserAccount userAccount) {
        for (UserReferee userReferee : userAccount.getReferees()) {
            ApplicationReferee applicationReferee = new ApplicationReferee();
            application.getReferees().add(applicationReferee);
            applicationReferee.setAssociation(application);
            cloneReferee(applicationReferee, userReferee);
        }
    }

    private void cloneReferee(ApplicationReferee applicationReferee, UserReferee userReferee) {
        applicationReferee.setAdvert(userReferee.getAdvert());
        applicationReferee.setUser(userReferee.getUser());
        applicationReferee.setPhone(userReferee.getPhone());
        applicationReferee.setSkype(userReferee.getSkype());
        applicationReferee.setLastUpdatedTimestamp(new DateTime());
    }

    private void fillApplicationDocument(Application application, UserAccount userAccount) {
        UserDocument userDocument = userAccount.getDocument();
        if (userDocument != null) {
            ApplicationDocument applicationDocument = new ApplicationDocument();
            application.setDocument(applicationDocument);
            applicationDocument.setAssociation(application);
            applicationDocument.setPersonalSummary(userDocument.getPersonalSummary());
            applicationDocument.setCv(documentService.cloneDocument(userDocument.getCv()));
            applicationDocument.setLastUpdatedTimestamp(now());
        }
    }

    private void fillApplicationAdditionalInformation(Application application, UserAccount userAccount) {
        UserAdditionalInformation userAdditionalInformation = userAccount.getAdditionalInformation();
        if (userAdditionalInformation != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            application.setAdditionalInformation(additionalInformation);
            additionalInformation.setAssociation(application);
            additionalInformation.setRequirements(userAdditionalInformation.getRequirements());
            additionalInformation.setConvictions(userAdditionalInformation.getConvictions());
            additionalInformation.setLastUpdatedTimestamp(new DateTime());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfilePersonalDetail<T>> U updatePersonalDetail(
            T profile, Class<U> personalDetailClass, ProfilePersonalDetailDTO personalDetailDTO) {
        userService.updateUser(personalDetailDTO.getUser());
        U personalDetail = (U) profile.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = instantiate(personalDetailClass);
        }

        personalDetail.setGender(personalDetailDTO.getGender());

        personalDetail.setNationality(prismService.getDomicileById(personalDetailDTO.getNationality()));
        personalDetail.setDomicile(prismService.getDomicileById(personalDetailDTO.getDomicile()));
        personalDetail.setVisaRequired(BooleanUtils.isTrue(personalDetailDTO.getVisaRequired()));

        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));

        personalDetail.setEthnicity(personalDetailDTO.getEthnicity());
        personalDetail.setDisability(personalDetailDTO.getDisability());

        if (personalDetailClass.equals(UserPersonalDetail.class)) {
            ((UserPersonalDetail) personalDetail).setDateOfBirth(personalDetailDTO.getDateOfBirth());
        }

        return personalDetail;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileAddress<T>> U updateAddress(
            T profile, Class<U> addressClass, ProfileAddressDTO addressSectionDTO) {
        U addressSection = (U) profile.getAddress();
        if (addressSection == null) {
            addressSection = instantiate(addressClass);
        }

        updateAddressComponent(addressSectionDTO, addressSection, "currentAddress");
        updateAddressComponent(addressSectionDTO, addressSection, "contactAddress");
        return addressSection;
    }

    private void updateAddressComponent(ProfileAddressDTO addressSectionDTO, ProfileAddress<?> addressSection, String componentName) {
        Address address = (Address) getProperty(addressSection, componentName);
        AddressDTO addressDTO = (AddressDTO) getProperty(addressSectionDTO, componentName);
        if (address == null) {
            address = new Address();
            setProperty(addressSection, componentName, address);
            addressService.updateGeocodeAndPersistAddress(address, addressDTO);
        } else {
            addressService.updateAndGeocodeAddress(address, addressDTO);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U updateQualification(
            T profile, Class<U> qualificationClass, Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
        U qualification;
        if (qualificationId == null) {
            qualification = instantiate(qualificationClass);
        } else {
            qualification = getProfileQualification(qualificationClass, qualificationId);
        }

        updateQualification(profile, qualification, qualificationDTO);
        return qualification;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void updateQualification(
            T profile, U qualification, ProfileQualificationDTO qualificationDTO) {
        updateAdvertRelation(qualification, qualificationDTO);

        qualification.setStartYear(qualificationDTO.getStartDate().getYear());
        qualification.setStartMonth(qualificationDTO.getStartDate().getMonthOfYear());

        LocalDate awardDate = qualificationDTO.getAwardDate();
        if (awardDate != null) {
            qualification.setAwardYear(awardDate.getYear());
            qualification.setAwardMonth(awardDate.getMonthOfYear());
        }

        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setCompleted(isTrue(qualificationDTO.getCompleted()));
        qualification.setDocument(getDocument(qualificationDTO.getDocument()));

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            ((ApplicationQualification) qualification).setLastUpdatedTimestamp(DateTime.now());
        }

        U duplicateQualification = (U) entityService.getDuplicateEntity(
                (Class<? extends UniqueEntity>) qualification.getClass(),
                new EntitySignature().addProperty("association", qualification.getAssociation()).addProperty("advert", qualification.getAdvert())
                        .addProperty("startYear",
                                qualification.getStartYear()));
        if (!(duplicateQualification == null || Objects.equal(qualification.getId(), duplicateQualification.getId()))) {
            entityService.delete(duplicateQualification);
        }

        if (qualification.getId() == null) {
            ((Set<U>) profile.getQualifications()).add(qualification);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileAward<T>> U updateAward(
            T profile, Class<U> awardClass, Integer awardId, ProfileAwardDTO awardDTO) {
        U award;
        if (awardId == null) {
            award = instantiate(awardClass);
        } else {
            award = getProfileAward(awardClass, awardId);
        }

        updateAward(profile, award, awardDTO);
        return award;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileAward<T>> void updateAward(
            T profile, U award, ProfileAwardDTO awardDTO) {
        award.setName(awardDTO.getName());
        award.setDescription(awardDTO.getDescription());

        LocalDate awardDate = awardDTO.getAwardDate();
        if (awardDate != null) {
            award.setAwardYear(awardDate.getYear());
            award.setAwardMonth(awardDate.getMonthOfYear());
        }

        if (award.getClass().equals(ApplicationAward.class)) {
            ((ApplicationAward) award).setLastUpdatedTimestamp(DateTime.now());
        }

        U duplicateAward = (U) entityService.getDuplicateEntity((Class<? extends UniqueEntity>) award.getClass(),
                new EntitySignature().addProperty("association", award.getAssociation()).addProperty("name", award.getName()).addProperty("awardYear",
                        award.getAwardYear()).addProperty("awardMonth", award.getAwardMonth()));
        if (!(duplicateAward == null || Objects.equal(award.getId(), duplicateAward.getId()))) {
            entityService.delete(duplicateAward);
        }

        if (award.getId() == null) {
            ((Set<U>) profile.getAwards()).add(award);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void deleteQualification(
            T profile, Class<U> qualificationClass, Integer qualificationId) {
        U qualification = getProfileQualification(qualificationClass, qualificationId);
        profile.getQualifications().remove(qualification);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileAward<T>> void deleteAward(
            T profile, Class<U> awardClass, Integer awardId) {
        U award = getProfileAward(awardClass, awardId);
        profile.getAwards().remove(award);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U updateEmploymentPosition(
            T profile, Class<U> employmentPositionClass, Integer employmentPositionId, ProfileEmploymentPositionDTO employmentPositionDTO) {
        U employmentPosition;
        if (employmentPositionId == null) {
            employmentPosition = instantiate(employmentPositionClass);
        } else {
            employmentPosition = getProfileEmploymentPosition(employmentPositionClass, employmentPositionId);
        }

        updateEmploymentPosition(profile, employmentPosition, employmentPositionDTO);
        return employmentPosition;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void updateEmploymentPosition(
            T profile, U employmentPosition, ProfileEmploymentPositionDTO employmentPositionDTO) {
        updateAdvertRelation(employmentPosition, employmentPositionDTO);

        employmentPosition.setStartYear(employmentPositionDTO.getStartDate().getYear());
        employmentPosition.setStartMonth(employmentPositionDTO.getStartDate().getMonthOfYear());

        LocalDate endDate = employmentPositionDTO.getEndDate();
        if (endDate != null) {
            employmentPosition.setEndYear(endDate.getYear());
            employmentPosition.setEndMonth(endDate.getMonthOfYear());
        }

        employmentPosition.setCurrent(isTrue(employmentPositionDTO.getCurrent()));

        if (employmentPosition.getClass().equals(ApplicationEmploymentPosition.class)) {
            ((ApplicationEmploymentPosition) employmentPosition).setLastUpdatedTimestamp(DateTime.now());
        }

        U duplicateEmploymentPosition = (U) entityService.getDuplicateEntity((Class<? extends UniqueEntity>) employmentPosition.getClass(),
                new EntitySignature().addProperty("association", employmentPosition.getAssociation()).addProperty("advert", employmentPosition.getAdvert())
                        .addProperty("startYear",
                                employmentPosition.getStartYear()).addProperty("startMonth", employmentPosition.getStartMonth()));
        if (!(duplicateEmploymentPosition == null || Objects.equal(employmentPosition.getId(), duplicateEmploymentPosition.getId()))) {
            entityService.delete(duplicateEmploymentPosition);
        }

        if (employmentPosition.getId() == null) {
            ((Set<U>) profile.getEmploymentPositions()).add(employmentPosition);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void deleteEmploymentPosition(
            T profile, Class<U> employmentPositionClass, Integer employmentPositionId) {
        U employmentPosition = getProfileEmploymentPosition(employmentPositionClass, employmentPositionId);
        profile.getEmploymentPositions().remove(employmentPosition);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> ProfileRefereeUpdateDTO updateReferee(
            T profile, Class<U> refereeClass, Integer refereeId, ProfileRefereeDTO refereeDTO) {
        U referee;
        if (refereeId == null) {
            referee = instantiate(refereeClass);
        } else {
            referee = getProfileReferee(refereeClass, refereeId);
        }

        List<CommentAssignedUser> refereeAssignments = updateReferee(profile, referee, refereeDTO);
        return new ProfileRefereeUpdateDTO(referee, refereeAssignments);
    }

    @SuppressWarnings("unchecked")
    private <U extends ProfileReferee<T>, T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> List<CommentAssignedUser> updateReferee(
            T profile, U referee, ProfileRefereeDTO refereeDTO) {
        List<CommentAssignedUser> refereeAssignments = assignReferee(profile, referee, refereeDTO);
        updateAdvertRelation(referee, refereeDTO);

        referee.setPhone(refereeDTO.getPhone());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));

        if (referee.getClass().equals(ApplicationReferee.class)) {
            ((ApplicationReferee) referee).setLastUpdatedTimestamp(DateTime.now());
        }

        U duplicateReferee = (U) entityService.getDuplicateEntity(
                (Class<? extends UniqueEntity>) referee.getClass(),
                new EntitySignature().addProperty("association", referee.getAssociation()).addProperty("advert", referee.getAdvert())
                        .addProperty("user", referee.getUser()));
        if (!(duplicateReferee == null || Objects.equal(referee.getId(), duplicateReferee.getId()))) {
            entityService.delete(duplicateReferee);
        }

        if (referee.getId() == null) {
            ((Set<U>) profile.getReferees()).add(referee);
        }

        return refereeAssignments;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileDocument<T>> U updateDocument(
            T profile, Class<U> documentClass, ProfileDocumentDTO documentDTO) {
        U document = (U) profile.getDocument();
        if (document == null) {
            document = instantiate(documentClass);
        }

        document.setPersonalSummary(documentDTO.getPersonalSummary());
        document.setCv(getDocument(documentDTO.getCv()));

        return document;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileAdditionalInformation<T>> U updateAdditionalInformation(
            T profile, Class<U> additionalInformationClass, ProfileAdditionalInformationDTO additionalInformationDTO) {
        U additionalInformation = (U) profile.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = instantiate(additionalInformationClass);
        }

        additionalInformation.setRequirements(additionalInformationDTO.getRequirements());
        additionalInformation.setConvictions(additionalInformationDTO.getConvictions());
        return additionalInformation;
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> U deleteReferee(
            T profile, Class<U> refereeClass, Integer refereeId) {
        U referee = getProfileReferee(refereeClass, refereeId);
        profile.getReferees().remove(referee);
        entityService.flush();
        return referee;
    }

    private <T extends ProfileReferee<U>, U extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> List<CommentAssignedUser> assignReferee(
            U profile, T referee, ProfileRefereeDTO refereeDTO) {
        User oldUser = referee.getUser();
        UserDTO userDTO = refereeDTO.getResource().getUser();
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        referee.setUser(newUser);

        if (referee.getClass().equals(ApplicationReferee.class)) {
            return getUserAssignmentsUpdate((Application) profile, oldUser, newUser, APPLICATION_REFEREE);
        }

        return emptyList();
    }

    private void updateAdvertRelation(ProfileAdvertRelationSection<?> advertRelation, ApplicationAdvertRelationSectionDTO advertRelationDTO) {
        ResourceRelationCreationDTO resourceRelationDTO = advertRelationDTO.getResource();
        ResourceParent resource = resourceService.createResourceRelation(resourceRelationDTO);

        UserDTO userConnectionDTO = resourceRelationDTO.getUser();
        if (userConnectionDTO != null) {
            advertRelation.setUser(userService.getUserByEmail(userConnectionDTO.getEmail()));
        }

        Advert advert = resource.getAdvert();
        ResourceCreationDTO resourceDTO = advertRelationDTO.getResource().getResource().getResource();
        if (ResourceParentDTO.class.isAssignableFrom(resourceDTO.getClass())) {
            advert.setSummary(((ResourceParentDTO) resourceDTO).getSummary());
        }

        advertRelation.setAdvert(advert);
    }

    private <T extends ProfileQualification<?>> T getProfileQualification(Class<T> qualificationClass, Integer qualificationId) {
        return entityService.getById(qualificationClass, qualificationId);
    }

    private <T extends ProfileAward<?>> T getProfileAward(Class<T> awardClass, Integer awardId) {
        return entityService.getById(awardClass, awardId);
    }

    private <T extends ProfileEmploymentPosition<?>> T getProfileEmploymentPosition(Class<T> employmentPositionClass, Integer employmentPositionId) {
        return entityService.getById(employmentPositionClass, employmentPositionId);
    }

    private <T extends ProfileReferee<?>> T getProfileReferee(Class<T> refereeClass, Integer refereeId) {
        return entityService.getById(refereeClass, refereeId);
    }

    private Document getDocument(DocumentDTO documentDTO) {
        Integer documentId = documentDTO == null ? null : documentDTO.getId();
        if (documentId != null) {
            return documentService.getById(documentId, DOCUMENT);
        }
        return null;
    }

    private List<CommentAssignedUser> getUserAssignmentsUpdate(Application application, User oldUser, User newUser, PrismRole prismRole) {
        List<CommentAssignedUser> assignees = Lists.newArrayList();
        if (application.isSubmitted()) {
            Role role = roleService.getById(prismRole);
            assignees.add(new CommentAssignedUser().withUser(newUser).withRole(role).withRoleTransitionType(CREATE));
            if (!(oldUser == null || Objects.equal(newUser.getId(), oldUser.getId()))) {
                assignees.add(new CommentAssignedUser().withUser(oldUser).withRole(role).withRoleTransitionType(DELETE));
            }
        }
        return assignees;
    }

    private CommentAssignedUser getUserAssignmentDelete(User user, PrismRole roleId) {
        Role role = roleService.getById(roleId);
        return new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(DELETE);
    }

    private DocumentDTO cloneDocument(DocumentDTO documentDTO) {
        if (documentDTO != null) {
            Document document = documentService.getById(documentDTO.getId());
            Document cloneDocument = documentService.cloneDocument(document);
            entityService.save(cloneDocument);

            return new DocumentDTO().withId(cloneDocument.getId()).withFileName(cloneDocument.getFileName());
        }
        return null;
    }

    private static class ProfileRefereeUpdateDTO {

        private ProfileReferee<?> referee;

        private List<CommentAssignedUser> assignments;

        public ProfileRefereeUpdateDTO(ProfileReferee<?> referee, List<CommentAssignedUser> assignments) {
            this.referee = referee;
            this.assignments = assignments;
        }

        public ProfileReferee<?> getReferee() {
            return referee;
        }

        public List<CommentAssignedUser> getAssignments() {
            return assignments;
        }

    }

}
