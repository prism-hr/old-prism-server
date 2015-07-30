package com.zuehlke.pgadmissions.rest.representation.address;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.address.Address;

public class AddressAdvertRepresentation extends Address implements AddressDefinition<ImportedAdvertDomicileResponse> {

    private ImportedAdvertDomicileResponse domicile;

    private String googleId;

    private CoordinatesRepresentation coordinates;

    private String locationString;

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

    public CoordinatesRepresentation getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesRepresentation coordinates) {
        this.coordinates = coordinates;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

}
