package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;

@Entity
@Table(name = "IMPORTED_PROGRAM", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_institution_id", "name" }) })
public class ImportedProgram extends ImportedEntity<ImportedProgramMapping> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "imported_qualification_type_id", nullable = false)
    private ImportedQualificationType qualificationType;

    @Column(name = "level")
    private String level;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "homepage")
    private String homepage;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedProgram")
    private Set<ImportedProgramMapping> mappings = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public PrismImportedEntity getType() {
        return IMPORTED_PROGRAM;
    }

    public ImportedInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
    }

    public ImportedQualificationType getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(ImportedQualificationType qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String type) {
        this.qualification = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Set<ImportedProgramMapping> getMappings() {
        return mappings;
    }
    
    public ImportedProgram withInstitution(ImportedInstitution institution) {
        this.institution = institution;
        return this;
    }
    
    public ImportedProgram withQualificationType(ImportedQualificationType qualificationType) {
        this.qualificationType = qualificationType;
        return this;
    }
    
    public ImportedProgram withName(String name) {
        this.name = name;
        return this;
    }
    
    public ImportedProgram withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institution.getId(), name);
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedProgram other = (ImportedProgram) object;
        return Objects.equal(institution, other.getInstitution());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("institution", institution);
    }

}
