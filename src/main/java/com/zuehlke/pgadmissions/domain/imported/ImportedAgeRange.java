package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_AGE_RANGE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import uk.co.alumeni.prism.api.model.imported.ImportedAgeRangeDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;

@Entity
@Table(name = "imported_age_range")
public class ImportedAgeRange extends ImportedEntity<Integer>
        implements ImportedAgeRangeDefinition, ImportedEntityResponseDefinition<Integer> {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "lower_bound", nullable = false, unique = true)
    private Integer lowerBound;

    @Column(name = "upper_bound", unique = true)
    private Integer upperBound;

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
    public PrismImportedEntity getType() {
        return IMPORTED_AGE_RANGE;
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
    public Integer getLowerBound() {
        return lowerBound;
    }

    @Override
    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public Integer getUpperBound() {
        return upperBound;
    }

    @Override
    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ImportedAgeRange withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedAgeRange withLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    public ImportedAgeRange withUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    public ImportedAgeRange withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}
