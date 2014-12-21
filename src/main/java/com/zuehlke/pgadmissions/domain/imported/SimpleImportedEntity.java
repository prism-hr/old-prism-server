package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.*;

@Entity
@Table(name = "imported_entity", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "imported_entity_type", "code" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "imported_entity_type", discriminatorType = DiscriminatorType.STRING)
public class SimpleImportedEntity extends ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "imported_entity_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private PrismImportedEntity type;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

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

    @Override
    public final PrismImportedEntity getType() {
        return type;
    }

    public final void setType(PrismImportedEntity type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
