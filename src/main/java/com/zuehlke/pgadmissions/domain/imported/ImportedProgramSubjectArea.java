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
@Table(name = "imported_program_subject_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_program_id", "imported_subject_area_id" }) })
public class ImportedProgramSubjectArea extends WeightedRelation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_program_id", nullable = false)
    private ImportedProgram program;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea subjectArea;

    @Column(name = "relation_strength", nullable = false)
    private Integer relationStrength;

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

    public ImportedProgram getProgram() {
        return program;
    }

    public void setProgram(ImportedProgram program) {
        this.program = program;
    }

    public ImportedSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(ImportedSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
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
        return new ResourceSignature().addProperty("program", program).addProperty("subjectArea", subjectArea);
    }

}
