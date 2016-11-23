package uk.co.alumeni.prism.domain.advert;

import com.google.common.base.Objects;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "advert_location", uniqueConstraints = {@UniqueConstraint(columnNames = {"advert_id", "location_advert_id"})})
public class AdvertLocation extends AdvertAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "location_advert_id", nullable = false)
    private Advert locationAdvert;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Advert getLocationAdvert() {
        return locationAdvert;
    }

    public void setLocationAdvert(Advert locationAdvert) {
        this.locationAdvert = locationAdvert;
    }

    public AdvertLocation withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertLocation withLocationAdvert(Advert locationAdvert) {
        this.locationAdvert = locationAdvert;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, locationAdvert);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertLocation other = (AdvertLocation) object;
        return equal(advert, other.getAdvert()) && equal(locationAdvert, other.getLocationAdvert());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("locationAdvert", locationAdvert);
    }

}
