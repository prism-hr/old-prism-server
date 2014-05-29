package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SUGGESTED_SUPERVISOR")
@Inheritance(strategy = InheritanceType.JOINED)
public class SuggestedSupervisor {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_form_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean aware;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAware() {
        return aware;
    }

    public void setAware(boolean aware) {
        this.aware = aware;
    }

    public SuggestedSupervisor withApplication(Application application) {
        this.application = application;
        return this;
    }

    public SuggestedSupervisor withUser(User user) {
        this.user = user;
        return this;
    }

    public SuggestedSupervisor withAware(boolean aware) {
        this.aware = aware;
        return this;
    }

}
