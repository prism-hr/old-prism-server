package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
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
    private SystemService systemService;

    public User getById(Integer id) {
        return entityService.getById(User.class, id);
    }

    @Transactional
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return entityService.getById(User.class, user.getId());
        }
        return null;
    }

    public UserExtendedRepresentation getUserRepresentation(User user) {
        return new UserExtendedRepresentation().withFirstName(user.getFirstName()).withFirstName2(user.getFirstName2()).withFirstName3(user.getFirstName3())
                .withLastName(user.getLastName()).withEmail(user.getEmail());
    }

    public User getOrCreateUser(String firstName, String lastName, String email) throws DeduplicationException {
        User user;
        User transientUser = new User().withFirstName(firstName).withLastName(lastName).withFullName(firstName + " " + lastName).withEmail(email);
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

    public User registerUser(UserRegistrationDTO registrationDTO, String referrer) throws DeduplicationException {
        User user = getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail());
        if ((registrationDTO.getActivationCode() != null && !user.getActivationCode().equals(registrationDTO.getActivationCode()))
                || user.getUserAccount() != null) {
            throw new ResourceNotFoundException();
        }

        user.setUserAccount(new UserAccount().withPassword(encryptionUtils.getMD5Hash(registrationDTO.getPassword())).withSendRecommendationNotification(false)
                .withEnabled(false));

        ActionOutcomeDTO outcome = actionService.getRegistrationOutcome(user, registrationDTO, referrer);
        notificationService.sendNotification(user, outcome.getTransitionResource(), PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                ImmutableMap.of("action", outcome.getTransitionAction().getId().name()));
        return user;
    }

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource,
            List<AbstractResourceRepresentation.RoleRepresentation> roles) throws DeduplicationException {
        User user = getOrCreateUser(firstName, lastName, email);
        roleService.updateRoles(resource, user, roles);
        return user;
    }

    public User getUserByEmail(String email) {
        return entityService.getByProperty(User.class, "email", email);
    }

    public User getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    public void resetPassword(String email) {
        // TODO send activation link if account disabled
        User storedUser = getUserByEmail(email);
        if (storedUser != null) {
            String newPassword = encryptionUtils.generateUserPassword();
            notificationService.sendNotification(storedUser, systemService.getSystem(), PrismNotificationTemplate.SYSTEM_PASSWORD_NOTIFICATION,
                    ImmutableMap.of("newPassword", newPassword));
            storedUser.getUserAccount().setTemporaryPassword(encryptionUtils.getMD5Hash(newPassword));
            storedUser.getUserAccount().setTemporaryPasswordExpiryTimestamp(new DateTime().plusHours(1));
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

    public boolean activateUser(Integer userId) {
        User user = getById(userId);
        boolean wasEnabled = user.getUserAccount().getEnabled();
        user.getUserAccount().setEnabled(true);
        return !wasEnabled;
    }

    public List<User> getEnabledResourceUsers(Resource resource) {
        return userDAO.getEnabledResourceUsers(resource);
    }

}
