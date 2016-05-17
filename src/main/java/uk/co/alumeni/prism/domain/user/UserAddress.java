package uk.co.alumeni.prism.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.profile.ProfileAddress;

@Entity
@Table(name = "user_address")
public class UserAddress implements ProfileAddress<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "address")
    private UserAccount association;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_address_id", nullable = false)
    private Address currentAddress;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_address_id", nullable = false)
    private Address contactAddress;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public UserAccount getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(UserAccount association) {
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

}
