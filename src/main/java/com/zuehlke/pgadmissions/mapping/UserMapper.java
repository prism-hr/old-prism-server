package com.zuehlke.pgadmissions.mapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetExtendedDTO;
import com.zuehlke.pgadmissions.dto.ProfileEntityDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.*;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ConnectionActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ConnectionActivityRepresentation.ConnectionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceUserActivityRepresentation;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static java.math.RoundingMode.HALF_UP;

@Service
@Transactional
public class UserMapper {

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private AdvertService advertService;

    @Inject
    private RoleService roleService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @Inject
    private UserService userService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private ProfileMapper profileMapper;

    @Inject
    private ScopeMapper scopeMapper;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private ApplicationContext applicationContext;

    public List<ProfileListRowRepresentation> getProfileListRowRepresentations(ProfileListFilterDTO filter) {
        DateTime updatedBaseline = DateTime.now().minusDays(1);
        List<ProfileListRowRepresentation> representations = Lists.newLinkedList();
        userService.getUserProfiles(filter).forEach(u -> {
            representations.add(new ProfileListRowRepresentation()
                    .withRaisesUpdateFlag(u.getUpdatedTimestamp().isAfter(updatedBaseline))
                    .withUser(getUserRepresentationSimple(u))
                    .withPersonalSummary(u.getPersonalSummary())
                    .withCv(u.getCvId() == null ? null : documentMapper.getDocumentRepresentation(u.getCvId()))
                    .withLinkedInProfileUrl(u.getLinkedInProfileUrl())
                    .withApplicationCount(u.getApplicationCount() == null ? null : u.getApplicationCount().intValue())
                    .withApplicationRatingCount(u.getApplicationRatingCount() == null ? null : u.getApplicationRatingCount().intValue())
                    .withApplicationRatingAverage(u.getApplicationRatingAverage() == null ? null : u.getApplicationRatingAverage().setScale(RATING_PRECISION, HALF_UP))
                    .withUpdatedTimestamp(u.getUpdatedTimestamp())
                    .withSequenceIdentifier(u.getSequenceIdentifier()));
        });
        return representations;
    }

    public UserRepresentationSimple getUserRepresentationSimple(User user) {
        return getUserRepresentation(user, UserRepresentationSimple.class);
    }

    public UserRepresentationExtended getUserRepresentationExtended(User user) {
        UserRepresentationExtended representation = getUserRepresentation(user, UserRepresentationExtended.class);
        representation.setSendApplicationRecommendationNotification(user.getUserAccount().getSendApplicationRecommendationNotification());

        PrismScope permissionScope = roleService.getPermissionScope(user);
        representation.setPermissionScope(permissionScope);

        representation.setParentUser(user.getEmail());
        representation.setLinkedUsers(userService.getLinkedUserAccounts(user));

        representation.setConnectedWithLinkedin(user.getUserAccount().getLinkedinId() != null);
        representation.setRequiredFeedbackRoleCategory(userFeedbackService.getRequiredFeedbackRoleCategory(user));
        representation.setResourcesForWhichUserCanCreateConnections(getUserConnectionResourceRepresentations(user, null));

        return representation;
    }

    public List<UserRepresentationUnverified> getUserUnverifiedRepresentations(Resource resource, UserListFilterDTO filterDTO) {
        String noDiagnosis = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem()).loadLazy(SYSTEM_NO_DIAGNOSTIC_INFORMATION);

