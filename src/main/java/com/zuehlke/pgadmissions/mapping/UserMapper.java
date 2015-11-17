package com.zuehlke.pgadmissions.mapping;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.dto.ProfileEntityDTO;
import com.zuehlke.pgadmissions.dto.UnverifiedUserDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.user.*;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceUnverifiedUserRepresentation;
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
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext.STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext.VIEWER;
import static java.math.RoundingMode.HALF_UP;

@Service
@Transactional
public class UserMapper {

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

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
        representation.setVisibleScopes(roleService.getVisibleScopes(user));
        representation.setUserRoles(getUserRoleRepresentations(user));

        representation.setParentUser(user.getEmail());
        representation.setLinkedUsers(userService.getLinkedUsers(user).stream().map(u -> u.getEmail()).collect(Collectors.toList()));

        representation.setConnectedWithLinkedin(user.getUserAccount().getLinkedinId() != null);
        representation.setRequiredFeedbackRoleCategory(userFeedbackService.getRequiredFeedbackRoleCategory(user));
        representation.setResourcesForWhichUserCanCreateConnections(getUserConnectionResourceRepresentations(user, null));

        return representation;
    }

    public List<UserRepresentationInvitationBounced> getUserUnverifiedRepresentations(Resource resource, UserListFilterDTO filterDTO) {
        String noDiagnosis = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem()).loadLazy(SYSTEM_NO_DIAGNOSTIC_INFORMATION);

        return userService.getBouncedOrUnverifiedUsers(resource, filterDTO).stream()
                .map(user -> getUserRepresentationUnverified(user, noDiagnosis)).collect(Collectors.toList());
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

    private UserRepresentationInvitationBounced getUserRepresentationUnverified(User user, String noDiagnosisMessage) {
        UserRepresentationInvitationBounced representation = getUserRepresentation(user, UserRepresentationInvitationBounced.class);

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
                .withAppointmentActivities(applicationMapper.getApplicationAppointmentRepresentations(user)).withUnverifiedUserActivities(getUnverifiedUserRepresentations(user))
                .withAdvertTargetActivities(advertMapper.getAdvertTargetRepresentations(advertService.getAdvertTargetsReceived(user)));
    }

    public UserProfileRepresentation getUserProfileRepresentation() {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        return new UserProfileRepresentation().withPersonalDetail(profileMapper.getPersonalDetailRepresentation(userAccount.getPersonalDetail()))
                .withAddress(profileMapper.getAddressRepresentation(userAccount.getAddress()))
                .withQualifications(profileMapper.getQualificationRepresentations(userAccount.getQualifications()))
                .withEmploymentPositions(profileMapper.getEmploymentPositionRepresentations(userAccount.getEmploymentPositions()))
                .withReferees(profileMapper.getRefereeRepresentations(userAccount.getReferees())).withDocument(profileMapper.getDocumentRepresentation(userAccount.getDocument()))
                .withAdditionalInformation(profileMapper.getAdditionalInformationRepresentation(userAccount.getAdditionalInformation()))
                .withShared(userAccount.getShared()).withUpdatedTimestamp(userAccount.getUpdatedTimestamp());
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

    public List<UserRolesRepresentation> getUserRoleRepresentations(User user) {
        HashMultimap<ResourceRepresentationIdentity, PrismRole> index = HashMultimap.create();
        roleService.getUserRoles(user)
                .forEach(userRole -> index.put(new ResourceRepresentationIdentity().withScope(userRole.getScope()).withId(userRole.getId()), userRole.getRole()));

        return index.keySet().stream()
                .map(resource -> new UserRolesRepresentation(resource, newArrayList(index.get(resource))))
                .collect(Collectors.toList());
    }

    public List<ResourceRepresentationConnection> getUserConnectionResourceRepresentations(User user, String searchTerm) {
        return resourceService.getResourcesForWhichUserCanConnect(user, searchTerm).stream()
                .map(resource -> resourceMapper.getResourceRepresentationConnection(resource))
                .collect(Collectors.toList());
    }

    private List<ResourceUnverifiedUserRepresentation> getUnverifiedUserRepresentations(User user) {
        Map<String, ResourceUnverifiedUserRepresentation> representations = Maps.newLinkedHashMap();
        TreeMultimap<UserRepresentationUnverified, PrismRoleContext> userRepresentationIndex = TreeMultimap.create();
        for (UnverifiedUserDTO unverifiedUser : userService.getUsersToVerify(user)) {
            Integer departmentId = unverifiedUser.getDepartmentId();
            Integer institutionId = unverifiedUser.getInstitutionId();
            String resourceKey = Joiner.on("|").skipNulls().join(institutionId, departmentId);

            List<UserRepresentationUnverified> userRepresentations;
            ResourceUnverifiedUserRepresentation representation = representations.get(resourceKey);
            if (representation == null) {
                userRepresentations = newLinkedList();
                representation = new ResourceUnverifiedUserRepresentation()
                        .withResource(resourceMapper.getResourceRepresentationConnection(institutionId, unverifiedUser.getInstitutionName(),
                                unverifiedUser.getInstitutionLogoImageId(), departmentId, unverifiedUser.getDepartmentName()))
                        .withUsers(userRepresentations);
                representations.put(resourceKey, representation);
            } else {
                userRepresentations = representation.getUsers();
            }

            UserRepresentationUnverified userRepresentation = new UserRepresentationUnverified().withId(unverifiedUser.getUserId()).withFirstName(unverifiedUser.getUserFirstName())
                    .withLastName(unverifiedUser.getUserLastName()).withEmail(unverifiedUser.getUserEmail()).withAccountProfileUrl(unverifiedUser.getUserLinkedinProfileUrl())
                    .withAccountImageUrl(unverifiedUser.getUserLinkedinImageUrl())
                    .withPortraitImage(documentMapper.getDocumentRepresentation(unverifiedUser.getUserPortraitImageId()));

            userRepresentations.add(userRepresentation);
            PrismRole unverifiedRole = unverifiedUser.getRoleId();
            userRepresentationIndex.put(userRepresentation, unverifiedRole.name().contains("STUDENT") ? STUDENT : VIEWER);
        }

        userRepresentationIndex.keySet().forEach(unverifiedUser -> {
            unverifiedUser.setContexts(newLinkedList(userRepresentationIndex.get(unverifiedUser)));
        });

        return newLinkedList(representations.values());
    }

}
