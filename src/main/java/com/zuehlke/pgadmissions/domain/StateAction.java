package com.zuehlke.pgadmissions.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "STATE_ACTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_id", "action_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateAction  {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "raises_urgent_flag", nullable = false)
    private boolean raisesUrgentFlag;
    
    @Column(name = "is_default_action", nullable = false)
    private boolean defaultAction;    

    @ManyToOne
    @JoinColumn(name = "notification_template_id")
    private NotificationTemplate notificationTemplate;

    @Column(name = "precedence")
    private Integer precedence;

    @OneToMany(mappedBy = "stateAction")
    private Set<StateActionAssignment> stateActionAssignments = new HashSet<StateActionAssignment>();
    
    @OneToMany(mappedBy = "stateAction")
    private Set<StateActionNotification> stateActionNotifications = new HashSet<StateActionNotification>();

    @OneToMany(mappedBy = "stateAction")
    private Set<StateTransition> stateTransitions = new HashSet<StateTransition>();

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

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public boolean isDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
    }

    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }

    public Set<StateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Set<StateActionNotification> getStateActionNotifications() {
        return stateActionNotifications;
    }

    public void setStateActionNotifications(Set<StateActionNotification> stateActionNotifications) {
        this.stateActionNotifications = stateActionNotifications;
    }

    public Set<StateTransition> getStateTransitions() {
        return stateTransitions;
    }

}
