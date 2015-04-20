package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;

@Entity
@Table(name = "ADVERT_LOCATION", uniqueConstraints = @UniqueConstraint(columnNames = { "advert_id", "location" }))
public class AdvertLocation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", insertable = false, updatable = false)
    private Advert advert;

    @Column(name = "location", nullable = false)
    private String location;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        this.location = location;
    }

    public AdvertLocation withLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, location);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final AdvertLocation other = (AdvertLocation) object;
        return Objects.equal(advert, other.getAdvert()) && Objects.equal(location, other.getLocation());
    }

}
