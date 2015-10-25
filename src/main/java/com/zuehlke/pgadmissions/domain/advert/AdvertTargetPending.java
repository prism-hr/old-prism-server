package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.workflow.user.AdvertTargetPendingReassignmentProcessor;

@Entity
@Table(name = "state_action_pending")
public class AdvertTargetPending implements UserAssignment<AdvertTargetPendingReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "advert_target_invite_list")
    private String advertTargetInviteList;

    @Lob
    @Column(name = "advert_target_connect_list")
    private String advertTargetConnectList;

    @Lob
    @Column(name = "advert_target_message")
    private String advertTargetMessage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAdvertTargetInviteList() {
        return advertTargetInviteList;
    }

    public void setAdvertTargetInviteList(String advertTargetInviteList) {
        this.advertTargetInviteList = advertTargetInviteList;
    }

    public String getAdvertTargetConnectList() {
        return advertTargetConnectList;
    }

    public void setAdvertTargetConnectList(String advertTargetConnectList) {
        this.advertTargetConnectList = advertTargetConnectList;
    }

    public String getAdvertTargetMessage() {
        return advertTargetMessage;
    }

    public void setAdvertTargetMessage(String advertTargetMessage) {
        this.advertTargetMessage = advertTargetMessage;
    }

    public AdvertTargetPending withUser(User user) {
        this.user = user;
        return this;
    }

    public AdvertTargetPending withAdvertTargetInviteList(String advertTargetInviteList) {
        this.advertTargetInviteList = advertTargetInviteList;
        return this;
    }

    public AdvertTargetPending withAdvertTargetConnectList(String advertTargetConnectList) {
        this.advertTargetConnectList = advertTargetConnectList;
        return this;
    }

    public AdvertTargetPending withAdvertTargetMessage(String advertTargetMessage) {
        this.advertTargetMessage = advertTargetMessage;
        return this;
    }

    @Override
    public Class<AdvertTargetPendingReassignmentProcessor> getUserReassignmentProcessor() {
        return AdvertTargetPendingReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}
