package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedDomicile domicile;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_town", nullable = false)
    private String addressTown;

    @Column(name = "address_region")
    private String addressRegion;

    @Column(name = "address_code")
    private String addressCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedDomicile domicile) {
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
        return Joiner.on(", ").skipNulls()
                .join(addressLine1, addressLine2, addressTown, addressRegion, addressCode, domicile == null ? null : domicile.getName());
    }

    public Address withCode(String code) {
        this.addressCode = code;
        return this;
    }

}