        List<User> users = userService.getBouncedOrUnverifiedUsers(resource, filterDTO);
        List<UserRepresentationUnverified> representations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            representations.add(getUserRepresentationUnverified(user, noDiagnosis));
        }

        return representations;
    }

    public UserFeedbackRepresentation getUserFeedbackRepresentation(UserFeedback userFeedback) {
        UserFeedbackRepresentation representation = new UserFeedbackRepresentation()
                .withResource(resourceMapper.getResourceRepresentationSimple(userFeedback.getResource()))
                .withUser(getUserRepresentationSimple(userFeedback.getUser())).withRoleCategory(userFeedback.getRoleCategory());

        if (BooleanUtils.isFalse(userFeedback.getDeclinedResponse())) {
            representation.setRating(userFeedback.getRating());
            representation.setContent(userFeedback.getContent());
            representation.setFeatureRequest(userFeedback.getFeatureRequest());
            representation.setRecommended(userFeedback.getRecommended());
            representation.setCreatedTimestamp(userFeedback.getCreatedTimestamp());
        }

        return representation;
    }

    public UserInstitutionIdentityRepresentation getUserInstitutionIdentityRepresentation(User user, Institution institution, PrismUserInstitutionIdentity identityType) {
        return new UserInstitutionIdentityRepresentation().withIdentityType(identityType).withIdentifier(
                userService.getUserInstitutionIdentity(user, institution, identityType));
    }

    private UserRepresentationUnverified getUserRepresentationUnverified(User user, String noDiagnosisMessage) {
        UserRepresentationUnverified representation = getUserRepresentation(user, UserRepresentationUnverified.class);

        String bounceMessage = user.getEmailBouncedMessage();
        if (bounceMessage != null) {
            JsonPrimitive diagnosis = new JsonParser().parse(bounceMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
            representation.setDiagnosticInformation(diagnosis != null ? diagnosis.getAsString() : noDiagnosisMessage);
        }

        return representation;
    }

    public <T extends UserRepresentationSimple> T getUserRepresentation(User user, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(user.getId());
        representation.setFirstName(user.getFirstName());
        representation.setFirstName2(user.getFirstName2());
        representation.setFirstName3(user.getFirstName3());
        representation.setLastName(user.getLastName());
        representation.setFullName(user.getFullName());
        representation.setEmail(user.getEmail());

        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            representation.setAccountProfileUrl(userAccount.getLinkedinProfileUrl());
            representation.setAccountImageUrl(userAccount.getLinkedinImageUrl());
            representation.setPortraitImage(documentMapper.getDocumentRepresentation(userAccount.getPortraitImage()));
        }

        return representation;
    }

    public List<UserRepresentationSimple> getUserRepresentations(List<UserSelectionDTO> users) {
        List<UserRepresentationSimple> representations = Lists.newLinkedList();
        for (UserSelectionDTO user : users) {
            representations.add(getUserRepresentationSimple(user.getUser()));
        }
        return representations;
    }

    public UserActivityRepresentation getUserActivityRepresentation(User user) {
        return new UserActivityRepresentation().withResourceActivities(scopeMapper.getResourceActivityRepresentation(user))
                .withAppointmentActivities(applicationMapper.getApplicationAppointmentRepresentations(user)).withJoinActivities(getUnverifiedUserRepresentations(user))
                .withConnectionActivities(getUserConnectionRepresentations(user, true));
    }

    public UserProfileRepresentation getUserProfileRepresentation() {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        return new UserProfileRepresentation().withPersonalDetail(profileMapper.getPersonalDetailRepresentation(userAccount.getPersonalDetail()))
                .withAddress(profileMapper.getAddressRepresentation(userAccount.getAddress()))
                .withQualifications(profileMapper.getQualificationRepresentations(userAccount.getQualifications()))
                .withEmploymentPositions(profileMapper.getEmploymentPositionRepresentations(userAccount.getEmploymentPositions()))
                .withReferees(profileMapper.getRefereeRepresentations(userAccount.getReferees())).withDocument(profileMapper.getDocumentRepresentation(userAccount.getDocument()))
                .withAdditionalInformation(profileMapper.getAdditionalInformationRepresentation(userAccount.getAdditionalInformation()))
                .withUpdatedTimestamp(userAccount.getUpdatedTimestamp());
    }

    public UserRepresentationSimple getUserRepresentationSimple(ProfileEntityDTO profileEntity) {
        UserRepresentationSimple representation = new UserRepresentationSimple();
        representation.setId(profileEntity.getUserId());
        representation.setFirstName(profileEntity.getUserFirstName());
        representation.setFirstName2(profileEntity.getUserFirstName2());
        representation.setFirstName3(profileEntity.getUserFirstName3());
        representation.setLastName(profileEntity.getUserLastName());
        representation.setEmail(profileEntity.getUserEmail());
        representation.setAccountImageUrl(profileEntity.getUserAccountImageUrl());
        return representation;
    }

    public List<ConnectionActivityRepresentation> getUserConnectionRepresentations(User user, boolean pending) {
        Map<ResourceRepresentationActivity, ConnectionActivityRepresentation> representationIndex = Maps.newHashMap();
        TreeMultimap<ConnectionActivityRepresentation, ConnectionRepresentation> representationFilter = TreeMultimap.create();
        for (AdvertTargetExtendedDTO advertTarget : advertService.getAdvertTargetsReceived(AdvertTargetExtendedDTO.class, user, pending)) {
            ResourceRepresentationActivity acceptResourceRepresentation = getUserConnectionRepresentation(advertTarget.getAcceptInstitutionId(),
                    advertTarget.getAcceptInstitutionName(), advertTarget.getAcceptLogoImageId(), advertTarget.getAcceptDepartmentId(),
                    advertTarget.getAcceptDepartmentName());

            ConnectionActivityRepresentation representation = representationIndex.get(acceptResourceRepresentation);
            if (representation == null) {
                representation = new ConnectionActivityRepresentation().withAcceptResource(acceptResourceRepresentation);
                representationIndex.put(acceptResourceRepresentation, representation);
            }

            representationFilter.put(representation, getUserConnectionRepresentation(advertTarget));
        }

        List<ConnectionActivityRepresentation> representations = Lists.newLinkedList();
        representationFilter.keySet().forEach(representation -> {
            representation.setConnections(Lists.newLinkedList(representationFilter.get(representation)));
            representations.add(representation);
        });

        return representations;
    }

    public ConnectionRepresentation getUserConnectionRepresentation(AdvertTargetDTO advertTarget) {
        ConnectionRepresentation connectionRepresentation = new ConnectionRepresentation().withAdvertTargetId(advertTarget.getAdvertTargetId())
                .withResource(resourceMapper.getResourceRepresentationActivity(advertTarget.getOtherInstitutionId(), advertTarget.getOtherInstitutionName(),
                        advertTarget.getOtherInstitutionLogoImageId(), advertTarget.getOtherDepartmentId(), advertTarget.getOtherDepartmentName()))
                .withCanAccept(BooleanUtils.isTrue(advertTarget.getCanAccept()));

        Integer otherUserId = advertTarget.getOtherUserId();
        if (otherUserId != null) {
            connectionRepresentation.setUser(new UserRepresentationSimple().withId(advertTarget.getOtherUserId()).withFirstName(advertTarget.getOtherUserFirstName())
                    .withLastName(advertTarget.getOtherUserLastName()).withEmail(advertTarget.getOtherUserEmail())
                    .withAccountProfileUrl(advertTarget.getOtherUserLinkedinProfileUrl()).withAccountImageUrl(advertTarget.getOtherUserLinkedinImageUrl())
                    .withPortraitImage(documentMapper.getDocumentRepresentation(advertTarget.getOtherUserPortraitImageId())));
        }

        return connectionRepresentation;
    }

    public List<ResourceRepresentationConnection> getUserConnectionResourceRepresentations(User user, String searchTerm) {
        List<ResourceRepresentationConnection> representations = Lists.newLinkedList();
        resourceService.getResourcesForWhichUserCanMakeConnections(user, searchTerm).forEach(resource -> {
            representations.add(resourceMapper.getResourceRepresentationConnection(resource));
        });
        return representations;
    }

    private List<ResourceUserActivityRepresentation> getUnverifiedUserRepresentations(User user) {
        Map<String, ResourceUserActivityRepresentation> representations = Maps.newLinkedHashMap();
        for(UserRole userRole : userService.getUsersToVerify(user)) {
            Resource resource = userRole.getResource();
            String resourceCode = resource.getCode();

            List<UserRepresentationSimple> userRepresentations;
            ResourceUserActivityRepresentation representation = representations.get(resourceCode);
            if (representation == null) {
                userRepresentations = newLinkedList();
                representation = new ResourceUserActivityRepresentation().withResource(resourceMapper.getResourceRepresentationActivity(resource)).withUsers(userRepresentations);
                representations.put(resourceCode, representation);
            } else {
                userRepresentations = representation.getUsers();
            }

            userRepresentations.add(getUserRepresentationSimple(userRole.getUser()));
        }

        return newLinkedList(representations.values());
    }

    private ResourceRepresentationActivity getUserConnectionRepresentation(Integer institutionId, String institutionName, Integer logoImageId, Integer departmentId,
            String departmentName) {
        ResourceRepresentationActivity resourceRepresentation = new ResourceRepresentationActivity().withInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                .withId(institutionId).withName(institutionName).withLogoImage(documentMapper.getDocumentRepresentation(logoImageId)));

        if (departmentId != null) {
            resourceRepresentation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT).withId(departmentId).withName(departmentName));
        }

        return resourceRepresentation;
    }

}
