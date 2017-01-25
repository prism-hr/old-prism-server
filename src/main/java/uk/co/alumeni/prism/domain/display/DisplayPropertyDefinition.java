package uk.co.alumeni.prism.domain.display;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyCategory;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;

import javax.persistence.*;

@Entity
@Table(name = "display_property_definition")
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
