package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.workflow.user.UserConnectionReassignmentProcessor;

@Entity
@Table(name = "user_connection", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_requested_id", "user_connected_id" }),
        @UniqueConstraint(columnNames = { "user_connected_id", "user_requested_id" }) })
public class UserConnection implements UniqueEntity, UserAssignment<UserConnectionReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "connected", nullable = false)
    private Boolean connected;

    @ManyToOne
    @JoinColumn(name = "user_requested_id", nullable = false)
    private User userRequested;

    @ManyToOne
    @JoinColumn(name = "user_connected_id", nullable = false)
    private User userConnected;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public User getUserRequested() {
        return userRequested;
    }

    public void setUserRequested(User userRequested) {
        this.userRequested = userRequested;
    }

    public User getUserConnected() {
        return userConnected;
    }

    public void setUserConnected(User userConnected) {
        this.userConnected = userConnected;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public UserConnection withUserRequested(User userRequested) {
        this.userRequested = userRequested;
        return this;
    }

    public UserConnection withUserConnected(User userConnected) {
        this.userConnected = userConnected;
        return this;
    }

    public UserConnection withConnected(Boolean connected) {
        this.connected = connected;
        return this;
    }

    public UserConnection withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    @Override
    public Class<UserConnectionReassignmentProcessor> getUserReassignmentProcessor() {
        return UserConnectionReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("userRequested", userRequested).addProperty("userConnected", userConnected);
    }

}
