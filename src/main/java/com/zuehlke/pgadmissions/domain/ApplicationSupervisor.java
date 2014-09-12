package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "APPLICATION_SUPERVISOR", uniqueConstraints = {@UniqueConstraint(columnNames = {"application_id", "user_id"})})
public class ApplicationSupervisor {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "aware_of_application", nullable = false)
    private Boolean aware = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAware() {
        return aware;
    }

    public void setAware(Boolean aware) {
        this.aware = aware;
    }

    public ApplicationSupervisor withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationSupervisor withUser(User user) {
        this.user = user;
        return this;
    }

    public ApplicationSupervisor withAware(boolean aware) {
        this.aware = aware;
        return this;
    }

}
