package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;

@Entity
@Table(name = "ADVERT_ADVERT_TYPE", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "advert_type" }),
        @UniqueConstraint(columnNames = { "advert_type", "advert_id" }) })
public class AdvertAdvertType extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "advert_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertType advertType;

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

    public final PrismAdvertType getAdvertType() {
        return advertType;
    }

    public final void setAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
    }

    @Override
    public Object getValue() {
        return getAdvertType();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("advertType", advertType);
    }

}
