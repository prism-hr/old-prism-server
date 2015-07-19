package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.api.model.resource.AddressDefinition;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;

@Entity
@Table(name = "advert_address")
public class AddressAdvert extends GeocodableLocation implements AddressDefinition<ImportedAdvertDomicile> {

    @ManyToOne
    @JoinColumn(name = "imported_advert_domicile_id", nullable = false)
    private ImportedAdvertDomicile domicile;

    @Column(name = "google_id")
    private String googleId;

    @Embedded
    private GeographicLocation location;

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
    public GeographicLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(GeographicLocation location) {
        this.location = location;
    }

    public AddressAdvert withDomicile(ImportedAdvertDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public AddressAdvert withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public AddressAdvert withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public String getLocationString() {
        return super.getLocationString() + ", " + domicile.getName();
    }

}
