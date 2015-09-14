package com.zuehlke.pgadmissions.rest.representation.address;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.address.AddressDefinition;

import uk.co.alumeni.prism.api.model.imported.response.ImportedDomicileResponse;

public class AddressRepresentation extends AddressDefinition<ImportedDomicileResponse> {

    private Integer id;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private ImportedDomicileResponse domicile;

    private String googleId;

    private AddressCoordinatesRepresentation coordinates;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public ImportedDomicileResponse getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedDomicileResponse domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public AddressCoordinatesRepresentation getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(AddressCoordinatesRepresentation coordinates) {
        this.coordinates = coordinates;
    }

    public AddressRepresentation withDomicile(ImportedDomicileResponse domicile) {
        this.domicile = domicile;
        return this;
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

    public AddressRepresentation withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressRepresentation withCoordinates(AddressCoordinatesRepresentation coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public String getLocationString() {
        return Joiner.on(", ").skipNulls().join(addressLine1, addressLine2, addressTown, addressRegion, addressCode, domicile.getName());
    }

}