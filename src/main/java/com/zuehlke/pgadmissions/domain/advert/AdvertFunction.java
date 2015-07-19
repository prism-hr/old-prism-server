package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;

import javax.persistence.*;

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

}
