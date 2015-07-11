package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.IMAGE;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserConnection;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class UserService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationSectionService applicationSectionService;

    @Inject
    private ProgramService programService;

    @Inject
    private RoleService roleService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EntityService entityService;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

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

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource, List<PrismRole> roles) throws Exception {
        User user = getOrCreateUser(firstName, lastName, email);
        roleService.assignUserRoles(resource, user, PrismRoleTransitionType.CREATE, roles.toArray(new PrismRole[roles.size()]));
        return user;
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
            errors.rejectValue("email", PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE.name());
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
                notificationService.sendInvitationNotifications(superAdmin, user);
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

    public List<Integer> getMatchingUsers(String searchTerm) {
        return userDAO.getMatchingUsers(searchTerm);
    }

    public void createOrUpdateUserInstitutionIdentity(Application application, String exportUserId) {
        UserInstitutionIdentity transientUserInstitutionIdentity = new UserInstitutionIdentity().withUser(application.getUser())
                .withInstitution(application.getInstitution()).withIdentityType(PrismUserInstitutionIdentity.STUDY_APPLICANT).withIdentitier(exportUserId);
        entityService.createOrUpdate(transientUserInstitutionIdentity);
    }

    public boolean isCurrentUser(User user) {
        User currentUser = getCurrentUser();
        return !(user == null || currentUser == null) && Objects.equal(user.getId(), getCurrentUser().getId());
    }

    public <T extends Resource> List<User> getBouncedOrUnverifiedUsers(UserListFilterDTO userListFilterDTO) {
        HashMultimap<PrismScope, T> userAdministratorResources = resourceService.getUserAdministratorResources(getCurrentUser());
        return userAdministratorResources.isEmpty() ? Lists.<User> newArrayList() : userDAO.getBouncedOrUnverifiedUsers(userAdministratorResources,
                userListFilterDTO);
    }

    public <T extends Resource> void correctBouncedOrUnverifiedUser(Integer userId, UserCorrectionDTO userCorrectionDTO) throws Exception {
        HashMultimap<PrismScope, T> userAdministratorResources = resourceService.getUserAdministratorResources(getCurrentUser());
        User user = userDAO.getBouncedOrUnverifiedUser(userAdministratorResources, userId);

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
        for (UserConnection connection : user.getRequestedUserConnections()) {
            connections.put(connection.getUserRequested().getFullName(), connection);
        }

        for (UserConnection connection : user.getConnectedUserConnections()) {
            connections.put(connection.getUserConnected().getFullName(), connection);
        }

        return Lists.newLinkedList(connections.values());
    }

    private void mergeUsers(User oldUser, User newUser) {
        resourceService.reassignResources(oldUser, newUser);
        applicationSectionService.reassignApplicationSections(oldUser, newUser);
        commentService.reassignComments(oldUser, newUser);
        documentService.reassignDocuments(oldUser, newUser);
        reassignUserRoles(oldUser, newUser);
        userDAO.reassignUsers(oldUser, newUser);
        reassignUserInsitutionIdentities(oldUser, newUser);
        reassignUserConnections(oldUser, newUser);
        notificationService.reassignUserNotifications(oldUser, newUser);

        oldUser.setActivationCode(null);
        UserAccount oldUserAccount = oldUser.getUserAccount();
        if (oldUserAccount != null) {
            oldUserAccount.setEnabled(false);
        }
    }

    private void reassignUserRoles(User oldUser, User newUser) {
        for (UserRole userRole : oldUser.getUserRoles()) {
            roleService.getOrCreateUserRole(new UserRole().withResource(userRole.getResource()).withUser(newUser).withRole(userRole.getRole())
                    .withAssignedTimestamp(new DateTime()));
            entityService.delete(userRole);
        }
    }

    private void reassignUserInsitutionIdentities(User oldUser, User newUser) {
        for (UserInstitutionIdentity userInstitutionIdentity : oldUser.getInstitutionIdentities()) {
            userInstitutionIdentity.setUser(newUser);
            UserInstitutionIdentity duplicateUserInstitutionIdentity = entityService.getDuplicateEntity(userInstitutionIdentity);
            if (duplicateUserInstitutionIdentity != null) {
                userInstitutionIdentity.setUser(oldUser);
                entityService.delete(userInstitutionIdentity);
            }
        }
    }

    private void reassignUserConnections(User oldUser, User newUser) {
        Set<UserConnection> requestedUserConnections = newUser.getRequestedUserConnections();
        for (UserConnection connection : oldUser.getRequestedUserConnections()) {
            requestedUserConnections.add(connection);
        }
        oldUser.getRequestedUserConnections().clear();

        Set<UserConnection> connectedUserConnections = newUser.getConnectedUserConnections();
        for (UserConnection connection : oldUser.getConnectedUserConnections()) {
            connectedUserConnections.add(connection);
        }
        oldUser.getConnectedUserConnections().clear();
    }

}
