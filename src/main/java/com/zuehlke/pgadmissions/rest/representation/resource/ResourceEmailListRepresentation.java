package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceEmailListRepresentation {

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
    
    public ResourceEmailListRepresentation withEmailAddresses(String emailAddresses) {
        this.emailAddresses = emailAddresses;
        return this;
    }

    public ResourceEmailListRepresentation withMailingList(Boolean mailingList) {
        this.mailingList = mailingList;
        return this;
    }
    
}
