package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

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
@Table(name = "ADVERT_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id" }) })
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

    @Column(name = "importance", nullable = false)
    private BigDecimal importance;

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
    public BigDecimal getImportance() {
        return importance;
    }

    @Override
    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

    @Override
    public String getTitle() {
        return value.getTitle();
    }

}
