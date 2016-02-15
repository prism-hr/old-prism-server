package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.math.RoundingMode.HALF_UP;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.VIEWER;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.PrismRoleContext;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.dto.ProfileEntityDTO;
import uk.co.alumeni.prism.dto.UnverifiedUserDTO;
import uk.co.alumeni.prism.dto.UserSelectionDTO;
import uk.co.alumeni.prism.rest.dto.UserListFilterDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceUnverifiedUserRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserFeedbackRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationInvitationBounced;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationUnverified;
import uk.co.alumeni.prism.rest.representation.user.UserRolesRepresentation;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.UserFeedbackService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

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
        userService.getUserProfiles(filter).forEach(user -> { //
                    representations.add(new ProfileListRowRepresentation()
                            .withRaisesUpdateFlag(user.getUpdatedTimestamp().isAfter(updatedBaseline))
                            .withUser(getUserRepresentationSimple(user))
                            .withPersonalSummary(user.getPersonalSummary())
                            .withCv(user.getCvId() == null ? null : documentMapper.getDocumentRepresentation(user.getCvId()))
                            .withLinkedInProfileUrl(user.getLinkedInProfileUrl())
                            .withApplicationCount(user.getApplicationCount() == null ? null : user.getApplicationCount().intValue())
                            .withApplicationRatingCount(user.getApplicationRatingCount() == null ? null : user.getApplicationRatingCount().intValue())
                            .withApplicationRatingAverage(
                                    user.getApplicationRatingAverage() == null ? null : user.getApplicationRatingAverage().setScale(RATING_PRECISION, HALF_UP))
                            .withUpdatedTimestamp(user.getUpdatedTimestamp())
                            .withSequenceIdentifier(user.getSequenceIdentifier()));
                });
        return representations;
    }

    public UserRepresentationSimple getUserRepresentationSimple(User user, User currentUser) {
        return getUserRepresentation(user, currentUser, UserRepresentationSimple.class);
    }

    public UserRepresentationExtended getUserRepresentationExtended(User user, User currentUser) {
        UserRepresentationExtended representation = getUserRepresentation(user, currentUser, UserRepresentationExtended.class);
        representation.setSendApplicationRecommendationNotification(user.getUserAccount().getSendActivityNotification());
        representation.setUserRoles(getUserRoleRepresentations(user));

        representation.setParentUser(user.getEmail());
        representation.setLinkedUsers(userService.getLinkedUsers(user).stream().map(u -> u.getEmail()).collect(Collectors.toList()));

        representation.setConnectedWithLinkedin(user.getUserAccount().getLinkedinId() != null);
        representation.setRequiredFeedbackRoleCategory(userFeedbackService.getRequiredFeedbackRoleCategory(user));
        representation.setResourcesForWhichUserCanCreateConnections(getUserConnectionResourceRepresentations(user, null));

        return representation;
    }

    public List<UserRepresentationInvitationBounced> getUserUnverifiedRepresentations(Resource resource, UserListFilterDTO filterDTO) {
        String noDiagnosis = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem())
                .loadLazy(SYSTEM_NO_DIAGNOSTIC_INFORMATION);

        User currentUser = userService.getCurrentUser();
        return userService.getBouncedOrUnverifiedUsers(resource, filterDTO).stream()
                .map(user -> getUserRepresentationUnverified(user, currentUser, noDiagnosis)).collect(Collectors.toList());
    }

    public UserFeedbackRepresentation getUserFeedbackRepresentation(UserFeedback userFeedback, User currentUser) {
        UserFeedbackRepresentation representation = new UserFeedbackRepresentation()
                .withResource(resourceMapper.getResourceRepresentationSimple(userFeedback.getResource()))
                .withUser(getUserRepresentationSimple(userFeedback.getUser(), currentUser)).withRoleCategory(userFeedback.getRoleCategory());

        if (BooleanUtils.isFalse(userFeedback.getDeclinedResponse())) {
            representation.setRating(userFeedback.getRating());
            representation.setContent(userFeedback.getContent());
            representation.setFeatureRequest(userFeedback.getFeatureRequest());
            representation.setRecommended(userFeedback.getRecommended());
            representation.setCreatedTimestamp(userFeedback.getCreatedTimestamp());
        }

        return representation;
    }

    private UserRepresentationInvitationBounced getUserRepresentationUnverified(User user, User currentUser, String noDiagnosisMessage) {
        UserRepresentationInvitationBounced representation = getUserRepresentation(user, currentUser, UserRepresentationInvitationBounced.class);

        String bounceMessage = user.getEmailBouncedMessage();
        if (bounceMessage != null) {
            JsonPrimitive diagnosis = new JsonParser().parse(bounceMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
            representation.setDiagnosticInformation(diagnosis != null ? diagnosis.getAsString() : noDiagnosisMessage);
        }

        return representation;
    }

    public <T extends UserRepresentationSimple> T getUserRepresentation(User user, User currentUser, Class<T> returnType) {
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

        representation.setEditable(userService.checkUserEditable(user, currentUser));
        return representation;
    }

    public List<UserRepresentationSimple> getUserRepresentations(List<UserSelectionDTO> users) {
        User currentUser = userService.getCurrentUser();
        return users.stream()
                .map(UserSelectionDTO::getUser)
                .map(user -> getUserRepresentationSimple(user, currentUser))
                .collect(Collectors.toList());
    }

    public UserActivityRepresentation getUserActivityRepresentation(Integer user) {
        return getUserActivityRepresentation(userService.getById(user));
    }

    public UserActivityRepresentation getUserActivityRepresentation(User user) {
        Map<PrismScope, PrismRoleCategory> defaultRoleCategories = roleService.getDefaultRoleCategories(user);
        return new UserActivityRepresentation().withDefaultRoleCategory(defaultRoleCategories.values().iterator().next())
                .withResourceActivities(scopeMapper.getResourceActivityRepresentation(user, defaultRoleCategories))
                .withAppointmentActivities(applicationMapper.getApplicationAppointmentRepresentations(user))
                .withUnverifiedUserActivities(getUnverifiedUserRepresentations(user))
                .withAdvertTargetActivities(advertMapper.getAdvertTargetRepresentations(advertService.getAdvertTargetsReceived(user)));
    }

    public UserProfileRepresentation getUserProfileRepresentation() {
        User currentUser = userService.getCurrentUser();
        UserAccount userAccount = currentUser.getUserAccount();
        return new UserProfileRepresentation().withPersonalDetail(profileMapper.getPersonalDetailRepresentation(userAccount.getPersonalDetail(), true))
                .withAddress(profileMapper.getAddressRepresentation(userAccount.getAddress()))
                .withQualifications(profileMapper.getQualificationRepresentations(userAccount.getQualifications(), currentUser))
                .withAwards(profileMapper.getAwardRepresentations(userAccount.getAwards()))
                .withEmploymentPositions(profileMapper.getEmploymentPositionRepresentations(userAccount.getEmploymentPositions(), currentUser))
                .withReferees(profileMapper.getRefereeRepresentations(userAccount.getReferees(), currentUser))
                .withDocument(profileMapper.getDocumentRepresentation(userAccount.getDocument()))
                .withAdditionalInformation(profileMapper.getAdditionalInformationRepresentation(userAccount.getAdditionalInformation(), true))
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
        roleService
                .getUserRoles(user)
                .forEach(
                        userRole -> index.put(new ResourceRepresentationIdentity().withScope(userRole.getScope()).withId(userRole.getId()), userRole.getRole()));

        return index.keySet().stream()
                .map(resource -> new UserRolesRepresentation(resource, newArrayList(index.get(resource))))
                .collect(Collectors.toList());
    }

    public List<ResourceRepresentationConnection> getUserConnectionResourceRepresentations(User user, String searchTerm) {
        List<ResourceRepresentationConnection> representations = Lists.newLinkedList();
        resourceService.getResourcesForWhichUserCanConnect(user, searchTerm).forEach(resource -> {
            representations.add(resourceMapper.getResourceRepresentationConnection(resource));
        });
        return representations;
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
                                unverifiedUser.getLogoImageId(), departmentId, unverifiedUser.getDepartmentName()))
                        .withUsers(userRepresentations);
                representations.put(resourceKey, representation);
            } else {
                userRepresentations = representation.getUsers();
            }

            UserRepresentationUnverified userRepresentation = new UserRepresentationUnverified().withId(unverifiedUser.getUserId())
                    .withFirstName(unverifiedUser.getUserFirstName())
                    .withLastName(unverifiedUser.getUserLastName()).withEmail(unverifiedUser.getUserEmail())
                    .withAccountProfileUrl(unverifiedUser.getUserLinkedinProfileUrl())
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
