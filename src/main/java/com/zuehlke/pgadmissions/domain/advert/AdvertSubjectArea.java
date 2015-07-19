package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

import javax.persistence.*;

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
    public String getTitle() {
        return value.getName();
    }

}
