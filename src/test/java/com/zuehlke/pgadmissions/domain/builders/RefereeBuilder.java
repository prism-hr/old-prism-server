package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.User;

public class RefereeBuilder {

    private Integer id;

    private ApplicationForm application;
    private String firstname;
    private String lastname;

    private String jobEmployer;
    private String jobTitle;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;

    private Domicile addressDomicile;
    private ReferenceComment reference;
    private String email;
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

    public RefereeBuilder application(ApplicationForm application) {
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

    public RefereeBuilder firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public RefereeBuilder lastname(String lastname) {
        this.lastname = lastname;

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

    public RefereeBuilder address1(String address1) {
        this.address1 = address1;
        return this;
    }

    public RefereeBuilder address2(String address2) {
        this.address2 = address2;
        return this;
    }

    public RefereeBuilder address3(String address3) {
        this.address3 = address3;
        return this;
    }

    public RefereeBuilder address4(String address4) {
        this.address4 = address4;
        return this;
    }

    public RefereeBuilder address5(String address5) {
        this.address5 = address5;
        return this;
    }

    public RefereeBuilder addressDomicile(Domicile addressDomicile) {
        this.addressDomicile = addressDomicile;
        return this;
    }

    public RefereeBuilder email(String email) {
        this.email = email;
        return this;
    }

    public RefereeBuilder messenger(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public Referee build() {
        Referee referee = new Referee();
        Address address = new AddressBuilder().address1(address1).address2(address2).address3(address3).address4(address4).address5(address5)
                        .domicile(addressDomicile).build();
        referee.setAddressLocation(address);
        referee.setApplication(application);
        referee.setEmail(email);
        referee.setFirstname(firstname);
        referee.setId(id);
        referee.setJobEmployer(jobEmployer);
        referee.setJobTitle(jobTitle);
        referee.setLastname(lastname);
        referee.setMessenger(messenger);

        referee.setReference(reference);
        referee.setUser(user);
        referee.setPhoneNumber(phoneNumber);
        referee.setDeclined(declined);
        referee.setLastNotified(lastNotified);
        referee.setSendToUCL(sendToUCL);
        return referee;
    }
}
