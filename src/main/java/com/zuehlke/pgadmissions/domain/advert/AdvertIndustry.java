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

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

@Entity
@Table(name = "ADVERT_INDUSTRY", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "industry" }) })
public class AdvertIndustry extends AdvertAttribute<PrismAdvertIndustry> {

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
    public PrismAdvertIndustry getValue() {
        return industry;
    }
    
    @Override
    public void setValue(PrismAdvertIndustry value) {
        setIndustry(value);
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("industry", industry);
    }

}
