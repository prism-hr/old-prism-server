package uk.co.alumeni.prism.domain.resource;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.co.alumeni.prism.domain.workflow.StateGroup;
import uk.co.alumeni.prism.domain.workflow.WorkflowResource;

import javax.persistence.*;

@Entity
@Table(name = "resource_state_transition_summary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"system_id", "state_group_id", "transition_state_selection"}),
        @UniqueConstraint(columnNames = {"institution_id", "state_group_id", "transition_state_selection"}),
        @UniqueConstraint(columnNames = {"department_id", "state_group_id", "transition_state_selection"}),
        @UniqueConstraint(columnNames = {"program_id", "state_group_id", "transition_state_selection"}),
        @UniqueConstraint(columnNames = {"project_id", "state_group_id", "transition_state_selection"})})
public class ResourceStateTransitionSummary extends WorkflowResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private uk.co.alumeni.prism.domain.resource.System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "state_group_id", nullable = false)
    private StateGroup stateGroup;

    @Column(name = "transition_state_selection", nullable = false)
    private String transitionStateSelection;

    @Column(name = "frequency", nullable = false)
    private Integer frequency;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public uk.co.alumeni.prism.domain.resource.System getSystem() {
        return system;
    }

    @Override
    public void setSystem(uk.co.alumeni.prism.domain.resource.System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    public final StateGroup getStateGroup() {
        return stateGroup;
    }

    public final void setStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public final String getTransitionStateSelection() {
        return transitionStateSelection;
    }

    public final void setTransitionStateSelection(String transitionStateSelection) {
        this.transitionStateSelection = transitionStateSelection;
    }

    public final Integer getFrequency() {
        return frequency;
    }

    public final void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public final DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public final void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public ResourceStateTransitionSummary withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ResourceStateTransitionSummary withStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

    public ResourceStateTransitionSummary withTransitionStateSelection(String transitionStateSelection) {
        this.transitionStateSelection = transitionStateSelection;
        return this;
    }

    public ResourceStateTransitionSummary withFrequency(Integer frequency) {
        this.frequency = frequency;
        return this;
    }

    public ResourceStateTransitionSummary withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("stateGroup", stateGroup).addProperty("transitionStateSelection", transitionStateSelection);
    }

}
