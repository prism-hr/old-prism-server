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

import com.zuehlke.pgadmissions.domain.Competence;

@Entity
@Table(name = "ADVERT_COMPETENCE", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "competence_id" }) })
public class AdvertCompetence extends AdvertTarget<Competence> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence value;

    @Column(name = "importance", nullable = false)
    private BigDecimal importance;

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

    @Override
    public Competence getValue() {
        return value;
    }

    @Override
    public void setValue(Competence competence) {
        this.value = competence;
    }

    public BigDecimal getImportance() {
        return importance;
    }

    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

    @Override
    public String getTitle() {
        return value.getTitle();
    }

}
