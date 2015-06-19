package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.*;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.AGE_RANGE;

@Entity
@Table(name = "imported_age_range", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "code" }) })
public class AgeRange extends ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "code", nullable = false)
    private String code;

    @Lob
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lower_bound", nullable = false)
    private Integer lowerBound;

    @Column(name = "upper_bound")
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
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public PrismImportedEntity getType() {
        return AGE_RANGE;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

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

    public AgeRange withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public AgeRange withCode(String code) {
        this.code = code;
        return this;
    }

    public AgeRange withName(String name) {
        this.name = name;
        return this;
    }

    public AgeRange withLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    public AgeRange withUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    public AgeRange withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}
