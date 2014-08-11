package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.rest.dto.UserAccountDTO;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

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
    private EncryptionUtils encryptionUtils;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SystemService systemService;

    public User getById(Integer id) {
        return entityService.getById(User.class, id);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public UserExtendedRepresentation getUserRepresentation(User user) {
        return new UserExtendedRepresentation().withFirstName(user.getFirstName()).withFirstName2(user.getFirstName2()).withFirstName3(user.getFirstName3())
                .withLastName(user.getLastName()).withEmail(user.getEmail());
    }

    public User getUserByEmail(String email) {
        return entityService.getByProperty(User.class, "email", email);
    }

    public User getOrCreateUser(String firstName, String lastName, String email) {
        User user;
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withEmail(email);
        User duplicateUser = entityService.getDuplicateEntity(transientUser);
        if (duplicateUser == null) {
            user = transientUser;
            user.setActivationCode(encryptionUtils.generateUUID());
            entityService.save(user);
            user.setParentUser(user);
        } else {
            user = duplicateUser;
        }
        return user;
    }
    
    public User registerUser(UserRegistrationDTO registrationDTO) throws WorkflowEngineException {
        User user = getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail());
        if ((registrationDTO.getActivationCode() != null && !user.getActivationCode().equals(registrationDTO.getActivationCode()))
                || user.getUserAccount() != null) {
            throw new ResourceNotFoundException();
        }

        user.setUserAccount(new UserAccount().withPassword(encryptionUtils.getMD5Hash(registrationDTO.getPassword())).withEnabled(false));

        Action action = actionService.getById(registrationDTO.getAction().getActionId());
        Resource resource = resourceService.getRegistrationResource(registrationDTO, user, action);
        notificationService.sendNotification(user, resource, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        return user;
    }
    
    public User activateUser(String activationCode) {
        User user = getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }
    
    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource,
            List<AbstractResourceRepresentation.RoleRepresentation> roles) throws WorkflowEngineException {
        User user = getOrCreateUser(firstName, lastName, email);
        roleService.updateRoles(resource, user, roles);
        return user;
    }

    public User getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    public void updateCurrentUser(User user) {
        User currentUser = getCurrentUser();
        currentUser.setFirstName(user.getFirstName());
        currentUser.setFirstName2(user.getFirstName2());
        currentUser.setFirstName3(user.getFirstName3());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        if (StringUtils.isNotBlank(user.getUserAccount().getNewPassword())) {
            currentUser.getUserAccount().setPassword(encryptionUtils.getMD5Hash(user.getUserAccount().getNewPassword()));
        }
    }

    public void resetPassword(String email) {
        User storedUser = entityService.getByProperty(User.class, "email", email);
        if (storedUser != null) {
            try {
                String newPassword = encryptionUtils.generateUserPassword();
                NotificationTemplate passwordTemplate = notificationService.getById(PrismNotificationTemplate.SYSTEM_PASSWORD_NOTIFICATION);
                notificationService.sendNotification(storedUser, null, passwordTemplate, ImmutableMap.of("newPassword", newPassword));
                storedUser.getUserAccount().setPassword(encryptionUtils.getMD5Hash(newPassword));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public void linkUsers(UserAccountDTO linkFromUserDTO, UserAccountDTO linkIntoUserDTO) {
        User linkFromUser = userDAO.getAuthenticatedUser(linkFromUserDTO.getEmail(), linkFromUserDTO.getPassword());
        User linkIntoUser = userDAO.getAuthenticatedUser(linkIntoUserDTO.getEmail(), linkIntoUserDTO.getPassword());
        
        if (linkFromUser != null && linkIntoUser != null) {
            userDAO.refreshParentUser(linkIntoUser);
            linkFromUser.setParentUser(linkIntoUser);
        }
    }

    public void setFilter(Filter transientFilter) {
        Filter persistentFilter = entityService.getDuplicateEntity(transientFilter);
        if (persistentFilter == null) {
            UserAccount userAccount = transientFilter.getUserAccount();
            Scope scope = transientFilter.getScope();
            
            transientFilter.setUserAccount(null);
            transientFilter.setScope(null);
            
            userAccount.getFilters().put(scope, transientFilter);
        }
    }

    public Integer getNumberOfActiveApplicationsForApplicant(User applicant) {
        return userDAO.getNumberOfActiveApplicationsForApplicant(applicant);
    }

    public List<MailDescriptor> getUsersDueTaskNotification() {
        return userDAO.getUseDueTaskNotification();
    }

    public List<User> getUsersForResourceAndRole(Resource resource, PrismRole authority) {
        return userDAO.getUsersForResourceAndRole(resource, authority);
    }

    public String getUserInstitutionId(User user, Institution institution, PrismUserIdentity identityType) {
        return userDAO.getUserInstitutionId(user, institution, identityType);
    }

    public List<User> getUsersInterestedInApplication(Application application) {
        Set<User> recruiters = Sets.newHashSet();
        TreeMap<String, User> orderedRecruiters = Maps.newTreeMap();

        List<Comment> assessments = commentService.getApplicationAssessmentComments(application);
        for (Comment comment : assessments) {
            User recruiter = comment.getUser().getParentUser();
            if (!recruiters.contains(recruiter) && (BooleanUtils.isTrue(comment.isDesireToInterview()) || BooleanUtils.isTrue(comment.isDesireToRecruit()))) {
                orderedRecruiters.put(recruiter.getLastName() + recruiter.getFirstName(), recruiter);
            }
            recruiters.add(recruiter);
        }

        List<User> suggestedSupervisors = userDAO.getSuggestedSupervisors(application);
        for (User suggestedSupervisor : suggestedSupervisors) {
            if (!recruiters.contains(suggestedSupervisor)) {
                orderedRecruiters.put(suggestedSupervisor.getLastName() + suggestedSupervisor.getFirstName(), suggestedSupervisor);
            }
        }

        return Lists.newArrayList(orderedRecruiters.values());
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
    
}
