package com.zuehlke.pgadmissions.domain.display;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "DISPLAY_PROPERTY_DEFINITION")
public class DisplayPropertyDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    @OrderColumn
    private PrismDisplayPropertyDefinition id;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    @OrderColumn
    private PrismDisplayPropertyCategory category;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismDisplayPropertyDefinition getId() {
        return id;
    }

    public void setId(PrismDisplayPropertyDefinition id) {
        this.id = id;
    }

    public final PrismDisplayPropertyCategory getCategory() {
        return category;
    }

    public final void setCategory(PrismDisplayPropertyCategory category) {
        this.category = category;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public DisplayPropertyDefinition withId(PrismDisplayPropertyDefinition id) {
        this.id = id;
        return this;
    }

    public DisplayPropertyDefinition withCategory(PrismDisplayPropertyCategory displayPropertyCategory) {
        this.category = displayPropertyCategory;
        return this;
    }

    public DisplayPropertyDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
