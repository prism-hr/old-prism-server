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
@Table(name = "advert_target_advert", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "target_advert_id" }) })
public class AdvertTargetAdvert extends AdvertTarget<Advert> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "target_advert_id", nullable = false)
    private Advert value;

    @Column(name = "selected", nullable = false)
    private Boolean selected;

    @Column(name = "endorsed", nullable = false)
    private Boolean endorsed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Advert getValue() {
        return value;
    }

    public void setValue(Advert value) {
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getEndorsed() {
        return endorsed;
    }

    public void setEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }

    @Override
    public String getName() {
        return value.getName();
    }

    public AdvertTargetAdvert withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertTargetAdvert withValue(Advert value) {
        this.value = value;
        return this;
    }

    public AdvertTargetAdvert withSelected(Boolean selected) {
        this.selected = selected;
        return this;
    }

    public AdvertTargetAdvert withEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
        return this;
    }
    
}
