package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;

@Entity
@Table(name = "USER_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "imported_institution_id" }) })
public class UserInstitution extends UserImportedEntityRelation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

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

    public ImportedInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
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
        return super.getResourceSignature().addProperty("institution", institution);
    }

}
