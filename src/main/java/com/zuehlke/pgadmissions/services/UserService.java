package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DocumentService documentService;

    @Value("${system.user.email}")
    private String systemUserEmail;

    public User getById(Integer id) {
        return entityService.getById(User.class, id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return entityService.getById(User.class, user.getId());
        }
        return null;
    }

    public UserRepresentation getUserRepresentation(User user) {
        return new UserRepresentation().withFirstName(user.getFirstName()).withFirstName2(user.getFirstName2()).withFirstName3(user.getFirstName3())
                .withLastName(user.getLastName()).withEmail(user.getEmail());
    }

    public User registerUser(UserRegistrationDTO registrationDTO, String referrer) throws Exception {
        Resource resource = resourceService.getById(registrationDTO.getAction().getActionId().getScope().getResourceClass(), registrationDTO.getResourceId());
        User user = getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail(), resource.getLocale());
        if ((registrationDTO.getActivationCode() != null && !user.getActivationCode().equals(registrationDTO.getActivationCode()))
                || user.getUserAccount() != null) {
            throw new ResourceNotFoundException("Incorrect code or user already exists");
        }

        user.setUserAccount(new UserAccount().withPassword(EncryptionUtils.getMD5(registrationDTO.getPassword()))
                .withSendApplicationRecommendationNotification(false).withEnabled(false));

        ActionOutcomeDTO outcome = actionService.getRegistrationOutcome(user, registrationDTO, referrer);
        notificationService.sendRegistrationNotification(user, outcome);
        return user;
    }

    public User getOrCreateUser(String firstName, String lastName, String email, PrismLocale locale) throws DeduplicationException {
        User user;
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withFullName(firstName + " " + lastName).withEmail(email)
                .withEmailValid(true).withLocale(locale);
        User duplicateUser = entityService.getDuplicateEntity(transientUser);
        if (duplicateUser == null) {
            user = transientUser;
            user.setActivationCode(EncryptionUtils.getUUID());
            entityService.save(user);
            user.setParentUser(user);
        } else {
            user = duplicateUser;
        }
        return user;
    }

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, PrismLocale locale, Resource resource, Set<PrismRole> roles)
            throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException,
            IntegrationException {
        User user = getOrCreateUser(firstName, lastName, email, locale);
        roleService.updateUserRole(resource, user, PrismRoleTransitionType.CREATE, roles.toArray(new PrismRole[roles.size()]));
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
            errors.rejectValue("email", "alreadyExists");
            throw new PrismValidationException("Cannot update user", errors);
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        user.setFirstName2(Strings.emptyToNull(userDTO.getFirstName2()));
        user.setFirstName3(Strings.emptyToNull(userDTO.getFirstName3()));
        user.setEmail(userDTO.getEmail());
        user.setLocale(userDTO.getLocale());

        Document portraitDocument = null;
        if (userDTO.getPortraitDocument() != null) {
            portraitDocument = documentService.getById(userDTO.getPortraitDocument(), FileCategory.IMAGE);
        }

        user.setPortraitDocument(portraitDocument);
        user.setLinkedinUri(userDTO.getLinkedinUri());
        user.setTwitterUri(userDTO.getTwitterUri());

        UserAccount account = user.getUserAccount();

        account.setSendApplicationRecommendationNotification(userDTO.getSendApplicationRecommendationNotification());

        String password = userDTO.getPassword();
        if (password != null) {
            account.setPassword(EncryptionUtils.getMD5(password));
            account.setTemporaryPassword(null);
            account.setTemporaryPasswordExpiryTimestamp(null);
        }
    }

    public User getUserByEmail(String email) {
        return entityService.getByProperty(User.class, "email", email);
    }

    public User getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
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

    public void unlinkUser(User user) {
        User currentUser = getCurrentUser();
        if (currentUser.getChildUsers().contains(user)) {
            user.setParentUser(user);
        }
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

    public String getUserInstitutionId(User user, Institution institution, PrismUserIdentity identityType) {
        return userDAO.getUserInstitutionId(user, institution, identityType);
    }

    public List<User> getUsersInterestedInApplication(Application application) {
        Set<User> recruiters = Sets.newHashSet();
        TreeMap<String, User> orderedRecruiters = Maps.newTreeMap();

        List<Comment> assessments = commentService.getApplicationAssessmentComments(application);
        for (Comment comment : assessments) {
            User recruiter = comment.getUser();
            if (!recruiters.contains(recruiter)
                    && ((BooleanUtils.isTrue(comment.getApplicationInterested())) || BooleanUtils.isTrue(comment.getRecruiterAcceptAppointment()))) {
                orderedRecruiters.put(recruiter.getIndexName(), recruiter);
            }
            recruiters.add(recruiter);
        }

        List<User> suggestedSupervisors = userDAO.getSuggestedSupervisors(application);
        for (User suggestedSupervisor : suggestedSupervisors) {
            if (!recruiters.contains(suggestedSupervisor)) {
                orderedRecruiters.put(suggestedSupervisor.getIndexName(), suggestedSupervisor);
            }
        }

        return Lists.newLinkedList(orderedRecruiters.values());
    }

    public List<User> getUsersPotentiallyInterestedInApplication(Application application, List<User> usersToExclude) {
        usersToExclude = Lists.newArrayList(usersToExclude);

        List<User> recruiters = userDAO.getRecruitersAssignedToApplication(application, usersToExclude);
        usersToExclude.addAll(recruiters);

        Program program = application.getProgram();

        List<User> programRecruiters = userDAO.getRecruitersAssignedToProgramApplications(program, usersToExclude);
        recruiters.addAll(programRecruiters);
        usersToExclude.addAll(programRecruiters);

        List<User> projectRecruiters = userDAO.getRecruitersAssignedToProgramProjects(program, usersToExclude);
        recruiters.addAll(projectRecruiters);

        TreeMap<String, User> orderedRecruiters = Maps.newTreeMap();

        for (User recruiter : recruiters) {
            orderedRecruiters.put(recruiter.getLastName() + recruiter.getFirstName(), recruiter);
        }

        return Lists.newArrayList(orderedRecruiters.values());
    }

    public List<UserRepresentation> getSimilarUsers(String searchTerm) {
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
                .withInstitution(application.getInstitution()).withIdentityType(PrismUserIdentity.STUDY_APPLICANT).withIdentitier(exportUserId);
        entityService.createOrUpdate(transientUserInstitutionIdentity);
    }

    public boolean isCurrentUser(User user) {
        return user != null && Objects.equal(user.getId(), getCurrentUser().getId());
    }


}
