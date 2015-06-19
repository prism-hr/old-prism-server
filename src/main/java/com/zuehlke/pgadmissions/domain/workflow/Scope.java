package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import javax.persistence.*;

@Entity
@Table(name = "scope")
public class Scope implements UniqueEntity {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    @Column(name = "ordinal", nullable = false, unique = true)
    private Integer ordinal;

    public PrismScope getId() {
        return id;
    }

    public void setId(PrismScope id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Scope withShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public Scope withId(PrismScope id) {
        this.id = id;
        return this;
    }

    public Scope withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", id);
    }

}
