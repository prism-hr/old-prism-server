package com.zuehlke.pgadmissions.domain.imported;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedProgramDefinition;

import javax.persistence.*;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;

@Entity
@Table(name = "IMPORTED_PROGRAM", uniqueConstraints = {@UniqueConstraint(columnNames = {"imported_institution_id", "name"})})
public class ImportedProgram extends ImportedEntity<Integer, ImportedProgramMapping>
        implements ImportedProgramDefinition<ImportedInstitution, ImportedEntitySimple>, ImportedEntityResponseDefinition<Integer> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "imported_qualification_type_id", nullable = false)
    private ImportedEntitySimple qualificationType;

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

    @OneToMany(mappedBy = "importedEntity")
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

    @Override
    public ImportedInstitution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
    }

    @Override
    public ImportedEntitySimple getQualificationType() {
        return qualificationType;
    }

    @Override
    public void setQualificationType(ImportedEntitySimple qualificationType) {
        this.qualificationType = qualificationType;
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String getQualification() {
        return qualification;
    }

    @Override
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

    @Override
    public String getHomepage() {
        return homepage;
    }

    @Override
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

    public ImportedProgram withQualificationType(ImportedEntitySimple qualificationType) {
        this.qualificationType = qualificationType;
        return this;
    }

    public ImportedProgram withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedProgram withHomepage(final String homepage) {
        this.homepage = homepage;
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
    public int index() {
        return Objects.hashCode(institution.getId(), name);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("institution", institution);
    }

}
