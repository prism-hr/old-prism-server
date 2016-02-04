package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;

@Entity
@Table(name = "ACTION_CUSTOM_QUESTION_DEFINITION")
public class ActionCustomQuestionDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCustomQuestionDefinition id;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    public final PrismActionCustomQuestionDefinition getId() {
        return id;
    }

    public final void setId(PrismActionCustomQuestionDefinition id) {
        this.id = id;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public ActionCustomQuestionDefinition withId(PrismActionCustomQuestionDefinition id) {
        this.id = id;
        return this;
    }

    public ActionCustomQuestionDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
