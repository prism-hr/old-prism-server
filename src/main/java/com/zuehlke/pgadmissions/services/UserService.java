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
import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.UserUnusedEmail;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.rest.dto.UserAccountDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service("userService")
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationsFilteringDAO filteringDAO;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SystemService systemService;

    public User getById(Integer id) {
        return entityService.getById(User.class, id);
    }

    public void save(User user) {
        entityService.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public boolean checkUserEnabled(User user) {
        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            return userAccount.isEnabled();
        }
        return false;
    }

    public User getUserByEmail(String email) {
        return entityService.getByProperty(User.class, "email", email);
    }

    public User getOrCreateUser(final String firstName, final String lastName, final String email) {
        User user;
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withEmail(email);
        User duplicateUser = entityService.getDuplicateEntity(transientUser);
        if (duplicateUser == null) {
            user = transientUser;
            user.setActivationCode(encryptionUtils.generateUUID());
            entityService.save(user);
        } else {
            user = duplicateUser;
        }
        return user;
    }

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource,
            List<AbstractResourceRepresentation.RoleRepresentation> roles) {
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

    public void mergeUsers(UserAccountDTO mergeFrom, UserAccountDTO mergeInto) {
        User mergeFromUser = userDAO.getAuthenticatedUser(mergeFrom.getEmail(), mergeFrom.getPassword());
        User mergeIntoUser = userDAO.getAuthenticatedUser(mergeInto.getEmail(), mergeInto.getPassword());
        
        if (mergeFromUser != null && mergeIntoUser != null) {
            userDAO.mergeUsers(mergeFromUser, mergeIntoUser);
            
            // TODO: When inviting new user to join, see if email address supplied is unused and map to active user
            
            UserUnusedEmail transientUnusedEmail = new UserUnusedEmail().withUser(mergeIntoUser).withEmail(mergeFrom.getEmail());
            UserUnusedEmail persistentUnusedEmail = entityService.getOrCreate(transientUnusedEmail);
            mergeIntoUser.getUnusedEmails().add(persistentUnusedEmail);
        }
    }

    public void setFiltering(final User user, final Filter filter) {
        Filter mergedFilter = filteringDAO.merge(filter);
        user.getUserAccount().getFilters().put(filter.getScope(), mergedFilter);
    }

    public Long getNumberOfActiveApplicationsForApplicant(final User applicant) {
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
        Set<User> assessors = Sets.newHashSet();
        TreeMap<String, User> interestedAssessors = Maps.newTreeMap();

        List<Comment> assessments = commentService.getApplicationAssessmentComments(application);
        for (Comment comment : assessments) {
            User assessor = comment.getUser();
            if (!assessors.contains(assessor) && (BooleanUtils.isTrue(comment.isDesireToInterview()) || BooleanUtils.isTrue(comment.isDesireToRecruit()))) {
                interestedAssessors.put(assessor.getLastName() + assessor.getFirstName(), assessor);
            }
            assessors.add(assessor);
        }

        List<User> suggestedSupervisors = userDAO.getSuggestedSupervisors(application);
        for (User suggestedSupervisor : suggestedSupervisors) {
            if (!assessors.contains(suggestedSupervisor)) {
                interestedAssessors.put(suggestedSupervisor.getLastName() + suggestedSupervisor.getFirstName(), suggestedSupervisor);
            }
        }

        return Lists.newArrayList(interestedAssessors.values());
    }

    public List<User> getUsersPotentiallyInterestedInApplication(Application application, List<User> usersInterestedInApplication) {
        return userDAO.getUsersPotentiallyInterestedInApplication(application, usersInterestedInApplication);
    }
}
