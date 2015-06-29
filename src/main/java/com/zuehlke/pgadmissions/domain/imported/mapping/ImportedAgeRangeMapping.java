package com.zuehlke.pgadmissions.domain.imported.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.resource.Institution;

@Entity
@Table(name = "IMPORTED_AGE_RANGE_MAPPING", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id, imported_age_range_id, code" }) })
public class ImportedAgeRangeMapping extends ImportedEntityMapping<ImportedAgeRange> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "imported_age_range_id", nullable = false)
    private ImportedAgeRange importedAgeRange;

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
    public ImportedAgeRange getImportedEntity() {
        return importedAgeRange;
    }
    
    public ImportedAgeRange getImportedAgeRange() {
        return importedAgeRange;
    }

    public void setImportedAgeRange(ImportedAgeRange importedAgeRange) {
        this.importedAgeRange = importedAgeRange;
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
   
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("importedAgeRange", importedAgeRange);
    }

}
