package com.zuehlke.pgadmissions.domain.address;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.Coordinates;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import javax.persistence.*;

@Entity
@Table(name = "advert_address")
public class AddressAdvert extends GeocodableLocation implements AddressDefinition<ImportedAdvertDomicile> {

    @ManyToOne
    @JoinColumn(name = "imported_advert_domicile_id", nullable = false)
    private ImportedAdvertDomicile domicile;

    @Column(name = "google_id")
    private String googleId;

    @Embedded
    private Coordinates coordinates;

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
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(Coordinates location) {
        this.coordinates = location;
    }

    public AddressAdvert withDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public AddressAdvert withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressAdvert withLocation(Coordinates location) {
        this.coordinates = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return super.getLocationString() + ", " + domicile.getName();
    }

}
