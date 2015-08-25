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

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;

@Entity
@Table(name = "advert_function", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "function" }) })
public class AdvertFunction extends AdvertAttribute<PrismAdvertFunction> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "function", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertFunction value;

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
    public final PrismAdvertFunction getValue() {
        return value;
    }

    @Override
    public final void setValue(PrismAdvertFunction function) {
        this.value = function;
    }
    
    public AdvertFunction withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertFunction withValue(PrismAdvertFunction value) {
        this.value = value;
        return this;
    }

}
