package com.zuehlke.pgadmissions.domain.imported.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "IMPORTED_LANGUAGE_QUALIFICATION_TYPE_MAPPING", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id, imported_language_qualification_type_id, code" }) })
public class ImportedLanguageQualificationTypeMapping extends ImportedEntityMapping {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "imported_age_range_id", nullable = false)
    private ImportedLanguageQualificationType importedLanguageQualificationType;

    @Column(name = "code")
    private String code;

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

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public ImportedLanguageQualificationType getImportedLanguageQualificationType() {
        return importedLanguageQualificationType;
    }

    public void setImportedLanguageQualificationType(ImportedLanguageQualificationType importedLanguageQualificationType) {
        this.importedLanguageQualificationType = importedLanguageQualificationType;
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
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("importedLanguageQualificationType", importedLanguageQualificationType);
    }

}
