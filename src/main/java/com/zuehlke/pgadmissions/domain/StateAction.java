package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
@Table(name = "STATE_ACTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_id", "action_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateAction implements IUniqueResource {

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
    private boolean raisesUrgentFlag;
    
    @Column(name = "is_default_action", nullable = false)
    private boolean defaultAction;
    
    @Column(name = "do_post_comment", nullable = false)
    private boolean postComment;

    @ManyToOne
    @JoinColumn(name = "notification_template_id")
    private NotificationTemplate notificationTemplate;
    
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

    public boolean isPostComment() {
        return postComment;
    }

    public void setPostComment(boolean postComment) {
        this.postComment = postComment;
    }

    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
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
    
    public StateAction withDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
        return this;
    }
    
    public StateAction withPostComment(boolean postComment) {
        this.postComment = postComment;
        return this;
    }
    
    public StateAction withNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("state", state);
        properties.put("action", action);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
