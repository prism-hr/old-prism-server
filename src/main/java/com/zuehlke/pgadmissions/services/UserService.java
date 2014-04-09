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

import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service("userService")
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ApplicationsFilteringDAO filteringDAO;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    public void save(User user) {
        userDAO.save(user);
    }

    public User getById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User currentUser = (User) authentication.getDetails();
            return userDAO.getPrimaryById(currentUser.getId());
        }
        return null;
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public User getUserByEmailIncludingDisabledAccounts(String email) {
        return userDAO.getUserByEmailIncludingDisabledAccounts(email);
    }

    /**
     * Gets or creates a user.
     * 
     * @param firstname
     *            user first name
     * @param lastname
     *            user last name
     * @param email
     *            user email
     * @param createIfNotExist
     *            if <code>true</code> creates non-existing user, otherwise throws {@link IllegalArgumentException} when user does not exist
     * @return found or created user
     */
    public User getUser(final String firstname, final String lastname, final String email, boolean createIfNotExist) {
        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

        if (!createIfNotExist) {
            throw new IllegalArgumentException();
        }

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(email);
        user.setEmail(email);
        user.setEnabled(false);
        user.setActivationCode(encryptionUtils.generateUUID());

        // FIXME specify inviting user

        userDAO.save(user);
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
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            currentUser.setPassword(encryptionUtils.getMD5Hash(user.getNewPassword()));
        }
        currentUser.setUsername(user.getEmail());
        save(currentUser);
    }

    public void resetPassword(String email) {
        User storedUser = userDAO.getUserByEmailIncludingDisabledAccounts(email);
        if (storedUser == null) { // user-mail not found -> ignore
            log.info("reset password request failed, e-mail not found: " + email);
            return;
        }
        try {
            String newPassword = encryptionUtils.generateUserPassword();

            mailService.sendResetPasswordMessage(storedUser, newPassword);

            String hashPassword = encryptionUtils.getMD5Hash(newPassword);
            storedUser.setPassword(hashPassword);
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

        User primary = currentAccount.getPrimaryAccount();
        if (primary == null) {
            primary = currentAccount;
        }

        User secondPrimary = secondAccount.getPrimaryAccount();
        if (secondPrimary != null) {
            for (User u : secondPrimary.getLinkedAccounts()) {
                u.setPrimaryAccount(primary);
            }
            secondPrimary.setPrimaryAccount(primary);
        } else {
            secondAccount.setPrimaryAccount(primary);
            for (User u : secondAccount.getLinkedAccounts()) {
                u.setPrimaryAccount(primary);
            }
        }
    }

    public void deleteLinkedAccount(String accountToDeleteEmail) {
        User currentAccount = getCurrentUser();
        User accountToDelete = getUserByEmail(accountToDeleteEmail);

        User primary = accountToDelete.getPrimaryAccount();
        if (primary == null) {
            for (User u : accountToDelete.getLinkedAccounts()) {
                u.setPrimaryAccount(currentAccount);
            }
            currentAccount.setPrimaryAccount(null);
        } else {
            accountToDelete.setPrimaryAccount(null);
        }
    }

    public void setFiltering(final User user, final ApplicationsFiltering filtering) {
        ApplicationsFiltering mergedFilter = filteringDAO.merge(filtering);
        user.setFiltering(mergedFilter);
        userDAO.save(user);
    }

    public Long getNumberOfActiveApplicationsForApplicant(final User applicant) {
        return userDAO.getNumberOfActiveApplicationsForApplicant(applicant);
    }

}
