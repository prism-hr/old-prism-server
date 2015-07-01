package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.address.AddressApplication;

@Entity
@Table(name = "application_address")
public class ApplicationAddress extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "address")
    private Application application;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_address_id", nullable = false)
    private AddressApplication currentAddress;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_address_id", nullable = false)
    private AddressApplication contactAddress;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public AddressApplication getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(AddressApplication currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressApplication getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressApplication contactAddress) {
        this.contactAddress = contactAddress;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public boolean currentAddressIsContactAddress() {
        return currentAddress == contactAddress;
    }

    public ApplicationAddress withCurrentAddress(AddressApplication addressApplication) {
        this.currentAddress = addressApplication;
        return this;
    }

    public ApplicationAddress withContactAddress(AddressApplication addressApplication) {
        this.contactAddress = addressApplication;
        return this;
    }

    public String getCurrentAddressDisplay() {
        return currentAddress == null ? null : currentAddress.getLocationString();
    }

    public String getConcactAddressDisplay() {
        return contactAddress == null ? null : contactAddress.getLocationString();
    }

}
