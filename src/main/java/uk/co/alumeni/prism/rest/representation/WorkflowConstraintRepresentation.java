package uk.co.alumeni.prism.rest.representation;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint;

public class WorkflowConstraintRepresentation {

    private PrismWorkflowConstraint constraint;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    public PrismWorkflowConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(PrismWorkflowConstraint constraint) {
        this.constraint = constraint;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public void setMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public void setMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
    }

    public WorkflowConstraintRepresentation withConstraint(PrismWorkflowConstraint constraint) {
        this.constraint = constraint;
        return this;
    }

    public WorkflowConstraintRepresentation withMinimumPermitted(Integer minimumPermitted) {
        this.minimumPermitted = minimumPermitted;
        return this;
    }

    public WorkflowConstraintRepresentation withMaximumPermitted(Integer maximumPermitted) {
        this.maximumPermitted = maximumPermitted;
        return this;
    }

}
