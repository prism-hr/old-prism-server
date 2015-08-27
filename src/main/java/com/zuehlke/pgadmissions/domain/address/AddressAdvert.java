package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

@Entity
@Table(name = "advert_address")
public class AddressAdvert extends GeocodableLocation implements AddressDefinition<ImportedAdvertDomicile> {

    @ManyToOne
    @JoinColumn(name = "imported_advert_domicile_id", nullable = false)
    private ImportedAdvertDomicile domicile;

    @Column(name = "google_id")
    private String googleId;

    @Embedded
    private AddressCoordinates addressCoordinates;

    @Override
    public ImportedAdvertDomicile getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @Override
    public AddressCoordinates getCoordinates() {
        return addressCoordinates;
    }

    @Override
    public void setCoordinates(AddressCoordinates location) {
        this.addressCoordinates = location;
    }

    public AddressAdvert withDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public AddressAdvert withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressAdvert withLocation(AddressCoordinates location) {
        this.addressCoordinates = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return super.getLocationString() + ", " + domicile.getName();
    }

}
