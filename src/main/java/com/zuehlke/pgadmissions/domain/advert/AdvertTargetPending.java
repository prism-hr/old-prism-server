package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "advert_target_list")
    private String advertTargetList;

    @Lob
    @Column(name = "advert_target_message")
    private String advertTargetMessage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAdvertTargetList() {
        return advertTargetList;
    }

    public void setAdvertTargetList(String advertTargetList) {
        this.advertTargetList = advertTargetList;
    }

    public String getAdvertTargetMessage() {
        return advertTargetMessage;
    }

    public void setAdvertTargetMessage(String advertTargetMessage) {
        this.advertTargetMessage = advertTargetMessage;
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
