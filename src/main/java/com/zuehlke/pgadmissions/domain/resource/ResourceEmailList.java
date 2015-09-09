package com.zuehlke.pgadmissions.domain.resource;

import javax.persistence.Embeddable;

@Embeddable
public class ResourceEmailList {

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
