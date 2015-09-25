package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.workflow.user.AdvertConnectionReassignmentProcessor;

@Entity
@Table(name = "advert_connection", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "advert_id", "advert_user_id", "target_advert_id", "target_user_id" }) })
public class AdvertConnection implements UniqueEntity, UserAssignment<AdvertConnectionReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "advert_user_id")
    private User advertUser;

    @ManyToOne
    @JoinColumn(name = "target_advert_id", nullable = false)
    private Advert targetAdvert;

    @ManyToOne
    @JoinColumn(name = "target_advert_user_id")
    private User targetAdvertUser;
    
    @ManyToOne
    @JoinColumn(name = "accepting_user_id")
    private User acceptingUser;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

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

    public User getAdvertUser() {
        return advertUser;
    }

    public void setAdvertUser(User advertUser) {
        this.advertUser = advertUser;
    }

    public Advert getTargetAdvert() {
        return targetAdvert;
    }

    public void setTargetAdvert(Advert targetAdvert) {
        this.targetAdvert = targetAdvert;
    }

    public User getTargetAdvertUser() {
        return targetAdvertUser;
    }

    public void setTargetAdvertUser(User targetAdvertUser) {
        this.targetAdvertUser = targetAdvertUser;
    }

    public User getAcceptingUser() {
        return acceptingUser;
    }

    public void setAcceptingUser(User acceptingUser) {
        this.acceptingUser = acceptingUser;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
    
    public AdvertConnection withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertConnection withAdvertUser(User advertUser) {
        this.advertUser = advertUser;
        return this;
    }
    
    public AdvertConnection withTargetAdvert(Advert targetAdvert) {
        this.targetAdvert = targetAdvert;
        return this;
    }

    public AdvertConnection withTargetAdvertUser(User targetAdvertUser) {
        this.targetAdvertUser = targetAdvertUser;
        return this;
    }

    public AdvertConnection withAcceptingUser(User acceptingUser) {
        this.acceptingUser = acceptingUser;
        return this;
    }
    
    public AdvertConnection withAccepted(Boolean accepted) {
        this.accepted = accepted;
        return this;
    }

    @Override
    public Class<AdvertConnectionReassignmentProcessor> getUserReassignmentProcessor() {
        return AdvertConnectionReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("advert", advert).addProperty("advertUser", advertUser) //
                .addProperty("targetAdvert", targetAdvert).addProperty("targetAdvertUser", targetAdvertUser);
    }

}
