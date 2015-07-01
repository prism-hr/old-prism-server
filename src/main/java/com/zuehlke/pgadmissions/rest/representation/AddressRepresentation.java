package com.zuehlke.pgadmissions.rest.representation;

public class AddressRepresentation {

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

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

    public AddressRepresentation withAddressLine1(String addressLine1) {
        setAddressLine1(addressLine1);
        return this;
    }

    public AddressRepresentation withAddressLine2(String addressLine2) {
        setAddressLine2(addressLine2);
        return this;
    }

    public AddressRepresentation withAddressTown(String addressTown) {
        setAddressTown(addressTown);
        return this;
    }

    public AddressRepresentation withAddressRegion(String addressRegion) {
        setAddressRegion(addressRegion);
        return this;
    }

    public AddressRepresentation withAddressCode(String addressCode) {
        setAddressCode(addressCode);
        return this;
    }

}
