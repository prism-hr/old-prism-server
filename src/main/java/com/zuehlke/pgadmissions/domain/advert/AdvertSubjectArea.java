package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

@Entity
@Table(name = "advert_subject_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "imported_subject_area_id" }) })
public class AdvertSubjectArea extends AdvertTarget<ImportedSubjectArea> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea value;

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
    public ImportedSubjectArea getValue() {
        return value;
    }

    @Override
    public void setValue(ImportedSubjectArea subjectArea) {
        this.value = subjectArea;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }
    
    @Override
    public String getName() {
        return value.getName();
    }

    public AdvertSubjectArea withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertSubjectArea withValue(ImportedSubjectArea value) {
        this.value = value;
        return this;
    }

}
