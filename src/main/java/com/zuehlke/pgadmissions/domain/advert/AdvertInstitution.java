package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.resource.Institution;

@Entity
@Table(name = "advert_institution", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id" }) })
public class AdvertInstitution extends AdvertTarget<Institution> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution value;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

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
    public Institution getValue() {
        return value;
    }

    @Override
    public void setValue(Institution institution) {
        this.value = institution;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }

    @Override
    public String getName() {
        return value.getName();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}