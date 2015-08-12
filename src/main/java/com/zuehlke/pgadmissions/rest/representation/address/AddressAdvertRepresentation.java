package com.zuehlke.pgadmissions.rest.representation.address;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressAdvertRepresentation extends Address implements AddressDefinition<ImportedAdvertDomicileResponse> {

    private ImportedAdvertDomicileResponse domicile;

    private String googleId;

    private AddressCoordinatesRepresentation coordinates;

    @Override
    public ImportedAdvertDomicileResponse getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedAdvertDomicileResponse domicile) {
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

    public AddressAdvertRepresentation withDomicile(ImportedAdvertDomicileResponse domicile) {
        this.domicile = domicile;
        return this;
    }

    public AddressAdvertRepresentation withAddressLine1(String addressLine1) {
        setAddressLine1(addressLine1);
        return this;
    }

    public AddressAdvertRepresentation withAddressLine2(String addressLine2) {
        setAddressLine2(addressLine2);
        return this;
    }

    public AddressAdvertRepresentation withAddressTown(String addressTown) {
        setAddressTown(addressTown);
        return this;
    }

    public AddressAdvertRepresentation withAddressRegion(String addressRegion) {
        setAddressRegion(addressRegion);
        return this;
    }

    public AddressAdvertRepresentation withAddressCode(String addressCode) {
        setAddressCode(addressCode);
        return this;
    }

    public AddressAdvertRepresentation withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressAdvertRepresentation withCoordinates(AddressCoordinatesRepresentation coordinates) {
        this.coordinates = coordinates;
        return this;
    }

}
