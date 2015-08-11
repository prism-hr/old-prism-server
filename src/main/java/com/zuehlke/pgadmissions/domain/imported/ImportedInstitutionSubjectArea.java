package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.WeightedRelation;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "imported_institution_subject_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_institution_id",
        "imported_subject_area_id", "concentration_factor", "proliferation_factor" }) })
public class ImportedInstitutionSubjectArea extends WeightedRelation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution importedInstitution;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea subjectArea;

    @Column(name = "concentration_factor", nullable = false)
    private Integer concentrationFactor;

    @Column(name = "proliferation_factor", nullable = false)
    private BigDecimal proliferationFactor;

    @Column(name = "relation_strength", nullable = false)
    private BigDecimal relationStrength;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

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

    public void setImportedInstitution(ImportedInstitution institution) {
        this.importedInstitution = institution;
    }

    public ImportedSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(ImportedSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
    }

    public Integer getConcentrationFactor() {
        return concentrationFactor;
    }

    public void setConcentrationFactor(Integer concentrationFactor) {
        this.concentrationFactor = concentrationFactor;
    }

    public BigDecimal getProliferationFactor() {
        return proliferationFactor;
    }

    public void setProliferationFactor(BigDecimal proliferationFactor) {
        this.proliferationFactor = proliferationFactor;
    }

    @Override
    public BigDecimal getRelationStrength() {
        return relationStrength;
    }

    @Override
    public void setRelationStrength(BigDecimal relationStrength) {
        this.relationStrength = relationStrength;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("importedInstitution", importedInstitution).addProperty("subjectArea", subjectArea)
                .addProperty("concentrationFactor", concentrationFactor).addProperty("proliferationFactor", proliferationFactor);
    }

}
