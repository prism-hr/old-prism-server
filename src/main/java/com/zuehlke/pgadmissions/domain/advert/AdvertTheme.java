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
@Table(name = "advert_theme", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "theme" }) })
public class AdvertTheme extends AdvertAttribute<String> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "theme", nullable = false)
    private String value;

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
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String theme) {
        this.value = theme;
    }

    public AdvertTheme withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertTheme withValue(String value) {
        this.value = value;
        return this;
    }

}
