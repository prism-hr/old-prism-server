package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.PassportInformation;

public class PassportInformationBuilder {

    private String passportNumber;

    private String nameOnPassport;

    private Date passportIssueDate;

    private Date passportExpiryDate;

    public PassportInformationBuilder passportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
        return this;
    }

    public PassportInformationBuilder nameOnPassport(String nameOnPassport) {
        this.nameOnPassport = nameOnPassport;
        return this;
    }

    public PassportInformationBuilder passportIssueDate(Date passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
        return this;
    }

    public PassportInformationBuilder passportExpiryDate(Date passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
        return this;
    }

    public PassportInformation build() {
        PassportInformation passportInformation = new PassportInformation();
        passportInformation.setPassportExpiryDate(passportExpiryDate);
        passportInformation.setPassportIssueDate(passportIssueDate);
        passportInformation.setPassportNumber(passportNumber);
        passportInformation.setNameOnPassport(nameOnPassport);
        return passportInformation;
    }
}
