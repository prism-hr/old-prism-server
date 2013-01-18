package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class RefereesAdminEditDTO {
    
    private String applicationId;
    
    private String editedRefereeId;
    
    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String email;
    
    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    private String telephone;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String skype;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
    private String comment;
    
    private Boolean suitableForUCL;
    
    private Boolean suitableForProgramme;
    
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

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

    public String getEditedRefereeId() {
        return editedRefereeId;
    }

    public void setEditedRefereeId(String editedRefereeId) {
        this.editedRefereeId = editedRefereeId;
    }
}