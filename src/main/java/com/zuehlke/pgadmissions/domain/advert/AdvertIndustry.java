package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

import javax.persistence.*;

@Entity
@Table(name = "ADVERT_INDUSTRY", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "industry" }) })
public class AdvertIndustry extends AdvertAttribute<PrismAdvertIndustry> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "industry", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertIndustry value;

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

    @Override
    public final PrismAdvertIndustry getValue() {
        return value;
    }

    @Override
    public final void setValue(PrismAdvertIndustry industry) {
        this.value = industry;
    }

}
