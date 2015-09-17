package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_EMPLOYMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.DOCUMENT;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.beans.BeanUtils.instantiate;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.UniqueEntity.EntitySignature;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdditionalInformation;
import com.zuehlke.pgadmissions.domain.profile.ProfileAddress;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdvertRelationSection;
import com.zuehlke.pgadmissions.domain.profile.ProfileDocument;
import com.zuehlke.pgadmissions.domain.profile.ProfileEmploymentPosition;
import com.zuehlke.pgadmissions.domain.profile.ProfileEntity;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;
import com.zuehlke.pgadmissions.domain.profile.ProfileQualification;
import com.zuehlke.pgadmissions.domain.profile.ProfileReferee;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAdditionalInformation;
import com.zuehlke.pgadmissions.domain.user.UserAddress;
import com.zuehlke.pgadmissions.domain.user.UserDocument;
import com.zuehlke.pgadmissions.domain.user.UserEmploymentPosition;
import com.zuehlke.pgadmissions.domain.user.UserPersonalDetail;
import com.zuehlke.pgadmissions.domain.user.UserQualification;
import com.zuehlke.pgadmissions.domain.user.UserReferee;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.rest.dto.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.DocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedDomicileDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfilePersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

@Service
@Transactional
public class ProfileService {

    @Inject
    private ActionService actionService;

    @Inject
    private AddressService addressService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private UserService userService;

    public void fillApplication(Application application) {
        UserAccount userAccount = application.getUser().getUserAccount();
        fillApplicationPersonalDetail(application, userAccount);
        fillApplicationAddress(application, userAccount);
        fillApplicationQualifications(application, userAccount);
        fillApplicationEmploymentPositions(application, userAccount);
        fillApplicationReferees(application, userAccount);
        fillApplicationDocument(application, userAccount);
        fillApplicationAdditionalInformation(application, userAccount);
    }

