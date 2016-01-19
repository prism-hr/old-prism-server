package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.PrismConstants.ACTIVITY_NOTIFICATION_INTERVAL;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.values;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
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

import org.apache.commons.lang.StringUtils;
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

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.co.alumeni.prism.dao.UserDAO;
import uk.co.alumeni.prism.dao.WorkflowDAO;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.UniqueEntity.EntitySignature;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismUserInstitutionIdentity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.user.UserCompetence;
import uk.co.alumeni.prism.domain.user.UserInstitutionIdentity;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ProfileListRowDTO;
import uk.co.alumeni.prism.dto.UnverifiedUserDTO;
import uk.co.alumeni.prism.dto.UserSelectionDTO;
import uk.co.alumeni.prism.exceptions.PrismValidationException;
import uk.co.alumeni.prism.exceptions.WorkflowPermissionException;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.UserListFilterDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.dto.user.UserAccountDTO;
import uk.co.alumeni.prism.rest.dto.user.UserCorrectionDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.dto.user.UserSimpleDTO;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismEncryptionUtils;
import uk.co.alumeni.prism.utils.PrismReflectionUtils;

@Service
@Transactional
public class UserService {

    private final HashMultimap<Class<? extends UniqueEntity>, String> userAssignments = HashMultimap.create();

    @Inject
    private UserDAO userDAO;

    @Inject
    private ActionService actionService;

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

    public User getOrCreateUser(UserDTO userDTO) {
        return userDTO == null ? null : getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
    }

    public User getOrCreateUser(String firstName, String lastName, String email) {
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withFullName(firstName + " " + lastName).withEmail(email);
        User duplicateUser = entityService.getDuplicateEntity(transientUser);
        if (duplicateUser == null) {
            transientUser.setActivationCode(PrismEncryptionUtils.getUUID());
            entityService.save(transientUser);
            transientUser.setParentUser(transientUser);
            return transientUser;
        } else {
            return duplicateUser;
        }
    }

