package com.zuehlke.pgadmissions.domain.resource.department;

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
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;

@Entity
@Table(name = "department_imported_subject_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "department_id",
        "imported_subject_area_id" }) })
public class DepartmentImportedSubjectArea extends WeightedRelation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
        return new ResourceSignature().addProperty("department", department).addProperty("subjectArea", subjectArea);
    }

}
