package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import javax.persistence.*;

@Entity
@Table(name = "SCOPE")
public class Scope implements IUniqueEntity {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;

    @Column(name = "precedence", nullable = false, unique = true)
    private Integer precedence;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    public PrismScope getId() {
        return id;
    }

    public void setId(PrismScope id) {
        this.id = id;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }

    public Scope withId(PrismScope id) {
        this.id = id;
        return this;
    }

    public Scope withPrecedence(Integer precedence) {
        this.precedence = precedence;
        return this;
    }

    public Scope withShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", id);
    }

}
