package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL_PASSPORT")
public class PassportInformation implements Serializable {

    private static final long serialVersionUID = -1147171760649226325L;
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @OneToOne(mappedBy = "passportInformation")
    private PersonalDetails personalDetails;

    @ESAPIConstraint(rule = "LettersAndNumbersOnly", maxLength = 35, message = "{text.field.nonlettersandnumbers}")
    @Column(name = "passport_number")
    private String passportNumber;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "passport_name")
    private String nameOnPassport;
    
    @Column(name = "passport_issue_date")
    @Temporal(TemporalType.DATE)
    private Date passportIssueDate;
    
    @Column(name = "passport_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date passportExpiryDate;
        
    public PassportInformation() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getNameOnPassport() {
        return nameOnPassport;
    }

    public void setNameOnPassport(String nameOnPassport) {
        this.nameOnPassport = nameOnPassport;
    }

    public Date getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(Date passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public Date getPassportExpiryDate() {
        return passportExpiryDate;
    }

    public void setPassportExpiryDate(Date passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }
    
    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }
}
