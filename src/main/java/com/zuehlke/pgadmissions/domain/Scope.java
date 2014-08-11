package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Entity
@Table(name = "SCOPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Scope extends WorkflowResource {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;
    
    @Column(name = "precedence", nullable = false, unique = true)
    private Integer precedence;
    
    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;
    
    @ManyToOne
    @JoinColumn(name = "fallback_action_id")
    private Action fallbackAction;
    
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
    
    public final Action getFallbackAction() {
        return fallbackAction;
    }

    public final void setFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
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
    
    public Scope withFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
        return this;
    }
    
}
