package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "advert_competency", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "competency" }),
        @UniqueConstraint(columnNames = { "competency", "advert_id" }) })
public class AdvertCompetency extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "competency", nullable = false)
    private String competency;

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

    public final String getCompetency() {
        return competency;
    }

    public final void setCompetency(String competency) {
        this.competency = competency;
    }

    @Override
    public Object getValue() {
        return getCompetency();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("competency", competency);
    }

}
