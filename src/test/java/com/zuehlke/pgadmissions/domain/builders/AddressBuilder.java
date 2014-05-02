package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Domicile;

public class AddressBuilder {

    private Integer id;

    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private Domicile domicile;

    public AddressBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public AddressBuilder address1(String address1) {
        this.address1 = address1;
        return this;
    }

    public AddressBuilder address2(String address2) {
        this.address2 = address2;
        return this;
    }

    public AddressBuilder address3(String address3) {
        this.address3 = address3;
        return this;
    }

    public AddressBuilder address4(String address4) {
        this.address4 = address4;
        return this;
    }

    public AddressBuilder address5(String address5) {
        this.address5 = address5;
        return this;
    }

    public AddressBuilder domicile(Domicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public Address build() {
        Address address = new Address();
        address.setId(id);
        address.setAddressLine1(address1);
        address.setAddressLine2(address2);
        address.setAddressTown(address3);
        address.setAddressRegion(address4);
        address.setAddressCode(address5);
        address.setDomicile(domicile);
        return address;
    }

}
