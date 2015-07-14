package com.zuehlke.pgadmissions.domain.imported.mapping;

import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "imported_program_mapping", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "imported_program_id", "code" }) })
public class ImportedProgramMapping extends ImportedEntityMapping<ImportedProgram> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "imported_program_id", nullable = false)
    private ImportedProgram importedEntity;

    @Column(name = "code")
    private String code;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "imported_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime importedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public ImportedProgram getImportedEntity() {
        return importedEntity;
    }

    @Override
    public void setImportedEntity(ImportedProgram importedProgram) {
        this.importedEntity = importedProgram;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
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
    public DateTime getImportedTimestamp() {
        return importedTimestamp;
    }

    public ImportedProgramMapping withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ImportedProgramMapping withImportedProgram(ImportedProgram importedProgram) {
        this.importedEntity = importedProgram;
        return this;
    }

    public ImportedProgramMapping withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ImportedProgramMapping withImportedTimestamp(final DateTime importedTimestamp) {
        this.importedTimestamp = importedTimestamp;
        return this;
    }


    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("importedEntity", importedEntity);
    }

}
