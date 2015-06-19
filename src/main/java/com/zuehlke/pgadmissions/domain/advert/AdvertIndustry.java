package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

import javax.persistence.*;

@Entity
@Table(name = "advert_industry", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "industry" }),
        @UniqueConstraint(columnNames = { "industry", "advert_id" }) })
public class AdvertIndustry extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "industry", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertIndustry industry;

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

    public final PrismAdvertIndustry getIndustry() {
        return industry;
    }

    public final void setIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
    }

    @Override
    public Object getValue() {
        return getIndustry();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("industry", industry);
    }

}
