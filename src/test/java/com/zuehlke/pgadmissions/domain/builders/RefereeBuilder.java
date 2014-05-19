package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.User;

public class RefereeBuilder {

    private Integer id;

    private Application application;

    private String jobEmployer;
    private String jobTitle;
    private Address address;

    private ReferenceComment reference;
    private String messenger;
    private User user;

    private String phoneNumber;
    private boolean declined;

    private Date lastNotified;

    private boolean sendToUCL;

    public RefereeBuilder sendToUCL(Boolean sendToUCL) {
        this.sendToUCL = sendToUCL;
        return this;
    }

    public RefereeBuilder lastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
        return this;
    }

    public RefereeBuilder declined(boolean declined) {
        this.declined = declined;
        return this;
    }

    public RefereeBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RefereeBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public RefereeBuilder application(Application application) {
        this.application = application;
        return this;
    }

    public RefereeBuilder user(User user) {
        this.user = user;
        return this;
    }

    public RefereeBuilder reference(ReferenceComment reference) {
        this.reference = reference;
        return this;
    }

    public RefereeBuilder jobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
        return this;
    }

    public RefereeBuilder jobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public RefereeBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public RefereeBuilder messenger(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public Referee build() {
        Referee referee = new Referee();
        referee.setAddress(address);
        referee.setApplication(application);
        referee.setId(id);
        referee.setJobEmployer(jobEmployer);
        referee.setJobTitle(jobTitle);
        referee.setMessenger(messenger);

        referee.setComment(reference);
        referee.setUser(user);
        referee.setPhoneNumber(phoneNumber);
        referee.setDeclined(declined);
        referee.setLastNotified(lastNotified);
        referee.setSendToUCL(sendToUCL);
        return referee;
    }
}
