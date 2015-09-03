package com.zuehlke.pgadmissions.services;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.UniqueEntity.EntitySignature;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.*;
import com.zuehlke.pgadmissions.dto.UserCompetenceDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.HibernateUtils;
import com.zuehlke.pgadmissions.utils.PrismQueryUtils;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.IMAGE;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.invokeMethod;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang.WordUtils.capitalize;

@Service
@Transactional
public class UserService {

    private HashMultimap<Class<? extends UniqueEntity>, String> userAssignments = HashMultimap.create();

    @Inject
    private UserDAO userDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ProgramService programService;

    @Inject
    private RoleService roleService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EntityService entityService;

    @Inject
    private DocumentService documentService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SessionFactory sessionFactory;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void postConstruct() throws Exception {
        Map<String, ClassMetadata> entities = sessionFactory.getAllClassMetadata();
        for (ClassMetadata metadata : entities.values()) {
            Class<?> entityClass = metadata.getMappedClass();
            if (entityClass != null) {
                Set<String> userProperties = getUserAssignments(entityClass, null);
                boolean isUserAssignment = !userProperties.isEmpty();
                if (UserAssignment.class.isAssignableFrom(entityClass) || Resource.class.isAssignableFrom(entityClass)) {
                    if (!isUserAssignment) {
                        throw new Exception(entityClass.getSimpleName() + " is not a user assignment. It must not have a user reassignment module");
                    }
                    userAssignments.putAll((Class<? extends UniqueEntity>) entityClass, userProperties);
                } else if (isUserAssignment) {
                    throw new Exception(entityClass.getSimpleName() + " is a user assignment. It must have a user reassignment module");
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

    public User getOrCreateUser(String firstName, String lastName, String email) throws DeduplicationException {
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withFullName(firstName + " " + lastName).withEmail(email);
        User duplicateUser = entityService.getDuplicateEntity(transientUser);
        if (duplicateUser == null) {
            transientUser.setActivationCode(EncryptionUtils.getUUID());
            entityService.save(transientUser);
            transientUser.setParentUser(transientUser);
            return transientUser;
        } else {
            return duplicateUser;
        }
    }

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource, List<PrismRole> roles) {
        User user = getOrCreateUser(firstName, lastName, email);
        roleService.assignUserRoles(resource, user, PrismRoleTransitionType.CREATE, roles.toArray(new PrismRole[roles.size()]));
        return user;
    }

    public void createOrUpdateUserProgram(User user, ImportedProgram importedProgram) {
        entityService.createOrUpdate(new UserProgram().withUser(user).withProgram(importedProgram));
        createOrUpdateUserAdverts(user, importedProgram);
    }

    public Long getUserProgramRelationCount(User user, ImportedProgram importedProgram) {
        return userDAO.getUserProgramRelationCount(user, importedProgram);
    }

    public void deleteUserProgram(User user, ImportedProgram importedProgram) {
        userDAO.deleteUserProgram(user, importedProgram);
        userDAO.deleteUserAdvert(user);
        entityService.flush();

        user.getUserPrograms().forEach(userProgram -> {
            createOrUpdateUserAdverts(user, importedProgram);
        });
    }

    public Set<String> getUserProperties(Class<? extends UniqueEntity> userAssignmentClass) {
        return userAssignments.get(userAssignmentClass);
    }

    public boolean activateUser(Integer userId, PrismAction actionId, Integer resourceId) {
        User user = getById(userId);
        boolean wasEnabled = user.getUserAccount().getEnabled();
        user.getUserAccount().setEnabled(true);
        return !wasEnabled;
    }

    public void updateUser(UserDTO userDTO) {
        User user = getCurrentUser();
        User userByEmail = getUserByEmail(userDTO.getEmail());
        if (userByEmail != null && !HibernateUtils.sameEntities(userByEmail, user)) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userDTO, "userDTO");
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem());
            errors.rejectValue("email", null, propertyLoader.loadLazy(PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE));
            throw new PrismValidationException("Cannot update user", errors);
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        user.setFirstName2(Strings.emptyToNull(userDTO.getFirstName2()));
        user.setFirstName3(Strings.emptyToNull(userDTO.getFirstName3()));
        user.setEmail(userDTO.getEmail());

        UserAccount userAccount = user.getUserAccount();

        Integer portraitDocumentId = userDTO.getPortraitDocument();
        if (portraitDocumentId != null) {
            userAccount.setPortraitImage(documentService.getById(userDTO.getPortraitDocument(), IMAGE));
        }

        userAccount.setSendApplicationRecommendationNotification(userDTO.getSendApplicationRecommendationNotification());

        String password = userDTO.getPassword();
        if (password != null) {
            userAccount.setPassword(EncryptionUtils.getMD5(password));
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

    public User getByExternalAccountId(OauthProvider oauthProvider, String externalId) {
        return userDAO.getByExternalAccountId(oauthProvider, externalId);
    }

    public void resetPassword(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            UserAccount account = user.getUserAccount();
            if (account == null) {
                User superAdmin = getUserByEmail("systemUserEmail");
                notificationService.sendInvitationNotification(superAdmin, user);
            } else {
                String newPassword = EncryptionUtils.getTemporaryPassword();
                notificationService.sendResetPasswordNotification(user, newPassword);
                account.setTemporaryPassword(EncryptionUtils.getMD5(newPassword));
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
        user.setParentUser(user);
    }

    public void selectParentUser(String email) {
        User user = getUserByEmail(email);
        userDAO.selectParentUser(user);
    }

    public List<String> getLinkedUserAccounts(User user) {
        return userDAO.getLinkedUserAccounts(user);
    }

    public List<User> getUsersForResourceAndRoles(Resource resource, PrismRole... roleIds) {
        return userDAO.getUsersForResourceAndRoles(resource, roleIds);
    }

    public List<User> getUsersForResourcesAndRoles(Set<Resource> resources, PrismRole... roleIds) {
        return userDAO.getUsersForResourcesAndRoles(resources, roleIds);
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

        List<UserSelectionDTO> suggestedSupervisors = userDAO.getSuggestedSupervisors(application);
        for (UserSelectionDTO suggestedSupervisor : suggestedSupervisors) {
            if (!(orderedUsers.containsValue(suggestedSupervisor) || userNotInterestedEvents.containsKey(suggestedSupervisor))) {
                orderedUsers.put(suggestedSupervisor.getIndexName(), suggestedSupervisor);
            }
        }

        return Lists.newLinkedList(orderedUsers.values());
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(Application application, List<UserSelectionDTO> usersToExclude) {
        List<UserSelectionDTO> usersToInclude = Lists.newLinkedList();

        Program program = application.getProgram();
        if (program != null) {
            Integer programId = program.getId();

            List<Integer> projects = programService.getProjects(programId);
            List<Integer> applications = programService.getApplications(programId);
            List<UserSelectionDTO> users = userDAO.getUsersPotentiallyInterestedInApplication(programId, projects, applications);
            for (UserSelectionDTO userPotentiallyInterested : users) {
                if (!usersToExclude.contains(userPotentiallyInterested)) {
                    usersToInclude.add(userPotentiallyInterested);
                }
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

    public void createOrUpdateUserInstitutionIdentity(Application application, String exportUserId) {
        UserInstitutionIdentity transientUserInstitutionIdentity = new UserInstitutionIdentity().withUser(application.getUser())
                .withInstitution(application.getInstitution()).withIdentityType(PrismUserInstitutionIdentity.STUDY_APPLICANT)
                .withIdentitier(exportUserId);
        entityService.createOrUpdate(transientUserInstitutionIdentity);
    }

    public boolean isCurrentUser(User user) {
        User currentUser = getCurrentUser();
        return !(user == null || currentUser == null) && Objects.equal(user.getId(), getCurrentUser().getId());
    }

    public boolean isLoggedInSession() {
        return getCurrentUser() != null;
    }

    public List<User> getBouncedOrUnverifiedUsers(Resource resource, UserListFilterDTO userListFilterDTO) {
        HashMultimap<PrismScope, Integer> administratorResources = resourceService.getUserAdministratorResources(getCurrentUser());
        if (!administratorResources.isEmpty()) {
            HashMultimap<PrismScope, PrismScope> expandedScopes = scopeService.getExpandedScopes(resource.getResourceScope());
            return userDAO.getBouncedOrUnverifiedUsers(resource, administratorResources, expandedScopes, userListFilterDTO);
        }
        return Lists.<User> newArrayList();
    }

    public void correctBouncedOrUnverifiedUser(Resource resource, Integer userId, UserCorrectionDTO userCorrectionDTO) {
        HashMultimap<PrismScope, Integer> administratorResources = resourceService.getUserAdministratorResources(getCurrentUser());
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

    public void createUserConnection(User userConnected) {
        createUserConnection(getCurrentUser(), userConnected);
    }

    public void createUserConnection(User userRequested, User userConnected) {
        if (!userRequested.equals(userConnected)) {
            UserConnection connection = userDAO.getUserConnection(userRequested, userConnected);
            if (connection == null) {
                entityService.save(new UserConnection().withUserRequested(userRequested).withUserConnected(userConnected).withConnected(false)
                        .withCreatedTimestamp(new DateTime()));
            }
        }
    }

    public void createUserConnections(List<User> users) {
        for (int i = users.size(); i < 0; i--) {
            User request = null;
            List<User> userConnects = users.subList(0, 1);
            for (User userConnect : userConnects) {
                if (request == null) {
                    request = userConnect;
                } else {
                    createUserConnection(request, userConnect);
                }
            }
        }
    }

    public void acceptUserConnection(User userConnected) {
        UserConnection connection = userDAO.getUserConnectionStrict(getCurrentUser(), userConnected);
        if (connection != null) {
            connection.setConnected(true);
        }
    }

    public void deleteUserConnection(User userConnected) {
        UserConnection connection = userDAO.getUserConnection(getCurrentUser(), userConnected);
        if (connection != null) {
            entityService.delete(connection);
        }
    }

    public List<UserConnection> getUserConnections(User user) {
        Map<String, UserConnection> connections = Maps.newTreeMap();
        for (UserConnection connection : user.getRequestedConnections()) {
            connections.put(connection.getUserRequested().getFullName(), connection);
        }

        for (UserConnection connection : user.getConnectedConnections()) {
            connections.put(connection.getUserConnected().getFullName(), connection);
        }

        return Lists.newLinkedList(connections.values());
    }

    public void updateUserCompetence(User user) {
        List<String> rows = Lists.newArrayList();
        for (UserCompetenceDTO userCompetence : userDAO.getUserCompetences(user)) {
            List<String> columns = Lists.newLinkedList();
            columns.add(prepareIntegerForSqlInsert(userCompetence.getUser()));
            columns.add(prepareIntegerForSqlInsert(userCompetence.getCompetence()));

            Integer ratingCount = userCompetence.getRatingCount().intValue();
            columns.add(prepareIntegerForSqlInsert(ratingCount));
            columns.add(prepareDecimalForSqlInsert(userCompetence.getRatingSum().divide(new BigDecimal(ratingCount), RATING_PRECISION, HALF_UP)));

            rows.add("(" + prepareColumnsForSqlInsert(columns) + ")");
        }

        if (!rows.isEmpty()) {
            entityService.executeBulkInsertUpdate("user_competence", "user_id, competence_id, rating_count, rating_average",
                    PrismQueryUtils.prepareRowsForSqlInsert(rows), "rating_count = values(rating_count), rating_average = values(rating_average)");
        }
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

    @SuppressWarnings("unchecked")
    private void mergeUsers(User oldUser, User newUser) {
        for (Entry<Class<? extends UniqueEntity>, String> userAssignmentEntry : userAssignments.entries()) {
            Class<? extends UniqueEntity> userAssignmentClass = userAssignmentEntry.getKey();
            if (UserAssignment.class.isAssignableFrom(userAssignmentClass)) {
                UserAssignment<?> userAssignment = BeanUtils.instantiate((Class<? extends UserAssignment<?>>) userAssignmentClass);
                applicationContext.getBean(userAssignment.getUserReassignmentProcessor()).reassign(oldUser, newUser, userAssignmentEntry.getValue());
            } else if (Resource.class.isAssignableFrom(userAssignmentClass)) {
                Resource resource = BeanUtils.instantiate((Class<? extends Resource>) userAssignmentClass);
                resourceService.getResourcesByUser(resource.getResourceScope(), oldUser).forEach(resourceAssigment -> {
                    resourceService.reassignResource(resource, newUser, userAssignmentEntry.getValue());
                });
            } else {
                throw new Error("Attempted to merge invalid role assignment");
            }
        }
    }

    private Set<String> getUserAssignments(Class<?> entityClass, Set<String> userAssignments) {
        userAssignments = userAssignments == null ? Sets.newHashSet() : userAssignments;
        for (Field entityProperty : entityClass.getDeclaredFields()) {
            if (User.class.isAssignableFrom(entityProperty.getType())) {
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

    private void createOrUpdateUserAdverts(User user, ImportedProgram importedProgram) {
        List<Department> departments = departmentService.getDepartmentsByImportedProgram(importedProgram);
        if (departments.isEmpty()) {
            Institution institution = institutionService.getInstitutionByImportedProgram(importedProgram);
            if (institution != null) {
                entityService.createOrUpdate(new UserAdvert().withUser(user).withAdvert(institution.getAdvert()).withIdentified(false));
            }
        } else {
            departments.forEach(department -> {
                entityService.createOrUpdate(new UserAdvert().withUser(user).withAdvert(department.getAdvert()).withIdentified(false));
                entityService.createOrUpdate(new UserAdvert().withUser(user).withAdvert(department.getInstitution().getAdvert()).withIdentified(false));
            });
        }
    }

}
