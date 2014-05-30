package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "ADDRESS")
public class Address implements Serializable {

    private static final long serialVersionUID = 2746228908173552617L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domicile_id", nullable = false)
    private Domicile domicile;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    @Column(name = "address_line_2")
    private String addressLine2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    @Column(name = "address_town", nullable = false)
    private String addressTown;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    @Column(name = "address_region")
    private String addressRegion;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 12)
    @Column(name = "address_code")
    private String addressCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getLocationString() {
        return Joiner.on('\n').skipNulls().join(addressLine1, addressLine2, addressTown, addressRegion, addressCode);
    }

    @Override
    public String toString() {
        String domicileName = domicile == null ? null : domicile.getName();
        return Joiner.on('\n').skipNulls().join(getLocationString(), domicileName);
    }
    
    public Address withDomicile(Domicile domicile) {
        this.domicile = domicile;
        return this;
    }
    
    public Address withLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }
    
    public Address withLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public Address withTown(String town) {
        this.addressTown = town;
        return this;
    }
    
    public Address withRegion(String region) {
        this.addressRegion = region;
        return this;
    }
    
    public Address withCode(String code) {
        this.addressCode = code;
        return this;
    }
    
}
