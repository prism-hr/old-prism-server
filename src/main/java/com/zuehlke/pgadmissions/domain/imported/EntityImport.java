package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "entity_import")
public class EntityImport {

    @Id
    @Column(name = "imported_entity_type")
    @Enumerated(EnumType.STRING)
    private PrismImportedEntity importedEntityType;

    @Column(name = "last_imported_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastImportedTimestamp;

    public PrismImportedEntity getImportedEntityType() {
        return importedEntityType;
    }

    public void setImportedEntityType(PrismImportedEntity importedEntityType) {
        this.importedEntityType = importedEntityType;
    }

    public DateTime getLastImportedTimestamp() {
        return lastImportedTimestamp;
    }

    public void setLastImportedTimestamp(DateTime lastImportedTimestamp) {
        this.lastImportedTimestamp = lastImportedTimestamp;
    }

    public EntityImport withImportedEntityType(final PrismImportedEntity importedEntityType) {
        this.importedEntityType = importedEntityType;
        return this;
    }

}
