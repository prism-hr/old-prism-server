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

import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

@Entity
@Table(name = "ADVERT_SUBJECT_AREA", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "imported_subject_area_id" }) })
public class AdvertSubjectArea extends AdvertTarget<ImportedSubjectArea> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedSubjectArea subjectArea;

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

    public ImportedSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(ImportedSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
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
    public ImportedSubjectArea getValue() {
        return subjectArea;
    }

    @Override
    public void setValue(ImportedSubjectArea value) {
        setSubjectArea(value);
    }
    
    @Override
    public String getTitle() {
        return subjectArea.getName();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("subjectArea", subjectArea);
    }

}
