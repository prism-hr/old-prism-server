package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class RegisteredUserBuilder {
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
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private String activationCode;
    private String upi;
    private Date applicationListLastAccessTimestamp;

    private List<Referee> referees = new ArrayList<Referee>();
    private List<Comment> comments = new ArrayList<Comment>();

    private List<Role> roles = new ArrayList<Role>();
    private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();
    private List<Program> programsOfWhichApprover = new ArrayList<Program>();
    private List<Program> programsOfWhichViewer = new ArrayList<Program>();

    private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();
    private ApplicationsFiltering filtering;

    private String originalApplicationQueryString;

    private RegisteredUser primaryAccount;
    private List<RegisteredUser> linkedAccounts = new ArrayList<RegisteredUser>();

    public RegisteredUserBuilder linkedAccounts(final RegisteredUser... user) {
        linkedAccounts.addAll(Arrays.asList(user));
        return this;
    }

    public RegisteredUserBuilder applicationListLastAccessTimestamp(Date applicationListLastAccessTimestamp) {
        this.applicationListLastAccessTimestamp = applicationListLastAccessTimestamp;
        return this;
    }

    public RegisteredUserBuilder upi(final String upi) {
        this.upi = upi;
        return this;
    }

    public RegisteredUserBuilder pendingRoleNotifications(PendingRoleNotification... pendingRoleNotifications) {
        this.pendingRoleNotifications.addAll(Arrays.asList(pendingRoleNotifications));
        return this;
    }

    public RegisteredUserBuilder programsOfWhichAdministrator(Program... programs) {
        this.programsOfWhichAdministrator.addAll(Arrays.asList(programs));
        return this;
    }

    public RegisteredUserBuilder programsOfWhichApprover(Program... programs) {
        this.programsOfWhichApprover.addAll(Arrays.asList(programs));
        return this;
    }

    public RegisteredUserBuilder programsOfWhichViewer(Program... programs) {
        this.programsOfWhichViewer.addAll(Arrays.asList(programs));
        return this;
    }

    public RegisteredUserBuilder referees(Referee... referees) {
        this.referees.addAll(Arrays.asList(referees));
        return this;
    }

    public RegisteredUserBuilder comments(Comment... comments) {
        this.comments.addAll(Arrays.asList(comments));
        return this;
    }

    public RegisteredUserBuilder role(Role role) {
        this.roles.add(role);
        return this;
    }

    public RegisteredUserBuilder roles(Role... roles) {
        for (Role role : roles) {
            this.roles.add(role);
        }
        return this;
    }

    public RegisteredUserBuilder filtering(ApplicationsFiltering filtering) {
        this.filtering = filtering;
        return this;
    }

    public RegisteredUserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public RegisteredUserBuilder directURL(String directURL) {
        this.directURL = directURL;
        return this;
    }

    public RegisteredUserBuilder originalApplicationQueryString(String originalApplicationQueryString) {
        this.originalApplicationQueryString = originalApplicationQueryString;
        return this;
    }

    public RegisteredUserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public RegisteredUserBuilder activationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public RegisteredUserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public RegisteredUserBuilder firstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }

    public RegisteredUserBuilder firstName3(String firstName3) {
        this.firstName3 = firstName3;
        return this;
    }

    public RegisteredUserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public RegisteredUserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public RegisteredUserBuilder newPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public RegisteredUserBuilder confirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public RegisteredUserBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public RegisteredUserBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public RegisteredUserBuilder accountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
        return this;
    }

    public RegisteredUserBuilder accountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
        return this;
    }

    public RegisteredUserBuilder credentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
        return this;
    }

    public RegisteredUserBuilder primaryAccount(RegisteredUser primaryAccount) {
        this.primaryAccount = primaryAccount;
        return this;
    }

    public RegisteredUser build() {
        RegisteredUser user = new RegisteredUser();
        user.setId(id);
        user.setFirstName(firstName);
        user.setFirstName2(firstName2);
        user.setFirstName3(firstName3);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setNewPassword(newPassword);
        user.setEnabled(enabled);
        user.setAccountNonExpired(accountNonExpired);
        user.setAccountNonLocked(accountNonLocked);
        user.setCredentialsNonExpired(credentialsNonExpired);
        user.setActivationCode(activationCode);
        user.getRoles().addAll(roles);

        user.getProgramsOfWhichAdministrator().addAll(programsOfWhichAdministrator);
        user.getProgramsOfWhichApprover().addAll(programsOfWhichApprover);
        user.getProgramsOfWhichViewer().addAll(programsOfWhichViewer);
        user.setConfirmPassword(confirmPassword);
        user.getReferees().addAll(referees);
        user.getComments().addAll(comments);
        user.getPendingRoleNotifications().addAll(pendingRoleNotifications);
        user.setDirectToUrl(directURL);
        user.setOriginalApplicationQueryString(originalApplicationQueryString);
        user.setFiltering(filtering);

        user.setUpi(upi);
        user.setApplicationListLastAccessTimestamp(applicationListLastAccessTimestamp);
        user.getLinkedAccounts().addAll(linkedAccounts);
        user.setPrimaryAccount(primaryAccount);

        return user;
    }
}
