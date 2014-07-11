package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_PASSPORT")
public class ApplicationPassport {

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "LettersAndNumbersOnly", maxLength = 35, message = "{text.field.nonlettersandnumbers}")
    @Column(name = "number", nullable = false)
    private String number;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "issue_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate expiryDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ApplicationPassport withNumber(String number) {
        this.number = number;
        return this;
    }

    public ApplicationPassport withName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationPassport withIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public ApplicationPassport withExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

}
