package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Entity
@Table(name = "imported_entity_type")
public class ImportedEntityType implements UniqueEntity {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismImportedEntity id;

    @Column(name = "last_imported_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastImportedTimestamp;

    public PrismImportedEntity getId() {
        return id;
    }

    public void setId(PrismImportedEntity id) {
        this.id = id;
    }

    public DateTime getLastImportedTimestamp() {
        return lastImportedTimestamp;
    }

    public ImportedEntityType withId(PrismImportedEntity id) {
        this.id = id;
        return this;
    }

    public void setLastImportedTimestamp(DateTime lastImportedTimestamp) {
        this.lastImportedTimestamp = lastImportedTimestamp;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}
