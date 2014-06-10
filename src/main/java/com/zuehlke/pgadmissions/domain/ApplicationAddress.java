package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "APPLICATION_ADDRESS")
public class ApplicationAddress {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_address_id", nullable = false)
    private Address currentAddress;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_address_id", nullable = false)
    private Address contactAddress;

    @OneToOne(mappedBy = "applicationAddress")
    private Application application;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public Address getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(Address contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean currentAddressIsContactAddress() {
        return currentAddress == contactAddress;
    }

    public ApplicationAddress withCurrentAddress(Address address) {
        this.currentAddress = address;
        return this;
    }

    public ApplicationAddress withContactAddress(Address address) {
        this.contactAddress = address;
        return this;
    }

}
