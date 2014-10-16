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
@Table(name = "ADVERT_FUNCTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "function" }),
        @UniqueConstraint(columnNames = { "function", "advert_id" }) })
public class AdvertFunction extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "function", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertFunction function;

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

    public final PrismAdvertFunction getFunction() {
        return function;
    }

    public final void setFunction(PrismAdvertFunction function) {
        this.function = function;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("function", function);
    }

}