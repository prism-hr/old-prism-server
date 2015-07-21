package com.zuehlke.pgadmissions.domain.imported;

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
    private ImportedInstitution importedInstitution;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea importedSubjectArea;

    @Column(name = "relation_strength", nullable = false)
    private Integer relationStrength;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedInstitution getImportedInstitution() {
        return importedInstitution;
    }

    public void setImportedInstitution(ImportedInstitution importedInstitution) {
        this.importedInstitution = importedInstitution;
    }

    public ImportedSubjectArea getImportedSubjectArea() {
        return importedSubjectArea;
    }

    public void setImportedSubjectArea(ImportedSubjectArea importedSubjectArea) {
        this.importedSubjectArea = importedSubjectArea;
    }

    @Override
    public Integer getRelationStrength() {
        return relationStrength;
    }

    @Override
    public void setRelationStrength(Integer relationStrength) {
        this.relationStrength = relationStrength;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("importedInstitution", importedInstitution).addProperty("importedSubjectArea", importedSubjectArea);
    }

}
