package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;

import javax.persistence.*;

@Entity
@Table(name = "state_group", uniqueConstraints = {@UniqueConstraint(columnNames = {"scope_id", "ordinal"})})
public class StateGroup extends WorkflowDefinition {

    @Id
    @Enumerated(EnumType.STRING)
    private PrismStateGroup id;

    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "repeatable")
    private Boolean repeatable;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismStateGroup getId() {
        return id;
    }

    public void setId(PrismStateGroup id) {
        this.id = id;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public final Boolean getRepeatable() {
        return repeatable;
    }

    public final void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public StateGroup withId(PrismStateGroup id) {
        this.id = id;
        return this;
    }

    public StateGroup withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    public StateGroup withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
