package com.zuehlke.pgadmissions.rest.representation.user;

import com.google.common.base.Objects;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class UserRepresentation implements Comparable<UserRepresentation> {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRepresentation withId(final Integer id) {
        this.id = id;
        return this;
    }

    public UserRepresentation withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserRepresentation withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserRepresentation withEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final UserRepresentation other = (UserRepresentation) object;
        return Objects.equal(email, other.getEmail());
    }

    @Override
    public int compareTo(UserRepresentation other) {
        int compare = compare(firstName, other.getFirstName());
        return compare == 0 ? compare(lastName, other.getLastName()) : compare;
    }

}
