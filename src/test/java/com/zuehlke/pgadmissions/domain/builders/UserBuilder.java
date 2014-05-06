package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;

public class UserBuilder {
    private String firstName;
    private String firstName2;
    private String firstName3;
    private String lastName;
    private String email;
    private Integer id;
    private String activationCode;
    private User parentUser;
    private List<User> linkedAccounts = new ArrayList<User>();

    private UserAccount userAccount;

    public UserBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withFirstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }

    public UserBuilder withFirstName3(String firstName3) {
        this.firstName3 = firstName3;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withActivationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public UserBuilder withLinkedAccounts(final User... user) {
        linkedAccounts.addAll(Arrays.asList(user));
        return this;
    }

    public UserBuilder withParentUser(User parentUser) {
        this.parentUser = parentUser;
        return this;
    }

    public UserBuilder withAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public User buildUser() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setFirstName2(firstName2);
        user.setFirstName3(firstName3);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setActivationCode(activationCode);
        user.getLinkedAccounts().addAll(linkedAccounts);
        user.setParentUser(parentUser);
        user.setAccount(userAccount);
        return user;
    }

}
