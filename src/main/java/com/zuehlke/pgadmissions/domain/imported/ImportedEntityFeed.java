package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.resource.Institution;

@Entity
@Table(name = "imported_entity_feed", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "imported_entity_type" }) })
public class ImportedEntityFeed implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "imported_entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismImportedEntity importedEntityType;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "last_imported_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastImportedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public PrismImportedEntity getImportedEntityType() {
        return importedEntityType;
    }

    public void setImportedEntityType(PrismImportedEntity importedEntityType) {
        this.importedEntityType = importedEntityType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public final DateTime getLastImportedTimestamp() {
        return lastImportedTimestamp;
    }

    public final void setLastImportedTimestamp(DateTime lastImportedTimestamp) {
        this.lastImportedTimestamp = lastImportedTimestamp;
    }

    public ImportedEntityFeed withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ImportedEntityFeed withImportedEntityType(PrismImportedEntity importedEntityType) {
        this.importedEntityType = importedEntityType;
        return this;
    }

    public ImportedEntityFeed withLocation(String location) {
        this.location = location;
        return this;
    }

    public ImportedEntityFeed withLastUploadedTimestamp(DateTime lastUploadedTimestamp) {
        this.lastImportedTimestamp = lastUploadedTimestamp;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", institution).addProperty("importedEntityType", importedEntityType);
    }

}
