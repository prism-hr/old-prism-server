package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.rest.representation.ResourceRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service("userService")
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

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
    private SystemService systemService;

    public void save(User user) {
        userDAO.save(user);
    }

    public User getById(int id) {
        return entityService.getById(User.class, id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            return currentUser.getParentUser();
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

    public User getUserByEmailIncludingDisabledAccounts(String email) {
        return userDAO.getUserByEmailIncludingDisabledAccounts(email);
    }

    public User getOrCreateUser(final String firstName, final String lastName, final String email) {
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

    public User getOrCreateUserWithRoles(String firstName, String lastName, String email, Resource resource,
            List<ResourceRepresentation.RoleRepresentation> roles) {
        User user = getOrCreateUser(firstName, lastName, email);
        roleService.updateRoles(resource, user, roles);
        return user;
    }

    public User getUserByActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    public List<User> getUsersWithUpi(final String upi) {
        return userDAO.getUsersWithUpi(upi);
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
        save(currentUser);
    }

    public void resetPassword(String email) {
        User storedUser = userDAO.getUserByEmailIncludingDisabledAccounts(email);
        if (storedUser == null) {
            log.info("reset password request failed, e-mail not found: " + email);
            return;
        }
        try {
            String newPassword = encryptionUtils.generateUserPassword();

            NotificationTemplate passwordTemplate = notificationService.getById(PrismNotificationTemplate.SYSTEM_PASSWORD_NOTIFICATION);
            notificationService.sendNotification(storedUser, null, passwordTemplate, ImmutableMap.of("newPassword", newPassword));

            String hashPassword = encryptionUtils.getMD5Hash(newPassword);
            storedUser.getUserAccount().setPassword(hashPassword);
            userDAO.save(storedUser);
        } catch (Exception e) {
            log.warn("error while sending email", e);
        }
    }

    public void linkAccounts(String secondAccountEmail) throws LinkAccountsException {
        User secondAccount = getUserByEmail(secondAccountEmail);
        User currentAccount = getCurrentUser();

        if (HibernateUtils.containsEntity(currentAccount.getLinkedAccounts(), secondAccount)) {
            return;
        }

        if (!currentAccount.isEnabled() || !secondAccount.isEnabled()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isAccountNonExpired() || !secondAccount.isAccountNonExpired()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isAccountNonLocked() || !secondAccount.isAccountNonLocked()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        if (!currentAccount.isCredentialsNonExpired() || !secondAccount.isCredentialsNonExpired()) {
            throw new LinkAccountsException("account.not.enabled");
        }

        User primary = currentAccount.getParentUser();
        if (primary == null) {
            primary = currentAccount;
        }

        User secondPrimary = secondAccount.getParentUser();
        if (secondPrimary != null) {
            for (User u : secondPrimary.getLinkedAccounts()) {
                u.setParentUser(primary);
            }
            secondPrimary.setParentUser(primary);
        } else {
            secondAccount.setParentUser(primary);
            for (User u : secondAccount.getLinkedAccounts()) {
                u.setParentUser(primary);
            }
        }
    }

    public void deleteLinkedAccount(String accountToDeleteEmail) {
        User currentAccount = getCurrentUser();
        User accountToDelete = getUserByEmail(accountToDeleteEmail);

        User primary = accountToDelete.getParentUser();
        if (primary == null) {
            for (User u : accountToDelete.getLinkedAccounts()) {
                u.setParentUser(currentAccount);
            }
            currentAccount.setParentUser(null);
        } else {
            accountToDelete.setParentUser(null);
        }
    }

    public void setFiltering(final User user, final Filter filter) {
        Filter mergedFilter = filteringDAO.merge(filter);
        user.getUserAccount().getFilters().put(filter.getScope(), mergedFilter);
        userDAO.save(user);
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

    public List<MailDescriptor> getUserStateTransitionNotifications(StateTransition stateTransition) {
        return userDAO.getUserStateTransitionNotifications(stateTransition);
    }

}
