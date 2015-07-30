package com.zuehlke.pgadmissions.domain.imported;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.WeightedRelation;

@Entity
@Table(name = "imported_institution_subject_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_institution_id",
        "imported_subject_area_id" }) })
public class ImportedInstitutionSubjectArea extends WeightedRelation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea subjectArea;

    @Column(name = "relation_strength", nullable = false)
    private BigDecimal relationStrength;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
    }

    public ImportedSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(ImportedSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
    }

    @Override
    public BigDecimal getRelationStrength() {
        return relationStrength;
    }

    @Override
    public void setRelationStrength(BigDecimal relationStrength) {
        this.relationStrength = relationStrength;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", institution).addProperty("subjectArea", subjectArea);
    }

}
