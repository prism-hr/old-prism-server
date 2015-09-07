package com.zuehlke.pgadmissions.domain.imported;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedProgramMapping;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedProgramDefinition;

import javax.persistence.*;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;

@Entity
@Table(name = "imported_program", uniqueConstraints = {@UniqueConstraint(columnNames = {"imported_institution_id", "qualification", "name"})})
public class ImportedProgram extends ImportedEntity<Integer, ImportedProgramMapping> implements
        ImportedProgramDefinition<ImportedInstitution, ImportedEntitySimple, PrismQualificationLevel>, ImportedEntityResponseDefinition<Integer>,
        ImportedEntityIndexable, TargetEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "imported_qualification_type_id", nullable = false)
    private ImportedEntitySimple qualificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private PrismQualificationLevel level;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ucas_code")
    private String ucasCode;

    @Column(name = "ucas_program_count")
    private Integer ucasProgramCount;

    @Column(name = "jacs_codes")
    private String jacsCodes;

    @Column(name = "ucas_subjects")
    private String ucasSubjects;

    @Column(name = "indexed", nullable = false)
    private Boolean indexed;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedEntity")
    private Set<ImportedProgramMapping> mappings = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<ImportedProgramSubjectArea> programSubjectAreas = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "department_imported_program", joinColumns = {@JoinColumn(name = "imported_program_id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "department_id", nullable = false)})
    private Set<Department> deparments = Sets.newHashSet();

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
    public PrismQualificationLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(PrismQualificationLevel level) {
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

    public String getUcasCode() {
        return ucasCode;
    }

    public void setUcasCode(String ucasCode) {
        this.ucasCode = ucasCode;
    }

    public Integer getUcasProgramCount() {
        return ucasProgramCount;
    }

    public void setUcasProgramCount(Integer ucasProgramCount) {
        this.ucasProgramCount = ucasProgramCount;
    }

    public String getJacsCodes() {
        return jacsCodes;
    }

    public void setJacsCodes(String jacsCodes) {
        this.jacsCodes = jacsCodes;
    }

    public String getUcasSubjects() {
        return ucasSubjects;
    }

    public void setUcasSubjects(String ucasSubjects) {
        this.ucasSubjects = ucasSubjects;
    }

    @Override
    public Boolean getIndexed() {
        return indexed;
    }

    @Override
    public void setIndexed(Boolean indexed) {
        this.indexed = indexed;
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

    public void setMappings(Set<ImportedProgramMapping> mappings) {
        this.mappings = mappings;
    }

    public Set<ImportedProgramSubjectArea> getProgramSubjectAreas() {
        return programSubjectAreas;
    }

    public Set<Department> getDeparments() {
        return deparments;
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

    public ImportedProgram withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ImportedProgram withIndexed(Boolean indexed) {
        this.indexed = indexed;
        return this;
    }

    @Override
    public String index() {
        String ucasIds = institution.getUcasIds();
        return Strings.nullToEmpty(ucasIds) + qualification + super.index();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institution.getId(), qualification, name);
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedProgram other = (ImportedProgram) object;
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(qualification, other.getQualification());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("institution", institution).addProperty("qualification", qualification);
    }

}
