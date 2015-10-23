package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message")
    private String message;
    
    @OneToMany(mappedBy = "invitation")
    private Set<UserRole> userRoles;
    
    @OneToMany(mappedBy = "invitation")
    private Set<AdvertTarget> advertTargets;
    
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Set<AdvertTarget> getAdvertTargets() {
        return advertTargets;
    }
    
    public Invitation withUser(User user) {
        this.user = user;
        return this;
    }

    public Invitation withMessage(String message) {
        this.message = message;
        return this;
    }

}
