package com.zuehlke.pgadmissions.dto;

import javax.validation.Valid;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.FormSectionObject;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class AddressSectionDTO implements FormSectionObject {

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String currentAddress1;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String currentAddress2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String currentAddress3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String currentAddress4;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 12)
    private String currentAddress5;

    @Valid
    private Domicile currentAddressDomicile;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String contactAddress1;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String contactAddress2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String contactAddress3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String contactAddress4;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 12)
    private String contactAddress5;

    @Valid
    private Domicile contactAddressDomicile;

    private boolean sameAddress;

    private ApplicationForm application;

    private boolean acceptedTerms;

    public String getCurrentAddress1() {
        return currentAddress1;
    }

    public void setCurrentAddress1(String currentAddress1) {
        this.currentAddress1 = currentAddress1;
    }

    public String getCurrentAddress2() {
        return currentAddress2;
    }

    public void setCurrentAddress2(String currentAddress2) {
        this.currentAddress2 = currentAddress2;
    }

    public String getCurrentAddress3() {
        return currentAddress3;
    }

    public void setCurrentAddress3(String currentAddress3) {
        this.currentAddress3 = currentAddress3;
    }

    public String getCurrentAddress4() {
        return currentAddress4;
    }

    public void setCurrentAddress4(String currentAddress4) {
        this.currentAddress4 = currentAddress4;
    }

    public String getContactAddress1() {
        return contactAddress1;
    }

    public void setContactAddress1(String contactAddress1) {
        this.contactAddress1 = contactAddress1;
    }

    public String getContactAddress2() {
        return contactAddress2;
    }

    public void setContactAddress2(String contactAddress2) {
        this.contactAddress2 = contactAddress2;
    }

    public String getContactAddress3() {
        return contactAddress3;
    }

    public void setContactAddress3(String contactAddress3) {
        this.contactAddress3 = contactAddress3;
    }

    public String getContactAddress4() {
        return contactAddress4;
    }

    public void setContactAddress4(String contactAddress4) {
        this.contactAddress4 = contactAddress4;
    }

    public String getCurrentAddress5() {
        return currentAddress5;
    }

    public void setCurrentAddress5(String currentAddress5) {
        this.currentAddress5 = currentAddress5;
    }

    public String getContactAddress5() {
        return contactAddress5;
    }

    public void setContactAddress5(String contactAddress5) {
        this.contactAddress5 = contactAddress5;
    }

    public Domicile getCurrentAddressDomicile() {
        return currentAddressDomicile;
    }

    public void setCurrentAddressDomicile(Domicile currentAddressDomicile) {
        this.currentAddressDomicile = currentAddressDomicile;
    }

    public Domicile getContactAddressDomicile() {
        return contactAddressDomicile;
    }

    public void setContactAddressDomicile(Domicile contactAddressDomicile) {
        this.contactAddressDomicile = contactAddressDomicile;
    }

    public boolean isSameAddress() {
        return sameAddress;
    }

    public void setSameAddress(boolean sameAddress) {
        this.sameAddress = sameAddress;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

}
