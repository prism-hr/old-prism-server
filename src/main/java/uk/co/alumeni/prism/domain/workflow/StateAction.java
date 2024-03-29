package uk.co.alumeni.prism.domain.workflow;

import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "state_action", uniqueConstraints = {@UniqueConstraint(columnNames = {"state_id", "action_id"})})
public class StateAction implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "raises_urgent_flag", nullable = false)
    private Boolean raisesUrgentFlag;

    @Column(name = "replicable_sequence_start", nullable = false)
    private Boolean replicableSequenceStart;

    @Column(name = "action_enhancement")
    @Enumerated(EnumType.STRING)
    private PrismActionEnhancement actionEnhancement;

    @ManyToOne
    @JoinColumn(name = "notification_definition_id")
    private NotificationDefinition notificationDefinition;

    @OneToMany(mappedBy = "stateAction")
    private Set<StateActionAssignment> stateActionAssignments = Sets.newHashSet();

    @OneToMany(mappedBy = "stateAction")
    private Set<StateTransition> stateTransitions = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State stateId) {
        this.state = stateId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getReplicableSequenceStart() {
        return replicableSequenceStart;
    }

    public void setReplicableSequenceStart(Boolean replicableSequenceStart) {
        this.replicableSequenceStart = replicableSequenceStart;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    public NotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(NotificationDefinition notificationTemplate) {
        this.notificationDefinition = notificationTemplate;
    }

    public Set<StateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Set<StateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public StateAction withState(State state) {
        this.state = state;
        return this;
    }

    public StateAction withAction(Action action) {
        this.action = action;
        return this;
    }

    public StateAction withRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }

    public StateAction withReplicableSequenceStart(Boolean replicableSequenceStart) {
        this.replicableSequenceStart = replicableSequenceStart;
        return this;
    }

    public StateAction withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    @Override
    public String toString() {
        return state.getId().name() + "-" + action.getId().name();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("state", state).addProperty("action", action);
    }

}
