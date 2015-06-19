package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;

import javax.persistence.*;

@Entity
@Table(name = "advert_domain", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "domain" }),
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
