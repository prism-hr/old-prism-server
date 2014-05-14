package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Passport;

public class PassportInformationBuilder {

    private String passportNumber;

    private String nameOnPassport;

    private Date passportIssueDate;

    private Date passportExpiryDate;

    public PassportInformationBuilder number(String passportNumber) {
        this.passportNumber = passportNumber;
        return this;
    }

    public PassportInformationBuilder name(String nameOnPassport) {
        this.nameOnPassport = nameOnPassport;
        return this;
    }

    public PassportInformationBuilder issueDate(Date passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
        return this;
    }

    public PassportInformationBuilder expiryDate(Date passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
        return this;
    }

    public Passport build() {
        Passport passportInformation = new Passport();
        passportInformation.setExpiryDate(passportExpiryDate);
        passportInformation.setIssueDate(passportIssueDate);
        passportInformation.setNumber(passportNumber);
        passportInformation.setName(nameOnPassport);
        return passportInformation;
    }
}
