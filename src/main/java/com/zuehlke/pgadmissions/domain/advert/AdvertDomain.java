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

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;

@Entity
@Table(name = "ADVERT_DOMAIN", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "domain" }),
        @UniqueConstraint(columnNames = { "domain", "advert_id" }) })
public class AdvertDomain extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "domain", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertDomain domain;

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

    public final PrismAdvertDomain getDomain() {
        return domain;
    }

    public final void setDomain(PrismAdvertDomain domain) {
        this.domain = domain;
    }

    @Override
    public Object getValue() {
        return getDomain();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("domain", domain);
    }

}
