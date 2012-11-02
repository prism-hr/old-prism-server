package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "PERSON")
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.FIELD)
public class Person extends DomainObject<Integer> {

    private static final long serialVersionUID = 1747305941073095458L;
    
    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String email;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String firstname;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    private String lastname;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Id
    @GeneratedValue
    @Access(AccessType.PROPERTY)
    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
