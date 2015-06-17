package com.zuehlke.pgadmissions.domain.user;

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
@Table(name = "USER_SUBJECT_AREA", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "imported_subject_area_id" }) })
public class UserSubjectArea extends UserImportedEntityRelation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "imported_subject_area_id", nullable = false)
    private ImportedSubjectArea subjectArea;

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

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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
        return super.getResourceSignature().addProperty("subjectArea", subjectArea);
    }

}
