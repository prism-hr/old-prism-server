package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.VIEWER;
import static uk.co.alumeni.prism.utils.PrismStringUtils.getObfuscatedEmail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
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
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationMessage;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceUserUnverifiedRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserFeedbackRepresentation;
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
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
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
    private PrismJsonMappingUtils prismJsonMappingUtils;

    @Inject
    private ApplicationContext applicationContext;

    public UserRepresentationSimple getUserRepresentationSimple(User user, User currentUser) {
        return getUserRepresentation(user, currentUser, UserRepresentationSimple.class);
    }

    public UserRepresentationSimple getUserRepresentationSimpleWithEmail(User user, User currentUser) {
        return getUserRepresentation(user, currentUser, true, UserRepresentationSimple.class);
    }

    public UserRepresentationExtended getUserRepresentationExtended(User user, User currentUser) {
        UserRepresentationExtended representation = getUserRepresentation(user, currentUser, UserRepresentationExtended.class);
        representation.setSendActivityNotification(user.getUserAccount().getSendActivityNotification());
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
                .map(user -> getUserRepresentationInvitationBounced(user, currentUser, noDiagnosis)).collect(Collectors.toList());
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

    private UserRepresentationInvitationBounced getUserRepresentationInvitationBounced(User user, User currentUser, String noDiagnosisMessage) {
        UserRepresentationInvitationBounced representation = getUserRepresentation(user, currentUser, true, UserRepresentationInvitationBounced.class);

        String bounceMessage = user.getEmailBouncedMessage();
        if (bounceMessage != null) {
            JsonPrimitive diagnosis = new JsonParser().parse(bounceMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
            representation.setDiagnosticInformation(diagnosis != null ? diagnosis.getAsString() : noDiagnosisMessage);
        }

        return representation;
    }

    public <T extends UserRepresentationSimple> T getUserRepresentation(User user, User currentUser, Class<T> returnType) {
        return getUserRepresentation(user, currentUser, false, returnType);
    }

    public <T extends UserRepresentationSimple> T getUserRepresentationWithEmail(User user, User currentUser, Class<T> returnType) {
        return getUserRepresentation(user, currentUser, true, returnType);
    }

    public List<UserRepresentationSimple> getUserRepresentations(List<UserSelectionDTO> users) {
        User currentUser = userService.getCurrentUser();
        List<UserRepresentationSimple> representations = newLinkedList();
        users.stream().forEach(user -> {
            UserRepresentationSimple representation = getUserRepresentationSimple(user.getUser(), currentUser);
            representation.setEmail(getObfuscatedEmail(representation.getEmail()));
            representations.add(representation);
        });
        return representations;
    }

    public UserActivityRepresentation getUserActivityRepresentation(Integer user) {
        return getUserActivityRepresentation(userService.getById(user));
    }

    public UserActivityRepresentation getUserActivityRepresentation(User user) {
        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            UserActivityRepresentation representation;
            String userActivityCache = userAccount.getActivityCache();
            if (userActivityCache == null) {
                representation = getUserActivityRepresentationFresh(user);
            } else {
                representation = prismJsonMappingUtils.readValue(userActivityCache, UserActivityRepresentation.class);
            }
            representation.setCacheIncrement(userAccount.getActivityCachedIncrement());
            return representation;
        }
        return null;
    }

    public UserActivityRepresentation getUserActivityRepresentationFresh(Integer user) {
        return getUserActivityRepresentationFresh(userService.getById(user));
    }

    public UserActivityRepresentation getUserActivityRepresentationFresh(User user) {
        Map<PrismScope, PrismRoleCategory> defaultRoleCategories = roleService.getDefaultRoleCategories(user);
        PrismRoleCategory defaultRoleCategory = defaultRoleCategories.size() == 0 ? null : defaultRoleCategories.values().iterator().next();

        Integer readMessageCount = userService.getUserReadMessageCount(user, user);
        Integer unreadMessageCount = userService.getUserUnreadMessageCount(user, user);
        UserActivityRepresentation representation = new UserActivityRepresentation().withDefaultRoleCategory(defaultRoleCategory)
                .withResourceActivities(scopeMapper.getResourceActivityRepresentation(user, defaultRoleCategories))
                .withProfileActivity(profileMapper.getProfileActivityRepresentation(user))
                .withMessageActivity(new ProfileRepresentationMessage()
                        .withReadMessageCount(readMessageCount == null ? 0 : readMessageCount)
                        .withUnreadMessageCount(unreadMessageCount == null ? 0 : unreadMessageCount))
                .withAppointmentActivities(applicationMapper.getApplicationAppointmentRepresentations(user))
                .withUnverifiedUserActivities(getUserUnverifiedRepresentations(user))
                .withAdvertTargetActivities(advertMapper.getAdvertTargetRepresentations(advertService.getAdvertTargetsReceived(user), user));

        userService.setUserActivityCache(user, representation, now());
        return representation;
    }

    public UserRepresentationSimple getUserRepresentationSimple(ProfileEntityDTO profileEntity, User user) {
        UserRepresentationSimple representation = new UserRepresentationSimple();
        representation.setId(profileEntity.getUserId());
        representation.setFirstName(profileEntity.getUserFirstName());
        representation.setFirstName2(profileEntity.getUserFirstName2());
        representation.setFirstName3(profileEntity.getUserFirstName3());
        representation.setLastName(profileEntity.getUserLastName());
        representation.setEmail(userService.getSecuredUserEmailAddress(profileEntity.getUserEmail(), user));
        representation.setAccountImageUrl(profileEntity.getUserAccountImageUrl());
        return representation;
    }

    public List<UserRolesRepresentation> getUserRoleRepresentations(User user) {
        HashMultimap<ResourceRepresentationIdentity, PrismRole> index = HashMultimap.create();
        resourceService.getResourceRoles(user).forEach( //
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

    private <T extends UserRepresentationSimple> T getUserRepresentation(User user, User currentUser, boolean forceReturnEmail, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(user.getId());
        representation.setFirstName(user.getFirstName());
        representation.setFirstName2(user.getFirstName2());
        representation.setFirstName3(user.getFirstName3());
        representation.setLastName(user.getLastName());
        representation.setFullName(user.getFullName());
        representation.setEmail(userService.getSecuredUserEmailAddress(user.getEmail(), currentUser, forceReturnEmail));

        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            representation.setAccountProfileUrl(userAccount.getLinkedinProfileUrl());
            representation.setAccountImageUrl(userAccount.getLinkedinImageUrl());
            representation.setPortraitImage(documentMapper.getDocumentRepresentation(userAccount.getPortraitImage()));
        }

        representation.setEditable(userService.checkUserEditable(user, currentUser));
        return representation;
    }

    private List<ResourceUserUnverifiedRepresentation> getUserUnverifiedRepresentations(User user) {
        Map<String, ResourceUserUnverifiedRepresentation> representations = newLinkedHashMap();
        TreeMultimap<UserRepresentationUnverified, PrismRoleContext> userRepresentationIndex = TreeMultimap.create();
        for (UnverifiedUserDTO unverifiedUser : userService.getUsersToVerify(user)) {
            Integer departmentId = unverifiedUser.getDepartmentId();
            Integer institutionId = unverifiedUser.getInstitutionId();
            String resourceKey = Joiner.on("|").skipNulls().join(institutionId, departmentId);

            List<UserRepresentationUnverified> userRepresentations;
            ResourceUserUnverifiedRepresentation representation = representations.get(resourceKey);
            if (representation == null) {
                userRepresentations = newLinkedList();
                representation = new ResourceUserUnverifiedRepresentation()
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
