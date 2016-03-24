package uk.co.alumeni.prism.domain.application;

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

import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.profile.ProfileAddress;

@Entity
@Table(name = "application_address")
public class ApplicationAddress extends ApplicationSection implements ProfileAddress<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "address")
    private Application association;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_address_id", nullable = false)
    private Address currentAddress;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_address_id", nullable = false)
    private Address contactAddress;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Application getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(Application association) {
        this.association = association;
    }

    @Override
    public Address getCurrentAddress() {
        return currentAddress;
    }

    @Override
    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Override
    public Address getContactAddress() {
        return contactAddress;
    }

    @Override
    public void setContactAddress(Address contactAddress) {
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

}
