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
        @UniqueConstraint(columnNames = { "inviting_user_id", "inviting_advert_id", "receiving_user_id", "receiving_advert_id" }) })
public class AdvertConnection implements UniqueEntity, UserAssignment<AdvertConnectionReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inviting_user_id", nullable = false)
    private User invitingUser;

    @ManyToOne
    @JoinColumn(name = "inviting_advert_id", nullable = false)
    private Advert invitingAdvert;

    @ManyToOne
    @JoinColumn(name = "receiving_user_id")
    private User receivingUser;

    @ManyToOne
    @JoinColumn(name = "receiving_advert_id")
    private Advert receivingAdvert;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(User invitingUser) {
        this.invitingUser = invitingUser;
    }

    public Advert getInvitingAdvert() {
        return invitingAdvert;
    }

    public void setInvitingAdvert(Advert invitingAdvert) {
        this.invitingAdvert = invitingAdvert;
    }

    public User getReceivingUser() {
        return receivingUser;
    }

    public void setReceivingUser(User receivingUser) {
        this.receivingUser = receivingUser;
    }

    public Advert getReceivingAdvert() {
        return receivingAdvert;
    }

    public void setReceivingAdvert(Advert receivingAdvert) {
        this.receivingAdvert = receivingAdvert;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public AdvertConnection withInvitingUser(User invitingUser) {
        this.invitingUser = invitingUser;
        return this;
    }

    public AdvertConnection withInvitingAdvert(Advert invitingAdvert) {
        this.invitingAdvert = invitingAdvert;
        return this;
    }

    public AdvertConnection withReceivingUser(User receivingUser) {
        this.receivingUser = receivingUser;
        return this;
    }

    public AdvertConnection withReceivingAdvert(Advert receivingAdvert) {
        this.receivingAdvert = receivingAdvert;
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
        return new EntitySignature().addProperty("invitingUser", invitingUser).addProperty("invitingAdvert", invitingAdvert).addProperty("receivingUser", receivingUser)
                .addProperty("receivingAdvert", receivingAdvert);
    }

}
