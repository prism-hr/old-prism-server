package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.targetScopes;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.invokeMethod;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.WordUtils.capitalize;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import org.joda.time.LocalDate;
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
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.UniqueEntity.EntitySignature;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.domain.user.UserCompetence;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ProfileListRowDTO;
import com.zuehlke.pgadmissions.dto.UnverifiedUserDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.StateActionPendingDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserAccountDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismEncryptionUtils;

@Service
@Transactional
public class UserService {

    private final HashMultimap<Class<? extends UniqueEntity>, String> userAssignments = HashMultimap.create();

    @Inject
    private UserDAO userDAO;

    @Inject
    private ActionService actionService;

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
            return entityService.getById(User.class, user.getId());
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
        if (!(action == null || !actionService.checkActionExecutable(resource, action, user, false))) {
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

    public boolean activateUser(Integer userId) {
        User user = getById(userId);
        boolean wasEnabled = user.getUserAccount().getEnabled();
        user.getUserAccount().setEnabled(true);
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
        userAccount.setSendApplicationRecommendationNotification(userAccountDTO.getSendApplicationRecommendationNotification());

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
        List<UserSelectionDTO> usersToInclude = Lists.newLinkedList();

        Department department = application.getDepartment();
        Institution institution = application.getInstitution();
        ResourceParent parent = department == null ? institution : department;

        List<Integer> programs = parent.getPrograms().stream().map(p -> p.getId()).collect(toList());
        List<Integer> projects = parent.getProjects().stream().map(p -> p.getId()).collect(toList());
        List<Integer> applications = parent.getApplications().stream().map(a -> a.getId()).collect(toList());

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
        HashMultimap<PrismScope, Integer> administratorResources = resourceService.getResourcesForWhichUserCanAdminister(getCurrentUser());
        if (!administratorResources.isEmpty()) {
            HashMultimap<PrismScope, PrismScope> expandedScopes = scopeService.getExpandedScopes(resource.getResourceScope());
            return userDAO.getBouncedOrUnverifiedUsers(resource, administratorResources, expandedScopes, userListFilterDTO);
        }
        return Lists.<User> newArrayList();
    }

    public void reassignBouncedOrUnverifiedUser(Resource resource, Integer userId, UserCorrectionDTO userCorrectionDTO) {
        HashMultimap<PrismScope, Integer> administratorResources = resourceService.getResourcesForWhichUserCanAdminister(getCurrentUser());
        User user = userDAO.getBouncedOrUnverifiedUser(userId, resource, administratorResources, scopeService.getExpandedScopes(resource.getResourceScope()));

        String email = userCorrectionDTO.getEmail();
        User userDuplicate = getUserByEmail(email);

        if (user != null && userDuplicate == null) {
            user.setFirstName(userCorrectionDTO.getFirstName());
            user.setLastName(userCorrectionDTO.getLastName());
            user.setFullName(user.getFirstName() + " " + user.getLastName());
            user.setEmail(userCorrectionDTO.getEmail());
            user.setEmailBouncedMessage(null);
            notificationService.resetNotifications(user);
        } else if (userDuplicate != null) {
            mergeUsers(user, userDuplicate);
        } else {
            throw new WorkflowPermissionException(systemService.getSystem(), actionService.getById(SYSTEM_VIEW_APPLICATION_LIST));
        }
    }

    public List<User> getUsersWithAction(Resource resource, PrismAction... actions) {
        return userDAO.getUsersWithAction(resource, actions);
    }

    public void updateUserCompetence(User user) {
        userDAO.getUserCompetences(user).forEach(uc -> {
            Integer ratingCount = uc.getRatingCount().intValue();
            entityService.createOrUpdate(new UserCompetence().withUser(uc.getUser()).withCompetence(uc.getCompetence()).withRatingCount(ratingCount)
                    .withRatingAverage(uc.getRatingSum().divide(new BigDecimal(ratingCount), RATING_PRECISION, HALF_UP)));
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

    public Set<Integer> getUsersWithActivity() {
        Set<Integer> users = Sets.newHashSet();
        DateTime updateBaseline = new DateTime().minusDays(1);
        LocalDate lastNotifiedBaseline = updateBaseline.toLocalDate().minusDays(1);

        scopeService.getEnclosingScopesDescending(APPLICATION, SYSTEM).forEach(scope -> {
            users.addAll(userDAO.getUsersWithActivity(scope, updateBaseline, lastNotifiedBaseline));
            scopeService.getParentScopesDescending(scope, SYSTEM).forEach(parentScope -> {
                users.addAll(userDAO.getUsersWithActivity(scope, parentScope, updateBaseline, lastNotifiedBaseline));
            });

            for (PrismScope targeterScope : targetScopes) {
                if (scope.ordinal() > targeterScope.ordinal()) {
                    for (PrismScope targetScope : targetScopes) {
                        users.addAll(userDAO.getUsersWithActivity(scope, targeterScope, targetScope, updateBaseline, lastNotifiedBaseline));

                        List<Integer> resources = resourceService.getResourcesWithNewOpportunities(PROJECT, targeterScope, targetScope, updateBaseline);
                        if (!resources.isEmpty()) {
                            users.addAll(userDAO.getUsersWithVerifiedRoles(targetScope, resources));

                            if (targetScope.equals(DEPARTMENT)) {
                                users.addAll(userDAO.getUsersWithVerifiedRolesForChildResource(INSTITUTION, targetScope, resources));
                            }
                        }
                    }
                }
            }
        });

        for (PrismScope targeterScope : targetScopes) {
            for (PrismScope targetScope : targetScopes) {
                List<Integer> resources = resourceService.getResourcesWithNewOpportunities(PROJECT, targeterScope, targetScope, updateBaseline);
                if (!resources.isEmpty()) {
                    users.addAll(userDAO.getUsersWithVerifiedRoles(targetScope, resources));

                    if (targetScope.equals(DEPARTMENT)) {
                        users.addAll(userDAO.getUsersWithVerifiedRolesForChildResource(INSTITUTION, targetScope, resources));
                    }
                }
            }
        }

        users.addAll(userDAO.getUsersWithAppointmentsForApplications());

        users.addAll(userDAO.getUsersWithConnectionsToVerify());
        for (PrismScope parentScope : targetScopes) {
            List<Integer> resources = resourceService.getResourcesWithUsersToVerify(parentScope);
            if (!resources.isEmpty()) {
                users.addAll(userDAO.getUsersWithUsersToVerify(parentScope, resources));
            }
            users.addAll(userDAO.getUsersWithConnectionsToVerify(parentScope));
        }

        return users;
    }

    public List<ProfileListRowDTO> getUserProfiles(ProfileListFilterDTO filter) {
        User user = getCurrentUser();
        List<Integer> departments = resourceService.getResources(user, DEPARTMENT, asList(INSTITUTION, SYSTEM)).stream().map(d -> d.getId()).collect(toList());
        return userDAO.getUserProfiles(departments, filter);
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
