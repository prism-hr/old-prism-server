package uk.co.alumeni.prism.domain.advert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "advert_function", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "function" }) })
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
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("locationAdvert", locationAdvert);
    }

}
