package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_EMPLOYMENT;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdditionalInformation;
import com.zuehlke.pgadmissions.domain.profile.ProfileAddress;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdvertRelationSection;
import com.zuehlke.pgadmissions.domain.profile.ProfileDocument;
import com.zuehlke.pgadmissions.domain.profile.ProfileEmploymentPosition;
import com.zuehlke.pgadmissions.domain.profile.ProfileEntity;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;
import com.zuehlke.pgadmissions.domain.profile.ProfileQualification;
import com.zuehlke.pgadmissions.domain.profile.ProfileReferee;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
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
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfilePersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

@Service
@Transactional
public class ProfileService {

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
        fillApplicationEmploymentPositions(application, userAccount);
        fillApplicationReferees(application, userAccount);
        fillApplicationDocument(application, userAccount);
        fillApplicationAdditionalInformation(application, userAccount);
    }

    public void updatePersonalDetailUser(ProfilePersonalDetailDTO personalDetailDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserPersonalDetail personalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);
        userAccount.setPersonalDetail(personalDetail);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updatePersonalDetailApplication(Integer applicationId, ProfilePersonalDetailDTO personalDetailDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationPersonalDetail applicationPersonalDetail = updatePersonalDetail(application, ApplicationPersonalDetail.class, personalDetailDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserPersonalDetail userPersonalDetail = updatePersonalDetail(userAccount, UserPersonalDetail.class, personalDetailDTO);

        LocalDate dateOfBirth = userPersonalDetail.getDateOfBirth();
        applicationPersonalDetail.setAgeRange(prismService.getAgeRangeFromAge(application.getCreatedTimestamp().getYear() - dateOfBirth.getYear()));

        userAccount.setPersonalDetail(userPersonalDetail);
        userAccountService.updateUserAccount(userAccount);

        applicationPersonalDetail.setLastUpdatedTimestamp(DateTime.now());
        application.setPersonalDetail(applicationPersonalDetail);

    }

    public void updateAddressUser(ProfileAddressDTO addressDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserAddress address = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(address);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateAddressApplication(Integer applicationId, ProfileAddressDTO addressDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationAddress address = updateAddress(application, ApplicationAddress.class, addressDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserAddress userAddress = updateAddress(userAccount, UserAddress.class, addressDTO);
        userAccount.setAddress(userAddress);
        userAccountService.updateUserAccount(userAccount);

        address.setLastUpdatedTimestamp(DateTime.now());
        application.setAddress(address);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_ADDRESS);
    }

    public UserQualification updateQualificationUser(Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserQualification userQualification = updateQualification(userAccount, UserQualification.class, qualificationId, qualificationDTO);
        userAccountService.updateUserAccount(userAccount);
        return userQualification;
    }

    public ApplicationQualification updateQualificationApplication(Integer applicationId, Integer qualificationId, ProfileQualificationDTO qualificationDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationQualification qualification = updateQualification(application, ApplicationQualification.class, qualificationId, qualificationDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserQualification userQualification = entityService.getDuplicateEntity(UserQualification.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("advert", qualification.getAdvert()).addProperty("startYear",
                        qualification.getStartYear()));
        updateQualification(userAccount, userQualification == null ? new UserQualification() : userQualification, qualificationDTO);
        userAccountService.updateUserAccount(userAccount);

        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
        return qualification;
    }

    public void deleteQualificationUser(Integer qualificationId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        ;
        deleteQualification(userAccount, UserQualification.class, qualificationId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteQualificationApplication(Integer applicationId, Integer qualificationId) {
        Application application = applicationService.getById(applicationId);
        deleteQualification(application, ApplicationQualification.class, qualificationId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_QUALIFICATION);
    }

    public UserEmploymentPosition updateEmploymentPositionUser(Integer employmentPositionId, ProfileEmploymentPositionDTO employmentPositionDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserEmploymentPosition employmentPosition = updateEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId, employmentPositionDTO);
        userAccountService.updateUserAccount(userAccount);
        return employmentPosition;
    }

    public ApplicationEmploymentPosition updateEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId,
            ProfileEmploymentPositionDTO employmentPositionDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationEmploymentPosition employmentPosition = updateEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId, employmentPositionDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserEmploymentPosition userEmploymentPosition = entityService.getDuplicateEntity(UserEmploymentPosition.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("advert", employmentPosition.getAdvert()).addProperty("startYear",
                        employmentPosition.getStartYear()));
        updateEmploymentPosition(userAccount, userEmploymentPosition == null ? new UserEmploymentPosition() : userEmploymentPosition, employmentPositionDTO);
        userAccountService.updateUserAccount(userAccount);

        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
        return employmentPosition;
    }

    public void deleteEmploymentPositionUser(Integer employmentPositionId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteEmploymentPosition(userAccount, UserEmploymentPosition.class, employmentPositionId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteEmploymentPositionApplication(Integer applicationId, Integer employmentPositionId) {
        Application application = applicationService.getById(applicationId);
        deleteEmploymentPosition(application, ApplicationEmploymentPosition.class, employmentPositionId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_EMPLOYMENT);
    }

    public UserReferee updateRefereeUser(Integer refereeId, ProfileRefereeDTO refereeDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserReferee userReferee = (UserReferee) updateReferee(userAccount, UserReferee.class, refereeId, refereeDTO).getReferee();
        userAccountService.updateUserAccount(userAccount);
        return userReferee;
    }

    public ApplicationReferee updateRefereeApplication(Integer applicationId, Integer refereeId, ProfileRefereeDTO refereeDTO) {
        Application application = applicationService.getById(applicationId);
        ProfileRefereeUpdateDTO referee = updateReferee(application, ApplicationReferee.class, refereeId, refereeDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserReferee userReferee = entityService.getDuplicateEntity(UserReferee.class,
                new EntitySignature().addProperty("association", userAccount).addProperty("user", referee.getReferee().getUser()));
        updateReferee(userAccount, userReferee != null ? userReferee : new UserReferee(), refereeDTO);
        userAccountService.updateUserAccount(userAccount);

        List<CommentAssignedUser> assignees = referee.getAssignments();
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, assignees.toArray(new CommentAssignedUser[assignees.size()]));
        return (ApplicationReferee) referee.getReferee();
    }

    public void deleteRefereeUser(Integer refereeId) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        deleteReferee(userAccount, UserReferee.class, refereeId);
        userAccountService.updateUserAccount(userAccount);
    }

    public void deleteRefereeApplication(Integer applicationId, Integer refereeId) {
        Application application = applicationService.getById(applicationId);
        ApplicationReferee referee = deleteReferee(application, ApplicationReferee.class, refereeId);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_REFEREE, getUserAssignmentDelete(referee.getUser(), APPLICATION_REFEREE));
    }

    public void updateDocumentUser(ProfileDocumentDTO documentDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserDocument document = updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccount.setDocument(document);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateDocumentApplication(Integer applicationId, ProfileDocumentDTO documentDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationDocument applicationDocument = updateDocument(application, ApplicationDocument.class, documentDTO);
        Document coveringLetter = documentDTO.getCoveringLetter() != null ? documentService.getById(documentDTO.getCoveringLetter().getId(), DOCUMENT) : null;
        applicationDocument.setCoveringLetter(coveringLetter);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserDocument userDocument = updateDocument(userAccount, UserDocument.class, documentDTO);
        userAccount.setDocument(userDocument);
        userAccountService.updateUserAccount(userAccount);

        applicationDocument.setLastUpdatedTimestamp(DateTime.now());
        application.setDocument(applicationDocument);
        applicationService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_DOCUMENT);
    }

    public void updateAdditionalInformationUser(ProfileAdditionalInformationDTO additionalInformationDTO) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        UserAdditionalInformation additionalInformation = updateAdditionalInformation(userAccount, UserAdditionalInformation.class, additionalInformationDTO);
        userAccount.setAdditionalInformation(additionalInformation);
        userAccountService.updateUserAccount(userAccount);
    }

    public void updateAdditionalInformationApplication(Integer applicationId, ProfileAdditionalInformationDTO additionalInformationDTO) {
        Application application = applicationService.getById(applicationId);
        ApplicationAdditionalInformation applicationAdditionalInformation = updateAdditionalInformation(application, ApplicationAdditionalInformation.class, additionalInformationDTO);

        UserAccount userAccount = application.getUser().getUserAccount();
        UserAdditionalInformation userAdditionalInformation = updateAdditionalInformation(userAccount, UserAdditionalInformation.class, additionalInformationDTO);
        userAccount.setAdditionalInformation(userAdditionalInformation);
        userAccountService.updateUserAccount(userAccount);

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
            additionalInformation.setRequirements(userAdditionalInformation.getRequirements());
            additionalInformation.setConvictions(userAdditionalInformation.getConvictions());
            additionalInformation.setLastUpdatedTimestamp(new DateTime());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfilePersonalDetail<T>> U updatePersonalDetail(
            T profile, Class<U> personalDetailClass, ProfilePersonalDetailDTO personalDetailDTO) {
        userService.updateUser(personalDetailDTO.getUser());
        U personalDetail = (U) profile.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = instantiate(personalDetailClass);
        }

        personalDetail.setGender(personalDetailDTO.getGender());

        personalDetail.setNationality(prismService.getDomicileById(personalDetailDTO.getNationality()));
        personalDetail.setDomicile(prismService.getDomicileById(personalDetailDTO.getDomicile()));
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());

        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));

        if (personalDetailClass.equals(UserPersonalDetail.class)) {
            ((UserPersonalDetail) personalDetail).setDateOfBirth(personalDetailDTO.getDateOfBirth());
        }

        return personalDetail;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileAddress<T>> U updateAddress(
            T profile, Class<U> addressClass, ProfileAddressDTO addressDTO) {
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

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> U updateQualification(
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
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void updateQualification(
            T profile, U qualification, ProfileQualificationDTO qualificationDTO) {
        createAdvertRelation(profile.getUser(), qualification, qualificationDTO);

        qualification.setStartYear(qualificationDTO.getStartDate().getYear());
        qualification.setStartMonth(qualificationDTO.getStartDate().getMonthOfYear());

        LocalDate awardDate = qualificationDTO.getAwardDate();
        if (awardDate != null) {
            qualification.setAwardYear(awardDate.getYear());
            qualification.setAwardMonth(awardDate.getMonthOfYear());
        }

        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setCompleted(isTrue(qualificationDTO.getCompleted()));
        Document document = qualificationDTO.getDocument() != null ? documentService.getById(qualificationDTO.getDocument().getId(), DOCUMENT) : null;
        qualification.setDocument(document);

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            ((ApplicationQualification) qualification).setLastUpdatedTimestamp(DateTime.now());
        }

        if (qualification.getId() == null) {
            ((Set<U>) profile.getQualifications()).add(qualification);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileQualification<T>> void deleteQualification(
            T profile, Class<U> qualificationClass, Integer qualificationId) {
        U qualification = getProfileQualification(qualificationClass, qualificationId);
        profile.getQualifications().remove(qualification);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> U updateEmploymentPosition(
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
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void updateEmploymentPosition(
            T profile, U employmentPosition, ProfileEmploymentPositionDTO employmentPositionDTO) {
        createAdvertRelation(profile.getUser(), employmentPosition, employmentPositionDTO);

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

        if (employmentPosition.getId() == null) {
            ((Set<U>) profile.getEmploymentPositions()).add(employmentPosition);
        }
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileEmploymentPosition<T>> void deleteEmploymentPosition(
            T profile, Class<U> employmentPositionClass, Integer employmentPositionId) {
        U employmentPosition = getProfileEmploymentPosition(employmentPositionClass, employmentPositionId);
        profile.getEmploymentPositions().remove(employmentPosition);
        entityService.flush();
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> ProfileRefereeUpdateDTO updateReferee(
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
    private <U extends ProfileReferee<T>, T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> List<CommentAssignedUser> updateReferee(
            T profile, U referee, ProfileRefereeDTO refereeDTO) {
        List<CommentAssignedUser> refereeAssignments = assignReferee(profile, referee, refereeDTO);
        createAdvertRelation(profile.getUser(), referee, refereeDTO);

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
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileDocument<T>> U updateDocument(
            T profile, Class<U> documentClass, ProfileDocumentDTO documentDTO) {
        U document = (U) profile.getDocument();
        if (document == null) {
            document = instantiate(documentClass);
        }

        document.setPersonalSummary(documentDTO.getPersonalSummary());
        document.setCv(documentService.getById(getDocumentId(documentDTO.getCv()), DOCUMENT));
        return document;
    }

    @SuppressWarnings("unchecked")
    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileAdditionalInformation<T>> U updateAdditionalInformation(
            T profile, Class<U> additionalInformationClass, ProfileAdditionalInformationDTO additionalInformationDTO) {
        U additionalInformation = (U) profile.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = instantiate(additionalInformationClass);
        }

        additionalInformation.setRequirements(additionalInformationDTO.getRequirements());
        additionalInformation.setConvictions(additionalInformationDTO.getConvictions());
        return additionalInformation;
    }

    private <T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>, U extends ProfileReferee<T>> U deleteReferee(
            T profile, Class<U> refereeClass, Integer refereeId) {
        U referee = getProfileReferee(refereeClass, refereeId);
        profile.getReferees().remove(referee);
        entityService.flush();
        return referee;
    }

    private <T extends ProfileReferee<U>, U extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> List<CommentAssignedUser> assignReferee(
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

    private void createAdvertRelation(User user, ProfileAdvertRelationSection<?> advertRelation, ApplicationAdvertRelationSectionDTO advertRelationDTO) {
        ResourceRelationCreationDTO resourceRelationDTO = advertRelationDTO.getResource();
        ResourceParent resource = resourceService.createResourceRelation(resourceRelationDTO);

        UserDTO userConnectionDTO = resourceRelationDTO.getUser();
        if (userConnectionDTO != null) {
            advertRelation.setUser(userService.getUserByEmail(userConnectionDTO.getEmail()));
        }

        Advert advert = resource.getAdvert();
        ResourceCreationDTO resourceDTO = advertRelationDTO.getResource().getResource().getResource();
        if (ResourceOpportunityDTO.class.isAssignableFrom(resourceDTO.getClass())) {
            advert.setSummary(((ResourceOpportunityDTO) resourceDTO).getAdvert().getSummary());
        }
        
        advertRelation.setAdvert(advert);
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
