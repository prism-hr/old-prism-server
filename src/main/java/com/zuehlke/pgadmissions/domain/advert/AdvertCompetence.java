package com.zuehlke.pgadmissions.domain.advert;

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
@Table(name = "advert_competence", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "competence_id" }) })
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

    @Column(name = "description")
    private String description;
    
    @Column(name = "importance", nullable = false)
    private Integer importance;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }

    @Override
    public String getName() {
        return value.getName();
    }

}