package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;

@Entity
@Table(name = "imported_entity", uniqueConstraints = { @UniqueConstraint(columnNames = { "code", "imported_entity_type_id" }),
        @UniqueConstraint(columnNames = { "name", "imported_entity_type_id" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "imported_entity_type_id", discriminatorType = DiscriminatorType.STRING)
public abstract class ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String code;

    private String name;

    private boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("code", code).add("name", name).toString();
    }

}
