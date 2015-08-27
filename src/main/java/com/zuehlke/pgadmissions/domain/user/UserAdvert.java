package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.workflow.user.UserAdvertReassignmentProcessor;

@Entity
@Table(name = "user_advert", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "advert_id" }) })
public class UserAdvert implements UniqueEntity, UserAssignment<UserAdvertReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public UserAdvert withUser(User user) {
        this.user = user;
        return this;
    }

    public UserAdvert withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    @Override
    public Class<UserAdvertReassignmentProcessor> getUserReassignmentProcessor() {
        return UserAdvertReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", user).addProperty("advert", advert);
    }

}
