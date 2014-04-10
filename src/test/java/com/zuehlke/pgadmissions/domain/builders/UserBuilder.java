package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;

public class UserBuilder {
    private String firstName;
    private String firstName2;
    private String firstName3;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String newPassword;
    private String directURL;
    private Integer id;
    private boolean enabled = true;
    private String activationCode;
    private String upi;
    private Date applicationListLastAccessTimestamp;
    private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();
    private ApplicationsFiltering filtering;
    private Advert advert;
    private User primaryAccount;
    private List<User> linkedAccounts = new ArrayList<User>();

    public UserBuilder linkedAccounts(final User... user) {
        linkedAccounts.addAll(Arrays.asList(user));
        return this;
    }

    public UserBuilder applicationListLastAccessTimestamp(Date applicationListLastAccessTimestamp) {
        this.applicationListLastAccessTimestamp = applicationListLastAccessTimestamp;
        return this;
    }

    public UserBuilder upi(final String upi) {
        this.upi = upi;
        return this;
    }

    public UserBuilder pendingRoleNotifications(PendingRoleNotification... pendingRoleNotifications) {
        this.pendingRoleNotifications.addAll(Arrays.asList(pendingRoleNotifications));
        return this;
    }

    public UserBuilder filtering(ApplicationsFiltering filtering) {
        this.filtering = filtering;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder directURL(String directURL) {
        this.directURL = directURL;
        return this;
    }

    public UserBuilder advert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder activationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder firstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }

    public UserBuilder firstName3(String firstName3) {
        this.firstName3 = firstName3;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder newPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public UserBuilder confirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public UserBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public UserBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserBuilder primaryAccount(User primaryAccount) {
        this.primaryAccount = primaryAccount;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setFirstName2(firstName2);
        user.setFirstName3(firstName3);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setEnabled(enabled);
        user.setActivationCode(activationCode);
        user.getPendingRoleNotifications().addAll(pendingRoleNotifications);
        user.setDirectToUrl(directURL);
        user.setAdvert(advert);
        user.setUpi(upi);
        user.getLinkedAccounts().addAll(linkedAccounts);
        user.setPrimaryAccount(primaryAccount);

        UserAccount userAccount = new UserAccount();
        userAccount.setPassword(password);
        userAccount.setNewPassword(newPassword);
        userAccount.setConfirmPassword(confirmPassword);
        userAccount.setFiltering(filtering);
        userAccount.setApplicationListLastAccessTimestamp(applicationListLastAccessTimestamp);
        user.setAccount(userAccount);
        return user;
    }

}
