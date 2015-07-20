package com.zuehlke.pgadmissions.domain.workflow;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;

@Entity
@Table(name = "state_action", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_id", "action_id" }) })
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

    @Column(name = "action_condition")
    @Enumerated(EnumType.STRING)
    private PrismActionCondition actionCondition;

    @Column(name = "action_enhancement")
    @Enumerated(EnumType.STRING)
    private PrismActionEnhancement actionEnhancement;

    @ManyToOne
    @JoinColumn(name = "notification_definition_id")
    private NotificationDefinition notificationDefinition;

    @OneToMany(mappedBy = "stateAction")
    private Set<StateActionAssignment> stateActionAssignments = Sets.newHashSet();

    @OneToMany(mappedBy = "stateAction")
    private Set<StateActionNotification> stateActionNotifications = Sets.newHashSet();

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

    public NotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(NotificationDefinition notificationTemplate) {
        this.notificationDefinition = notificationTemplate;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    public Set<StateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Set<StateActionNotification> getStateActionNotifications() {
        return stateActionNotifications;
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

    public StateAction withRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }

    public StateAction withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public StateAction withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("state", state).addProperty("action", action);
    }

}