    public void getOrCreateUsersWithRoles(Resource resource, StateActionPendingDTO stateActionPendingDTO) {
        User user = getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user))) {
            stateService.createStateActionPending(resource, user, action, stateActionPendingDTO);
        }
    }

    public User getOrCreateUserWithRoles(User invoker, String firstName, String lastName, String email, Resource resource, String message, List<PrismRole> roles) {
        User user = getOrCreateUser(firstName, lastName, email);
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
        User user = getCurrentUser();
        User userByEmail = getUserByEmail(userDTO.getEmail());
        if (!(userByEmail == null || user.equals(userByEmail))) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userDTO, "userDTO");
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
            errors.rejectValue("email", null, propertyLoader.loadLazy(PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE));
            throw new PrismValidationException("Cannot update user", errors);
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        user.setFirstName2(Strings.emptyToNull(userDTO.getFirstName2()));
        user.setFirstName3(Strings.emptyToNull(userDTO.getFirstName3()));
        user.setEmail(userDTO.getEmail());
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

    public String getUserInstitutionIdentity(User user, Institution institution, PrismUserInstitutionIdentity identityType) {
        return userDAO.getUserInstitutionId(user, institution, identityType);
    }

    public List<UserSelectionDTO> getUsersInterestedInApplication(Application application) {
        TreeMap<String, UserSelectionDTO> orderedUsers = Maps.newTreeMap();

        Map<UserSelectionDTO, DateTime> userNotInterestedEvents = Maps.newHashMap();
        List<UserSelectionDTO> usersNotInterested = userDAO.getUsersNotInterestedInApplication(application);
        for (UserSelectionDTO userNotInterested : usersNotInterested) {
            userNotInterestedEvents.put(userNotInterested, userNotInterested.getEventTimestamp());
        }

        List<UserSelectionDTO> usersInterested = userDAO.getUsersInterestedInApplication(application);
        for (UserSelectionDTO userInterested : usersInterested) {
            DateTime userNotInterestedTimestamp = userNotInterestedEvents.get(userInterested);
            if (userNotInterestedTimestamp == null || userNotInterestedTimestamp.isBefore(userInterested.getEventTimestamp())) {
                orderedUsers.put(userInterested.getIndexName(), userInterested);
            }
        }

        return Lists.newLinkedList(orderedUsers.values());
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(Application application, List<UserSelectionDTO> usersToExclude) {
        ResourceParent parent = application.getResourceParent();

        List<Integer> programs = resourceService.getResourceIds(parent, PROGRAM);
        List<Integer> projects = resourceService.getResourceIds(parent, PROJECT);
        List<Integer> applications = resourceService.getResourceIds(parent, APPLICATION);

        List<UserSelectionDTO> usersToInclude = Lists.newLinkedList();
        List<UserSelectionDTO> users = userDAO.getUsersPotentiallyInterestedInApplication(programs, projects, applications);
        for (UserSelectionDTO userPotentiallyInterested : users) {
            if (!usersToExclude.contains(userPotentiallyInterested)) {
                usersToInclude.add(userPotentiallyInterested);
            }
        }

        return usersToInclude;
    }

    public List<UserRepresentationSimple> getSimilarUsers(String searchTerm) {
        String trimmedSearchTerm = StringUtils.trim(searchTerm);

        if (trimmedSearchTerm.length() >= 1) {
            return userDAO.getSimilarUsers(trimmedSearchTerm);
        }

        return Lists.newArrayList();
    }

    public List<User> getResourceUsers(Resource resource) {
        return userDAO.getResourceUsers(resource);
    }

    public List<User> getResourceUsers(Resource resource, PrismRole role) {
        return userDAO.getResourceUsers(resource, role);
    }

    public void createOrUpdateUserInstitutionIdentity(Application application, String exportUserId) {
        UserInstitutionIdentity transientUserInstitutionIdentity = new UserInstitutionIdentity().withUser(application.getUser())
                .withInstitution(application.getInstitution()).withIdentityType(PrismUserInstitutionIdentity.STUDY_APPLICANT)
                .withIdentitier(exportUserId);
        entityService.createOrUpdate(transientUserInstitutionIdentity);
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

    public void reassignBouncedOrUnverifiedUser(Resource resource, Integer userId, UserCorrectionDTO userCorrectionDTO) {
        User user = getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user))) {
            HashMultimap<PrismScope, Integer> enclosedResources = resourceService.getEnclosedResources(resource);
            User bouncedOrUnverifiedUser = userDAO.getBouncedOrUnverifiedUser(userId, enclosedResources);

            String email = userCorrectionDTO.getEmail();
            User userDuplicate = getUserByEmail(email);

            if (bouncedOrUnverifiedUser != null && userDuplicate == null) {
                bouncedOrUnverifiedUser.setFirstName(userCorrectionDTO.getFirstName());
                bouncedOrUnverifiedUser.setLastName(userCorrectionDTO.getLastName());
                bouncedOrUnverifiedUser.setFullName(bouncedOrUnverifiedUser.getFirstName() + " " + bouncedOrUnverifiedUser.getLastName());
                bouncedOrUnverifiedUser.setEmail(userCorrectionDTO.getEmail());
                bouncedOrUnverifiedUser.setEmailBouncedMessage(null);
                notificationService.resetUserNotifications(bouncedOrUnverifiedUser);
            } else if (userDuplicate != null) {
                mergeUsers(bouncedOrUnverifiedUser, userDuplicate);
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
            PrismReflectionUtils.invokeMethod(oldAssignment, "set" + capitalize(userProperty), newUser);
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
        HashMultimap<PrismScope, Integer> resources = resourceService.getResourcesForWhichUserCanAdminister(user);
        Set<UnverifiedUserDTO> userRoles = Sets.newTreeSet();
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

    public Set<Integer> getUsersForActivityRepresentation() {
        Set<Integer> users = Sets.newHashSet();
        DateTime baseline = now().minusDays(ACTIVITY_NOTIFICATION_INTERVAL);
        stream(values()).forEach(scope -> {
            users.addAll(userDAO.getUsersForActivityNotification(scope, baseline));
        });
        return users;
    }

    public List<ProfileListRowDTO> getUserProfiles(ProfileListFilterDTO filter) {
        User user = getCurrentUser();

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        Arrays.stream(WorkflowDAO.organizationScopes).forEach(ts -> resources.putAll(ts,
                resourceService.getResources(user, ts, scopeService.getParentScopesDescending(ts, SYSTEM)).stream().map(d -> d.getId()).collect(toList())));

        Set<ProfileListRowDTO> profiles = Sets.newLinkedHashSet();
        resources.keySet().forEach(scope -> profiles.addAll(userDAO.getUserProfiles(scope, resources.get(scope), filter)));

        return newLinkedList(profiles);
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

    @SuppressWarnings("unchecked")
    private void mergeUsers(User oldUser, User newUser) {
        for (Entry<Class<? extends UniqueEntity>, String> userAssignmentEntry : userAssignments.entries()) {
            Class<? extends UniqueEntity> userAssignmentClass = userAssignmentEntry.getKey();
            if (UserAssignment.class.isAssignableFrom(userAssignmentClass)) {
                UserAssignment<?> userAssignment = BeanUtils.instantiate((Class<? extends UserAssignment<?>>) userAssignmentClass);
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

}
