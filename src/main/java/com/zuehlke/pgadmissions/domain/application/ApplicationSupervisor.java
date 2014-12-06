package com.zuehlke.pgadmissions.domain.application;

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

import com.zuehlke.pgadmissions.domain.user.User;

@Entity
@Table(name = "APPLICATION_SUPERVISOR", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "user_id" }) })
public class ApplicationSupervisor extends ApplicationSection {

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
    private Boolean acceptedSupervision = false;

    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

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

    public Boolean getAcceptedSupervision() {
        return acceptedSupervision;
    }

    public void setAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
    }

    @Override
    public DateTime getLastEditedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastEditedTimestamp(DateTime lastEditedTimestamp) {
        this.lastUpdatedTimestamp = lastEditedTimestamp;
    }

    public ApplicationSupervisor withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationSupervisor withUser(User user) {
        this.user = user;
        return this;
    }

    public ApplicationSupervisor withAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
        return this;
    }

}
