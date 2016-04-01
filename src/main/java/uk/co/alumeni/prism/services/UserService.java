package uk.co.alumeni.prism.services;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.joda.time.DateTime.now;
import static org.springframework.beans.BeanUtils.instantiate;
import static uk.co.alumeni.prism.PrismConstants.ADDRESS_LOCATION_PRECISION;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.PrismConstants.SYSTEM_NOTIFICATION_INTERVAL;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.values;
import static uk.co.alumeni.prism.utils.PrismEncryptionUtils.getUUID;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.invokeMethod;
import static uk.co.alumeni.prism.utils.PrismStringUtils.getObfuscatedEmail;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import uk.co.alumeni.prism.dao.UserDAO;
import uk.co.alumeni.prism.dao.WorkflowDAO;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.UniqueEntity.EntitySignature;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.user.UserCompetence;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActivityMessageCountDTO;
import uk.co.alumeni.prism.dto.ProfileListRowDTO;
import uk.co.alumeni.prism.dto.UnverifiedUserDTO;
import uk.co.alumeni.prism.dto.UserOrganizationDTO;
import uk.co.alumeni.prism.dto.UserSelectionDTO;
import uk.co.alumeni.prism.exceptions.PrismValidationException;
import uk.co.alumeni.prism.exceptions.WorkflowPermissionException;
import uk.co.alumeni.prism.rest.UserDescriptor;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.UserListFilterDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.dto.user.UserAccountDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.dto.user.UserSimpleDTO;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismEncryptionUtils;
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

@Service
@Transactional
public class UserService {

    private final HashMultimap<Class<? extends UniqueEntity>, String> userAssignments = HashMultimap.create();

    @Inject
    private UserDAO userDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AddressService addressService;

    @Inject
    private AdvertService advertService;

    @Inject
    private RoleService roleService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private PrismJsonMappingUtils prismJsonMappingUtils;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SessionFactory sessionFactory;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void postConstruct() {
        Map<String, ClassMetadata> entities = sessionFactory.getAllClassMetadata();
        for (ClassMetadata metadata : entities.values()) {
            Class<?> entityClass = metadata.getMappedClass();
            if (entityClass != null) {
                Set<String> userProperties = getUserAssignments(entityClass, null);
                boolean isUserAssignment = !userProperties.isEmpty();
                if (UserAssignment.class.isAssignableFrom(entityClass) || Resource.class.isAssignableFrom(entityClass)) {
                    if (!isUserAssignment) {
                        throw new RuntimeException(entityClass.getSimpleName() + " is not a user assignment. It must not have a user reassignment module");
                    }
                    userAssignments.putAll((Class<? extends UniqueEntity>) entityClass, userProperties);
                } else if (isUserAssignment) {
                    throw new RuntimeException(entityClass.getSimpleName() + " is a user assignment. It must have a user reassignment module");
                }
            }
        }
    }

    public User getById(Integer id) {
        return entityService.getById(User.class, id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof User) {
            User user = (User) authentication.getDetails();
            user = getById(user.getId());
            if (user != null) {
                DateTime baseline = DateTime.now();
                user.setLastLoggedInTimestamp(baseline);
                roleService.acceptUnnacceptedUserRoles(user, baseline);
            }
            return user;
        }
        return null;
    }

    public User getOrCreateUser(UserDescriptor userDescriptor) {
        if (userDescriptor != null) {
            Integer id = userDescriptor.getId();
            if (id != null) {
                return getById(id);
            } else {
                String firstName = userDescriptor.getFirstName();
                String lastName = userDescriptor.getLastName();
                User transientUser = new User().withFirstName(firstName).withLastName(lastName).withFullName(firstName + " " + lastName)
                        .withEmail(userDescriptor.getEmail()).withCreatorUser(getCurrentUser());
                User persistentUser = entityService.getDuplicateEntity(transientUser);
                if (persistentUser == null) {
                    persistentUser = transientUser;
                    persistentUser.setActivationCode(getUUID());
                    entityService.save(persistentUser);
                    persistentUser.setParentUser(persistentUser);
                } else if (checkUserEditable(persistentUser, getCurrentUser())) {
                    persistentUser.setFirstName(firstName);
                    persistentUser.setLastName(lastName);
                }
                return persistentUser;
            }
        }
        return null;
    }

