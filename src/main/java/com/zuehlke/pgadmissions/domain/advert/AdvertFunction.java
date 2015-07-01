package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;

import javax.persistence.*;

@Entity
@Table(name = "advert_function", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "function" }),
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
    public Object getValue() {
        return getFunction();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("function", function);
    }

}
