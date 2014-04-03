package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class Passport implements Serializable {

    private static final long serialVersionUID = 633405865707537799L;

    @Id
    @GeneratedValue
    private Integer id;
    
    @ESAPIConstraint(rule = "LettersAndNumbersOnly", maxLength = 35, message = "{text.field.nonlettersandnumbers}")
    @Column(name = "number")
    private String number;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "name")
    private String name;
    
    @Column(name = "issue_date")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