    public void getOrCreateUsersWithRoles(Resource resource, StateActionPendingDTO stateActionPendingDTO) {
        User user = getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user))) {
            stateService.createStateActionPending(resource, user, action, stateActionPendingDTO);
        }
    }

    public User getOrCreateUserWithRoles(User invoker, UserDescriptor userDescriptor, Resource resource, String message, Collection<PrismRole> roles) {
        User user = getOrCreateUser(userDescriptor);
        roleService.createUserRoles(invoker, resource, user, message, roles.toArray(new PrismRole[roles.size()]));
        return user;
    }

    public Set<String> getUserProperties(Class<? extends UniqueEntity> userAssignmentClass) {
        return userAssignments.get(userAssignmentClass);
    }

    public boolean enableUser(Integer userId) {
        UserAccount userAccount = getById(userId).getUserAccount();
        boolean wasEnabled = toBoolean(userAccount.getEnabled());
        userAccountService.enableUserAccount(userAccount);
        return !wasEnabled;
    }

    public void updateUser(UserSimpleDTO userDTO) {
        User currentUser = getCurrentUser();
        User dereferencedUser = getById(userDTO.getId());
        if (!(dereferencedUser == null || currentUser.equals(dereferencedUser))) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userDTO, "userDTO");
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
            errors.rejectValue("email", null, propertyLoader.loadLazy(SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE));
            throw new PrismValidationException("Cannot update user", errors);
        }

        currentUser.setFirstName(userDTO.getFirstName());
        currentUser.setLastName(userDTO.getLastName());
        currentUser.setFullName(currentUser.getFirstName() + " " + currentUser.getLastName());
        currentUser.setFirstName2(Strings.emptyToNull(userDTO.getFirstName2()));
        currentUser.setFirstName3(Strings.emptyToNull(userDTO.getFirstName3()));
        currentUser.setEmail(userDTO.getEmail());
    }

    public void updateUserAccount(UserAccountDTO userAccountDTO) {
        UserAccount userAccount = getCurrentUser().getUserAccount();
        userAccount.setSendActivityNotification(userAccountDTO.getSendActivityNotification());

        String password = userAccountDTO.getPassword();
        if (password != null) {
            userAccount.setPassword(PrismEncryptionUtils.getMD5(password));
            userAccount.setTemporaryPassword(null);
            userAccount.setTemporaryPasswordExpiryTimestamp(null);
        }
    }

    public User getUserByEmail(String email) {
        return entityService.getByProperty(User.class, "email", email);
    }

    public User getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    public User getByLinkedinId(String externalId) {
        return userDAO.getByLinkedinId(externalId);
    }

    public void resetPassword(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            UserAccount account = user.getUserAccount();
            if (account == null) {
                notificationService.sendCompleteRegistrationForgottenRequest(user);
            } else {
                String newPassword = PrismEncryptionUtils.getTemporaryPassword();
                notificationService.sendResetPasswordNotification(user, newPassword);
                account.setTemporaryPassword(PrismEncryptionUtils.getMD5(newPassword));
                account.setTemporaryPasswordExpiryTimestamp(new DateTime().plusDays(2));
            }
        }
    }

    public void linkUsers(User linkIntoUser, User linkFromUser) {
        if (linkFromUser != null && linkIntoUser != null) {
            userDAO.refreshParentUser(linkIntoUser, linkFromUser);
            linkFromUser.setParentUser(linkIntoUser);
        }
    }

    public void unlinkUser(Integer userId) {
        User user = getById(userId);
        if (Objects.equals(user.getId(), user.getParentUser().getId())) {
            User newParent = getCurrentUser();
            List<User> childUsers = Lists.asList(user, user.getChildUsers().toArray(new User[0]));
            for (User childUser : childUsers) {
                childUser.setParentUser(newParent);
            }
        }
        user.setParentUser(user);
    }

    public void setParentUser(String email) {
        User user = getUserByEmail(email);
        userDAO.setParentUser(user);
    }

    public List<User> getLinkedUsers(User user) {
        return userDAO.getLinkedUsers(user);
    }

    public List<UserSelectionDTO> getUsersInterestedInApplication(Application application) {
        TreeMap<String, UserSelectionDTO> orderedUsers = newTreeMap();

        Set<User> userNotInterestedIndex = newHashSet();
        Map<UserSelectionDTO, DateTime> userNotInterestedEvents = newHashMap();
        List<UserSelectionDTO> usersNotInterested = userDAO.getUsersNotInterestedInApplication(application);
        for (UserSelectionDTO userNotInterested : usersNotInterested) {
            userNotInterestedEvents.put(userNotInterested, userNotInterested.getEventTimestamp());
            userNotInterestedIndex.add(userNotInterested.getUser());
        }

        List<UserSelectionDTO> usersInterested = userDAO.getUsersInterestedInApplication(application);
        for (UserSelectionDTO userInterested : usersInterested) {
            DateTime userNotInterestedTimestamp = userNotInterestedEvents.get(userInterested);
            if (userNotInterestedTimestamp == null || userNotInterestedTimestamp.isBefore(userInterested.getEventTimestamp())) {
                orderedUsers.put(userInterested.getIndexName(), userInterested);
            }
        }

        DateTime baseline = now();
        for (ApplicationReferee applicationReferee : application.getReferees()) {
            User referee = applicationReferee.getUser();
            Comment referenceComment = applicationReferee.getComment();
            if ((referenceComment == null || isFalse(referenceComment.getDeclinedResponse())) && !userNotInterestedIndex.contains(referee)) {
                orderedUsers.put(referee.getFullName(), new UserSelectionDTO().withUser(referee).withEventTimestamp(baseline));
            }
        }

        return newLinkedList(orderedUsers.values());
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(Application application, List<UserSelectionDTO> usersToExclude) {
        ResourceParent parent = application.getResourceParent();

        List<Integer> programs = resourceService.getResourceIds(parent, PROGRAM);
        List<Integer> projects = resourceService.getResourceIds(parent, PROJECT);
        List<Integer> applications = resourceService.getResourceIds(parent, APPLICATION);

        List<UserSelectionDTO> usersToInclude = newLinkedList();
        List<UserSelectionDTO> users = userDAO.getUsersPotentiallyInterestedInApplication(programs, projects, applications);
        for (UserSelectionDTO userPotentiallyInterested : users) {
            if (!usersToExclude.contains(userPotentiallyInterested)) {
                usersToInclude.add(userPotentiallyInterested);
            }
        }

        return usersToInclude;
    }

    public List<UserRepresentationSimple> getSimilarUsers(String searchTerm) {
        User currentUser = getCurrentUser();

        if (isNotBlank(searchTerm)) {
            List<UserRepresentationSimple> similarUsers = userDAO.getSimilarUsers(searchTerm);
            similarUsers.forEach(similarUser -> {
                similarUser.setEmail(getSecuredUserEmailAddress(similarUser.getEmail(), currentUser));
                similarUser.setEditable(false);
            });
            return similarUsers;
        }

        return Lists.newArrayList();
    }

    public String getSecuredUserEmailAddress(String email, User currentUser) {
        return getSecuredUserEmailAddress(email, currentUser, false);
    }

    public String getSecuredUserEmailAddress(String email, User currentUser, boolean forceReturnEmail) {
        return (forceReturnEmail || Objects.equals(email, (currentUser == null ? null : currentUser.getEmail()))) ? email : getObfuscatedEmail(email);
    }

    public List<User> getResourceUsers(Resource resource, PrismRole role) {
        return userDAO.getResourceUsers(resource, role);
    }

    public List<User> getBouncedOrUnverifiedUsers(Resource resource, UserListFilterDTO userListFilterDTO) {
        User user = getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user))) {
            HashMultimap<PrismScope, Integer> enclosedResources = resourceService.getEnclosedResources(resource);
            return userDAO.getBouncedOrUnverifiedUsers(enclosedResources, userListFilterDTO);
        }
        return Lists.<User> newArrayList();
    }

    public void reassignBouncedOrUnverifiedUser(Resource resource, Integer userId, UserDTO userDTO) {
        User user = getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user))) {
            HashMultimap<PrismScope, Integer> enclosedResources = resourceService.getEnclosedResources(resource);
            User userBouncedOrUnverified = userDAO.getBouncedOrUnverifiedUser(userId, enclosedResources);

            String email = userDTO.getEmail();
            User userDuplicate = getUserByEmail(email);

            if (userBouncedOrUnverified != null && userDuplicate == null) {
                userBouncedOrUnverified.setFirstName(userDTO.getFirstName());
                userBouncedOrUnverified.setLastName(userDTO.getLastName());
                userBouncedOrUnverified.setFullName(userBouncedOrUnverified.getFirstName() + " " + userBouncedOrUnverified.getLastName());
                userBouncedOrUnverified.setEmail(userDTO.getEmail());
                userBouncedOrUnverified.setEmailBouncedMessage(null);
                notificationService.resetUserNotifications(userBouncedOrUnverified);
            } else if (userDuplicate != null) {
                mergeUsers(userBouncedOrUnverified, userDuplicate);
            } else {
                throw new WorkflowPermissionException(systemService.getSystem(), actionService.getById(SYSTEM_VIEW_APPLICATION_LIST));
            }
        }
    }

    public List<User> getUsersWithActions(Resource resource, PrismAction... actions) {
        Set<User> users = Sets.newHashSet();
        PrismScope scope = resource.getResourceScope();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        users.addAll(userDAO.getUsersWithActions(scope, resource, actions));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                users.addAll(userDAO.getUsersWithActions(scope, parentScope, resource, actions));
            }

            List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(scope);
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : WorkflowDAO.organizationScopes) {
                    for (PrismScope targetScope : WorkflowDAO.organizationScopes) {
                        users.addAll(userDAO.getUsersWithActions(scope, targeterScope, targetScope, targeterEntities, resource, actions));
                    }
                }
            }
        }

        return newArrayList(users);
    }

    public void updateUserCompetence(User user) {
        userDAO.getUserCompetences(user).forEach(uc -> {
            Integer ratingCount = uc.getRatingCount().intValue();
            entityService.createOrUpdate(new UserCompetence().withUser(uc.getUser()).withCompetence(uc.getCompetence()).withRatingCount(ratingCount)
                    .withRatingAverage(new BigDecimal(uc.getRatingSum()).divide(new BigDecimal(ratingCount), RATING_PRECISION, HALF_UP)));
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends UniqueEntity> boolean mergeUserAssignment(T oldAssignment, User newUser, String userProperty) {
        EntitySignature newSignature;
        try {
            newSignature = oldAssignment.getEntitySignature().clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        newSignature.addProperty(userProperty, newUser);

        T mergedAssignmentConflict = entityService.getDuplicateEntity((Class<T>) oldAssignment.getClass(), newSignature);
        if (mergedAssignmentConflict == null) {
            invokeMethod(oldAssignment, "set" + capitalize(userProperty), newUser);
            return true;
        }
        return false;
    }

    public <T extends UserAssignment<?> & UniqueEntity> void mergeUserAssignmentStrict(T oldAssignment, User newUser, String userProperty) {
        if (!mergeUserAssignment(oldAssignment, newUser, userProperty)) {
            entityService.delete(oldAssignment);
        }
    }

    public List<UnverifiedUserDTO> getUsersToVerify(User user) {
        Set<UnverifiedUserDTO> userRoles = newTreeSet();
        HashMultimap<PrismScope, Integer> resources = resourceService.getResourcesForWhichUserCanAdminister(user);
        if (!resources.isEmpty()) {
            for (PrismScope scope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
                Set<Integer> scopedResources = resources.get(scope);
                if (isNotEmpty(scopedResources)) {
                    userRoles.addAll(userDAO.getUsersToVerify(scope, resources.get(scope)));
                }
            }
        }
        return newLinkedList(userRoles);
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public Set<Integer> getUsersForActivityNotification() {
        Set<Integer> users = Sets.newHashSet();
        DateTime baseline = now().minusDays(SYSTEM_NOTIFICATION_INTERVAL);
        stream(values()).forEach(scope -> {
            users.addAll(userDAO.getUsersForActivityNotification(scope, baseline));
        });
        return users;
    }

    public Set<Integer> getUsersForReminderNotification() {
        Set<Integer> users = Sets.newHashSet();
        DateTime baseline = now().minusDays(SYSTEM_NOTIFICATION_INTERVAL);
        stream(values()).forEach(scope -> {
            users.addAll(userDAO.getUsersForReminderNotification(scope, baseline));
        });
        return users;
    }

    public List<ProfileListRowDTO> getUserProfiles(ProfileListFilterDTO filter, User currentUser) {
        return getUserProfiles(resourceService.getResourcesForWhichUserCanViewProfiles(currentUser), filter, currentUser, null);
    }

    public List<ProfileListRowDTO> getUserProfiles(User user) {
        HashMultimap<PrismScope, Integer> resourceIndex = resourceService.getResourcesForWhichUserCanViewProfiles(user);

        Set<ProfileListRowDTO> profiles = newLinkedHashSet();
        resourceIndex.keySet().forEach(scope -> profiles.addAll(userDAO.getUserProfiles(scope, resourceIndex.get(scope), user)));

        return newLinkedList(profiles);
    }

    public List<ProfileListRowDTO> getUserProfiles(HashMultimap<PrismScope, Integer> resourceIndex, ProfileListFilterDTO filter, User user,
            String lastSequenceIdentifier) {
        if (isTrue(filter.getWithNewMessages())) {
            filter.setUserIds(userDAO.getUsersWithUnreadMessages(user));
        }

        Set<ProfileListRowDTO> profiles = newLinkedHashSet();
        resourceIndex.keySet()
                .forEach(scope -> profiles.addAll(userDAO.getUserProfiles(scope, resourceIndex.get(scope), filter, user, lastSequenceIdentifier)));

        return newLinkedList(profiles);
    }

    public List<User> getUsersWithRoles(Resource resource, PrismRole... roles) {
        return (resource == null || isEmpty(roles)) ? emptyList() : userDAO.getUsersWithRoles(resource, roles);
    }

    public List<Integer> getUsersWithRoles(PrismScope scope, List<Integer> resources, PrismRole... roles) {
        return (isEmpty(resources) || isEmpty(roles)) ? emptyList() : userDAO.getUsersWithRoles(scope, resources, roles);
    }

    public List<Integer> getUsersWithRoles(PrismScope scope, PrismScope parentScope, List<Integer> resources, PrismRole... roles) {
        return (isEmpty(resources) || isEmpty(roles)) ? emptyList() : userDAO.getUsersWithRoles(scope, parentScope, resources, roles);
    }

    public UserDTO getUserDTO(User user) {
        return new UserDTO().withId(user.getId()).withFirstName(user.getFirstName()).withLastName(user.getLastName()).withEmail(user.getEmail());
    }

    public boolean checkUserEditable(User user, User currentUser) {
        UserAccount userAccount = user.getUserAccount();
        return (userAccount == null || isFalse(userAccount.getEnabled())) && equal(user.getCreatorUser(), currentUser);
    }

    public DateTime getUserCreatedTimestamp(User user) {
        return userDAO.getUserCreatedTimestamp(user);
    }

    public Set<Integer> getUsersWithActivitiesToCache(PrismScope scope, Integer resourceId, DateTime baseline) {
        Set<Integer> users = Sets.newHashSet();

        scopeService.getEnclosingScopesDescending(scope, SYSTEM).forEach(roleScope ->
                users.addAll(userDAO.getUsersWithActivitiesToCache(scope, roleScope, resourceId)));

        if (!scope.equals(SYSTEM)) {
            stream(organizationScopes).forEach(targeterScope -> {
                stream(organizationScopes).forEach(targetScope -> {
                    users.addAll(userDAO.getUsersWithActivitiesToCache(scope, targeterScope, targetScope, resourceId));
                });
            });
        }

        return users;
    }

    public void setUserActivityCache(Integer user, UserActivityRepresentation userActivityRepresentation, DateTime baseline) {
        UserAccount userAccount = getById(user).getUserAccount();
        userAccount.setActivityCache(prismJsonMappingUtils.writeValue(userActivityRepresentation));
        userAccount.setActivityCachedTimestamp(baseline);
    }

    public boolean checkUserCanViewUserProfile(User user, User currentUser) {
        return isNotEmpty(getUserProfiles(new ProfileListFilterDTO().withUserIds(asList(user.getId())), currentUser));
    }

    public Long getUserUnreadMessageCount(Collection<Integer> userIds, User currentUser) {
        return userDAO.getUserUnreadMessageCount(userIds, currentUser);
    }

    public Integer getUserReadMessageCount(User user, User currentUser) {
        if (user.equals(currentUser)) {
            return userDAO.getUserReadMessageCounts(currentUser).intValue();
        }
        return getFirstUserMessageCount(getUserReadMessageCounts(newArrayList(user.getId()), currentUser));
    }

    public Integer getUserUnreadMessageCount(User user, User currentUser) {
        if (user.equals(currentUser)) {
            return userDAO.getUserUnreadMessageCounts(currentUser).intValue();
        }
        return getFirstUserMessageCount(getUserUnreadMessageCounts(newArrayList(user.getId()), currentUser));
    }

    public List<ActivityMessageCountDTO> getUserReadMessageCounts(Collection<Integer> userIds, User currentUser) {
        return userDAO.getUserReadMessageCounts(userIds, currentUser);
    }

    public List<ActivityMessageCountDTO> getUserUnreadMessageCounts(Collection<Integer> userIds, User currentUser) {
        return userDAO.getUserUnreadMessageCounts(userIds, currentUser);
    }

    public Integer getMaximumUserAccountCompleteScore() {
        return userDAO.getMaximumUserAccountCompleteScore();
    }

    public List<Integer> getUserAccounts() {
        return userDAO.getUserAccounts();
    }

    public LinkedHashMultimap<Integer, String> getUserLocations(Collection<Integer> userIds) {
        return addressService.getAddressLocationIndex(userDAO.getUserLocations(userIds), ADDRESS_LOCATION_PRECISION);
    }

    public TreeMultimap<Integer, UserOrganizationDTO> getUserOrganizations(Collection<Integer> userIds, HashMultimap<PrismScope, Integer> resourceIndex,
            PrismRoleCategory roleCategory) {
        TreeMultimap<Integer, UserOrganizationDTO> userResourceParents = TreeMultimap.create();
        Arrays.stream(organizationScopes).forEach(
                os -> userDAO.getUserOrganizations(userIds, os, resourceIndex.get(os), roleCategory).forEach(
                        urp -> userResourceParents.put(urp.getUserId(), urp)));
        return userResourceParents;
    }

    @SuppressWarnings("unchecked")
    private void mergeUsers(User oldUser, User newUser) {
        for (Entry<Class<? extends UniqueEntity>, String> userAssignmentEntry : userAssignments.entries()) {
            Class<? extends UniqueEntity> userAssignmentClass = userAssignmentEntry.getKey();
            if (UserAssignment.class.isAssignableFrom(userAssignmentClass)) {
                UserAssignment<?> userAssignment = instantiate((Class<? extends UserAssignment<?>>) userAssignmentClass);
                applicationContext.getBean(userAssignment.getUserReassignmentProcessor()).reassign(oldUser, newUser, userAssignmentEntry.getValue());
            } else if (Resource.class.isAssignableFrom(userAssignmentClass)) {
                Resource resource = BeanUtils.instantiate((Class<? extends Resource>) userAssignmentClass);
                resourceService.getResourcesByUser(resource.getResourceScope(), oldUser).forEach(
                        resourceAssignment -> resourceService.reassignResource(resource, newUser, userAssignmentEntry.getValue()));
            } else {
                throw new Error("Attempted to merge invalid role assignment");
            }
        }
    }

    private Set<String> getUserAssignments(Class<?> entityClass, Set<String> userAssignments) {
        userAssignments = userAssignments == null ? Sets.newHashSet() : userAssignments;
        for (Field entityProperty : entityClass.getDeclaredFields()) {
            if (User.class.isAssignableFrom(entityProperty.getType())
                    && !(entityProperty.getAnnotation(Column.class) == null && entityProperty.getAnnotation(JoinColumn.class) == null)) {
                userAssignments.add(entityProperty.getName());
            }
        }

        if (userAssignments.isEmpty()) {
            Class<?> entitySuperClass = entityClass.getSuperclass();
            if (!Object.class.equals(entitySuperClass)) {
                return getUserAssignments(entitySuperClass, userAssignments);
            }
        }

        return userAssignments;
    }

    private Integer getFirstUserMessageCount(List<ActivityMessageCountDTO> counts) {
        for (ActivityMessageCountDTO count : counts) {
            return count.getMessageCount().intValue();
        }
        return null;
    }

}
