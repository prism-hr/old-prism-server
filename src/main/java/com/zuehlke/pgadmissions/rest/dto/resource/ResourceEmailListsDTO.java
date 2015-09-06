package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

public class ResourceEmailListsDTO {

    @Valid
    private ResourceEmailListDTO recruiterEmailList;

    @Valid
    private ResourceEmailListDTO applicantEmailList;

    public ResourceEmailListDTO getRecruiterEmailList() {
        return recruiterEmailList;
    }

    public void setRecruiterEmailList(ResourceEmailListDTO recruiterEmailList) {
        this.recruiterEmailList = recruiterEmailList;
    }

    public ResourceEmailListDTO getApplicantEmailList() {
        return applicantEmailList;
    }

    public void setApplicantEmailList(ResourceEmailListDTO applicantEmailList) {
        this.applicantEmailList = applicantEmailList;
    }

    public static class ResourceEmailListDTO {

        @NotEmpty
        private String emailAddresses;

        private Boolean mailingList;

        public String getEmailAddresses() {
            return emailAddresses;
        }

        public void setEmailAddresses(String emailAddresses) {
            this.emailAddresses = emailAddresses;
        }

        public Boolean getMailingList() {
            return mailingList;
        }

        public void setMailingList(Boolean mailingList) {
            this.mailingList = mailingList;
        }

    }

}