    public void updatePersonalDetailUser(Integer userId, ProfilePersonalDetailDTO personalDetailDTO) {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserPersonalDetail personalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);
        userAccount.setPersonalDetail(personalDetail);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updatePersonalDetailApplication(Integer applicationId, ProfilePersonalDetailDTO personalDetailDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationPersonalDetail applicationPersonalDetail = updatePersonalDetail(application, ApplicationPersonalDetail.class, personalDetailDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserPersonalDetail userPersonalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);

        LocalDate dateOfBirth = personalDetailDTO.getDateOfBirth();
        userPersonalDetail.setDateOfBirth(personalDetailDTO.getDateOfBirth());
        applicationPersonalDetail.setAgeRange(importedEntityService.getAgeRange(application.getCreatedTimestamp().getYear() - dateOfBirth.getYear()));

        userAccount.setPersonalDetail(userPersonalDetail);
        userAccountService.updateUserAccount(userAccount);

        applicationPersonalDetail.setLastUpdatedTimestamp(DateTime.now());
        application.setPersonalDetail(applicationPersonalDetail);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL);
    }

    public void updateAddressUser(Integer userId, ProfileAddressDTO addressDTO) {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserAddress address = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(address);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateAddressApplication(Integer applicationId, ProfileAddressDTO addressDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationAddress address = updateAddress(application, ApplicationAddress.class, addressDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserAddress userAddress = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(userAddress);
        userAccountService.updateUserAccount(userAccount);

        address.setLastUpdatedTimestamp(DateTime.now());
        application.setAddress(address);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDRESS);
    }

    public UserQualification updateQualificationUser(Integer userId, Integer qualificationId, ProfileQualificationDTO qualificationDTO) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserQualification userQualification = updateQualification(userAccount, UserQualification.class, qualificationId, qualificationDTO);
        userAccountService.updateUserAccount(userAccount);
        return userQualification;
    }

    public ApplicationQualification updateQualificationApplication(Integer applicationId, Integer qualificationId, ProfileQualificationDTO qualificationDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationQualification qualification = updateQualification(application, ApplicationQualification.class, qualificationId, qualificationDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserQualification userQualification = entityService.getDuplicateEntity(UserQualification.class,
                new EntitySignature().addProperty("userAccount", userAccount).addProperty("advert", qualification.getAdvert()).addProperty("startYear",
                        qualification.getStartYear()));
        updateQualification(userAccount, userQualification == null ? new UserQualification() : userQualification, qualificationDTO);
        userAccountService.updateUserAccount(userAccount);

        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualificationUser(Integer userId, Integer qualificationId) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        deleteQualification(userAccount, UserQualification.class, qualificationId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteQualificationApplication(Integer applicationId, Integer qualificationId) throws Exception {
        Application application = applicationService.getById(applicationId);
        deleteQualification(application, ApplicationQualification.class, qualificationId);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public UserEmploymentPosition updateEmploymentPositionUser(Integer userId, Integer employmentPositionId, ProfileEmploymentPositionDTO employmentPositionDTO) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserEmploymentPosition employmentPosition = updateEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId, employmentPositionDTO);
        userAccountService.updateUserAccount(userAccount);
        return employmentPosition;
    }

    public ApplicationEmploymentPosition updateEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId,
            ProfileEmploymentPositionDTO employmentPositionDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationEmploymentPosition employmentPosition = updateEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId, employmentPositionDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserEmploymentPosition userEmploymentPosition = entityService.getDuplicateEntity(UserEmploymentPosition.class,
                new EntitySignature().addProperty("userAccount", userAccount).addProperty("advert", employmentPosition.getAdvert()).addProperty("startYear",
                        employmentPosition.getStartYear()));
        updateEmploymentPosition(userAccount, userEmploymentPosition == null ? new UserEmploymentPosition() : userEmploymentPosition, employmentPositionDTO);
        userAccountService.updateUserAccount(userAccount);

        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
        return employmentPosition;
    }

    public void deleteEmploymentPositionUser(Integer userId, Integer employmentPositionId) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        deleteEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId) throws Exception {
        Application application = applicationService.getById(applicationId);
        deleteEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
    }

    public UserReferee updateRefereeUser(Integer userId, Integer refereeId, ProfileRefereeDTO refereeDTO) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserReferee userReferee = (UserReferee) updateReferee(userAccount, UserReferee.class, refereeId, refereeDTO).getReferee();
        userAccountService.updateUserAccount(userAccount);
        return userReferee;
    }

    public ApplicationReferee updateRefereeApplication(Integer applicationId, Integer refereeId, ProfileRefereeDTO refereeDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ProfileRefereeUpdateDTO referee = updateReferee(application, ApplicationReferee.class, refereeId, refereeDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserReferee userReferee = entityService.getDuplicateEntity(UserReferee.class,
                new EntitySignature().addProperty("userAccount", userAccount).addProperty("user", referee.getReferee().getUser()));
        updateReferee(userAccount, userReferee, refereeDTO);
        userAccountService.updateUserAccount(userAccount);

        List<CommentAssignedUser> assignees = referee.getAssignments();
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return (ApplicationReferee) referee.getReferee();
    }

    public void deleteRefereeUser(Integer userId, Integer refereeId) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        deleteReferee(userAccount, UserReferee.class, refereeId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteRefereeApplication(Integer applicationId, Integer refereeId) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationReferee referee = deleteReferee(application, ApplicationReferee.class, refereeId);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, getUserAssignmentDelete(referee.getUser(), APPLICATION_REFEREE));
    }

    public void updateDocumentUser(Integer userId, ProfileDocumentDTO documentDTO) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserDocument document = updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccount.setDocument(document);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateDocumentApplication(Integer applicationId, ProfileDocumentDTO documentDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationDocument document = updateDocument(application, ApplicationDocument.class, documentDTO);
        document.setCoveringLetter(documentService.getById(getDocumentId(documentDTO.getCoveringLetter()), DOCUMENT));

        UserAccount userAccount = application.getUser().getUserAccount();
        updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccountService.updateUserAccount(userAccount);

        document.setLastUpdatedTimestamp(DateTime.now());
        application.setDocument(document);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformationUser(Integer userId, ProfileAdditionalInformationDTO additionalInformationDTO) throws Exception {
        UserAccount userAccount = userAccountService.getCurrentUserAccount(userId);
        UserAdditionalInformation additionalInformation = updateAdditionalInformation(userAccount, UserAdditionalInformation.class, additionalInformationDTO);
        userAccount.setAdditionalInformation(additionalInformation);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateAdditionalInformationApplication(Integer applicationId, ProfileAdditionalInformationDTO additionalInformationDTO) throws Exception {
        Application application = applicationService.getById(applicationId);
        ApplicationAdditionalInformation additionalInformation = updateAdditionalInformation(application, ApplicationAdditionalInformation.class, additionalInformationDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        updateAdditionalInformation(userAccount, UserAdditionalInformation.class, additionalInformationDTO);
        userAccountService.updateUserAccount(userAccount);

        additionalInformation.setLastUpdatedTimestamp(DateTime.now());
        application.setAdditionalInformation(additionalInformation);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION);
    }

    private void fillApplicationPersonalDetail(Application application, UserAccount userAccount) {
        UserPersonalDetail userPersonalDetail = userAccount.getPersonalDetail();
        if (userPersonalDetail != null) {
            ApplicationPersonalDetail applicationPersonalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(applicationPersonalDetail);
            applicationPersonalDetail.setAssociation(application);

            applicationPersonalDetail.setTitle(userPersonalDetail.getTitle());
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
        }
    }

    private void fillApplicationAdditionalInformation(Application application, UserAccount userAccount) {
        UserAdditionalInformation userAdditionalInformation = userAccount.getAdditionalInformation();
        if (userAdditionalInformation != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            application.setAdditionalInformation(additionalInformation);
            additionalInformation.setAssociation(application);
            additionalInformation.setConvictionsText(userAdditionalInformation.getConvictionsText());
            additionalInformation.setLastUpdatedTimestamp(new DateTime());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfilePersonalDetail<T>> U updatePersonalDetail(T profile, Class<U> personalDetailClass,
            ProfilePersonalDetailDTO personalDetailDTO) {
        U personalDetail = (U) profile.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = instantiate(personalDetailClass);
        }

        personalDetail.setTitle(importedEntityService.getById(ImportedEntitySimple.class, getImportedEntityId(personalDetailDTO.getTitle())));
        personalDetail.setGender(importedEntityService.getById(ImportedEntitySimple.class, getImportedEntityId(personalDetailDTO.getGender())));

        personalDetail.setNationality(importedEntityService.getById(ImportedDomicile.class, getImportedDomicileId(personalDetailDTO.getNationality())));
        personalDetail.setDomicile(importedEntityService.getById(ImportedDomicile.class, getImportedDomicileId(personalDetailDTO.getDomicile())));
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());

        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));

        personalDetail.setEthnicity(importedEntityService.getById(ImportedEntitySimple.class, getImportedEntityId(personalDetailDTO.getEthnicity())));
        personalDetail.setDisability(importedEntityService.getById(ImportedEntitySimple.class, getImportedEntityId(personalDetailDTO.getDisability())));
        return personalDetail;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileAddress<T>> U updateAddress(T profile, Class<U> addressClass, ProfileAddressDTO addressDTO) {
        U address = (U) profile.getAddress();
        if (address == null) {
            address = instantiate(addressClass);
        }

        AddressDTO currentAddressDTO = addressDTO.getCurrentAddress();
        Address currentAddress = address.getCurrentAddress();
        if (currentAddress == null) {
            currentAddress = new Address();
            address.setCurrentAddress(currentAddress);
        }
        addressService.copyAddress(currentAddress, currentAddressDTO);

        AddressDTO contactAddressDTO = addressDTO.getContactAddress();
        Address contactAddress = address.getContactAddress();
        if (contactAddress == null) {
            contactAddress = new Address();
            address.setContactAddress(contactAddress);
        }
        addressService.copyAddress(contactAddress, contactAddressDTO);

        return address;
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U updateQualification(T profile, Class<U> qualificationClass,
            Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
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
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void updateQualification(T profile, U qualification,
            ProfileQualificationDTO qualificationDTO) {
        createUserAdvertRelation(profile.getUser(), qualification, qualificationDTO);

        qualification.setStartYear(qualificationDTO.getStartYear());
        qualification.setStartMonth(qualificationDTO.getStartMonth());
        qualification.setAwardYear(qualificationDTO.getAwardYear());
        qualification.setAwardMonth(qualificationDTO.getAwardMonth());

        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setCompleted(isTrue(qualificationDTO.getCompleted()));
        qualification.setDocument(documentService.getById(getDocumentId(qualificationDTO.getDocument()), DOCUMENT));

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            ((ApplicationQualification) qualification).setLastUpdatedTimestamp(DateTime.now());
        }

        if (qualification.getId() == null) {
            ((Set<U>) profile.getQualifications()).add(qualification);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void deleteQualification(T profile, Class<U> qualificationClass,
            Integer qualificationId) {
        U qualification = getProfileQualification(qualificationClass, qualificationId);
        updateUserAdvertRelation(profile.getUser(), qualification.getAdvert());
        profile.getQualifications().remove(qualification);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U updateEmploymentPosition(T profile, Class<U> employmentPositionClass,
            Integer employmentPositionId, ProfileEmploymentPositionDTO employmentPositionDTO) {
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
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void updateEmploymentPosition(T profile, U employmentPosition,
            ProfileEmploymentPositionDTO employmentPositionDTO) {
        createUserAdvertRelation(profile.getUser(), employmentPosition, employmentPositionDTO);

        employmentPosition.setStartYear(employmentPositionDTO.getStartYear());
        employmentPosition.setStartMonth(employmentPositionDTO.getStartMonth());
        employmentPosition.setEndYear(employmentPositionDTO.getEndYear());
        employmentPosition.setEndMonth(employmentPositionDTO.getEndMonth());
        employmentPosition.setCurrent(isTrue(employmentPositionDTO.getCurrent()));

        if (employmentPosition.getClass().equals(ApplicationEmploymentPosition.class)) {
            ((ApplicationEmploymentPosition) employmentPosition).setLastUpdatedTimestamp(DateTime.now());
        }

        if (employmentPosition.getId() == null) {
            ((Set<U>) profile.getEmploymentPositions()).add(employmentPosition);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void deleteEmploymentPosition(T profile, Class<U> employmentPositionClass,
            Integer employmentPositionId) {
        U employmentPosition = getProfileEmploymentPosition(employmentPositionClass, employmentPositionId);
        updateUserAdvertRelation(profile.getUser(), employmentPosition.getAdvert());
        profile.getEmploymentPositions().remove(employmentPosition);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> ProfileRefereeUpdateDTO updateReferee(T profile, Class<U> refereeClass,
            Integer refereeId, ProfileRefereeDTO refereeDTO) {
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
    private <U extends ProfileReferee<T>, T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> List<CommentAssignedUser> updateReferee(T profile, U referee,
            ProfileRefereeDTO refereeDTO) {
        List<CommentAssignedUser> refereeAssignments = assignReferee(referee, refereeDTO);
        createUserAdvertRelation(profile.getUser(), referee, refereeDTO);

        referee.setPhone(refereeDTO.getPhone());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));

        if (referee.getClass().equals(ApplicationReferee.class)) {
            ((ApplicationReferee) referee).setLastUpdatedTimestamp(DateTime.now());
        }

        if (referee.getId() == null) {
            ((Set<U>) profile.getReferees()).add(referee);
        }
        return refereeAssignments;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileDocument<T>> U updateDocument(T profile, Class<U> documentClass, ProfileDocumentDTO documentDTO) {
        U document = (U) profile.getAddress();
        if (document == null) {
            document = instantiate(documentClass);
        }

        document.setPersonalSummary(documentDTO.getPersonalSummary());
        document.setCv(documentService.getById(getDocumentId(documentDTO.getCv()), DOCUMENT));
        return document;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileAdditionalInformation<T>> U updateAdditionalInformation(T profile, Class<U> additionalInformationClass,
            ProfileAdditionalInformationDTO additionalInformationDTO) {
        U additionalInformation = (U) profile.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = instantiate(additionalInformationClass);
        }

        additionalInformation.setConvictionsText(additionalInformationDTO.getConvictionsText());
        return additionalInformation;
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> U deleteReferee(T profile, Class<U> refereeClass, Integer refereeId) {
        U referee = getProfileReferee(refereeClass, refereeId);
        updateUserAdvertRelation(profile.getUser(), referee.getAdvert());
        profile.getReferees().remove(referee);
        entityService.flush();
        return referee;
    }

    private <T extends ProfileReferee<?>> List<CommentAssignedUser> assignReferee(T referee, ProfileRefereeDTO refereeDTO) {
        User oldUser = referee.getUser();
        UserDTO userDTO = refereeDTO.getUser();
        User newUser = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        referee.setUser(newUser);

        if (referee.getClass().equals(ApplicationReferee.class)) {
            return getUserAssignmentsUpdate((Application) referee.getAssociation(), oldUser, newUser, APPLICATION_REFEREE);
        }

        return emptyList();
    }

    // TODO - implement the resource creation loop and link to theo
    // connections infrastructure
    // TODO - implement all of the possible scenarios (e.g. suggested user,
    // no known department, etc)
    private void createUserAdvertRelation(User user, ProfileAdvertRelationSection<?> advertRelation, ApplicationAdvertRelationSectionDTO advertRelationDTO) {
        List<ResourceCreationDTO> resourceCreations = resourceService.getResourceCreations(advertRelationDTO.getResource(), null);
        ResourceCreationDTO resourceDTO = Iterables.getLast(resourceCreations);
        Advert newAdvert = resourceService
                .createResource(systemService.getSystem().getUser(), actionService.getById(PrismAction.valueOf("INSTITUTION_CREATE" + resourceDTO.getScope().name())), resourceDTO)
                .getResource().getAdvert();

        Advert oldAdvert = advertRelation.getAdvert();
        if (oldAdvert != null && !oldAdvert.getId().equals(newAdvert.getId())) {
            userService.deleteUserAdvert(user, oldAdvert);
        }

        advertRelation.setAdvert(newAdvert);
        userService.getOrCreateUserAdvert(user, advertRelation.getAdvert());
    }

    private void updateUserAdvertRelation(User user, Advert advert) {
        if (userService.getUserAdvertRelationCount(user, advert).equals(0)) {
            userService.deleteUserAdvert(user, advert);
        }
    }

    private <T extends ProfileQualification<?>> T getProfileQualification(Class<T> qualificationClass, Integer qualificationId) {
        return entityService.getById(qualificationClass, qualificationId);
    }

    private <T extends ProfileEmploymentPosition<?>> T getProfileEmploymentPosition(Class<T> employmentPositionClass, Integer employmentPositionId) {
        return entityService.getById(employmentPositionClass, employmentPositionId);
    }

    private <T extends ProfileReferee<?>> T getProfileReferee(Class<T> refereeClass, Integer refereeId) {
        return entityService.getById(refereeClass, refereeId);
    }

    private Integer getImportedEntityId(ImportedEntityDTO importedEntity) {
        return importedEntity == null ? null : importedEntity.getId();
    }

    private String getImportedDomicileId(ImportedDomicileDTO importedDomicile) {
        return importedDomicile == null ? null : importedDomicile.getId();
    }

    private Integer getDocumentId(DocumentDTO document) {
        return document == null ? null : document.getId();
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
