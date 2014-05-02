package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Advert;
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
    private Advert advert;
    private User primaryAccount;
    private List<User> linkedAccounts = new ArrayList<User>();

    private UserAccount userAccount;

    public UserBuilder linkedAccounts(final User... user) {
        linkedAccounts.addAll(Arrays.asList(user));
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
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

    public UserBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public UserBuilder primaryAccount(User primaryAccount) {
        this.primaryAccount = primaryAccount;
        return this;
    }
    public UserBuilder userAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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
        user.setActivationCode(activationCode);
        user.setAdvert(advert);
        user.getLinkedAccounts().addAll(linkedAccounts);
        user.setParentUser(primaryAccount);
        user.setAccount(userAccount);
        return user;
    }

}
