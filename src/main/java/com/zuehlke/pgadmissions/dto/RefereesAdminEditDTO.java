package com.zuehlke.pgadmissions.dto;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class RefereesAdminEditDTO {

    private String editedRefereeId;

    // reference data
    
    private String comment;

    private Document referenceDocument;

    private Boolean suitableForUCL;

    private Boolean suitableForProgramme;

    private Integer applicantRating;
    
    private String alert;

    // referee data

    private Boolean containsRefereeData;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String firstname;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    private String lastname;

    private Address addressLocation;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String jobEmployer;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String jobTitle;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String email;

    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    private String phoneNumber;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String messenger;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSuitableForUCLSet() {
        return suitableForUCL != null;
    }

    public boolean isSuitableForProgrammeSet() {
        return suitableForProgramme != null;
    }

    public Boolean getSuitableForUCL() {
        return suitableForUCL;
    }

    public void setSuitableForUCL(Boolean suitableForUCL) {
        this.suitableForUCL = suitableForUCL;
    }

    public Boolean getSuitableForProgramme() {
        return suitableForProgramme;
    }

    public void setSuitableForProgramme(Boolean suitableForProgramme) {
        this.suitableForProgramme = suitableForProgramme;
    }

    public Integer getApplicantRating() {
        return applicantRating;
    }

    public void setApplicantRating(Integer applicantRating) {
        this.applicantRating = applicantRating;
    }

    public String getEditedRefereeId() {
        return editedRefereeId;
    }

    public void setEditedRefereeId(String editedRefereeId) {
        this.editedRefereeId = editedRefereeId;
    }

    public Document getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(Document referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public Boolean getContainsRefereeData() {
        return containsRefereeData;
    }

    public void setContainsRefereeData(Boolean containsRefereeData) {
        this.containsRefereeData = containsRefereeData;
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

    public Address getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Address addressLocation) {
        this.addressLocation = addressLocation;
    }

    public String getJobEmployer() {
        return jobEmployer;
    }

    public void setJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public boolean hasUserStartedTyping() {
        boolean startedTypingReference = StringUtils.isNotBlank(comment) || !allNull(referenceDocument, suitableForProgramme, suitableForUCL);
        boolean startedTypingRefereeData = addressLocation != null
                && (!allBlank(firstname, lastname, addressLocation.getAddressLine1(), addressLocation.getAddressLine2(), addressLocation.getAddressTown(),
                        addressLocation.getAddressRegion(), addressLocation.getAddressCode(), jobEmployer, jobTitle, email, phoneNumber, messenger) || addressLocation
                        .getDomicile() != null);
        return startedTypingReference || startedTypingRefereeData;
    }

    private boolean allBlank(String... strings) {
        for (String string : strings) {
            if (StringUtils.isNotBlank(string)) {
                return false;
            }
        }
        return true;
    }

    private boolean allNull(Object... objects) {
        for (Object obj : objects) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}