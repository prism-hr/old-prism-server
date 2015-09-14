package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_ADVERT_DOMICILE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import uk.co.alumeni.prism.api.model.imported.ImportedDomicileDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;

@Entity
@Table(name = "imported_domicile")
public class ImportedDomicile extends ImportedEntity<String>
        implements ImportedDomicileDefinition, ImportedEntityResponseDefinition<String> {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public PrismImportedEntity getType() {
        return IMPORTED_ADVERT_DOMICILE;
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
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ImportedDomicile withId(String id) {
        this.id = id;
        return this;
    }

    public ImportedDomicile withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedDomicile withCurrency(final String currency) {
        this.currency = currency;
        return this;
    }

    public ImportedDomicile withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String index() {
        return id.toString();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}