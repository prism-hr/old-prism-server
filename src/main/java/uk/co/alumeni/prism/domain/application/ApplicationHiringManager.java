package uk.co.alumeni.prism.domain.application;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.ApplicationAssignedUserReassignmentProcessor;

import com.google.common.base.Objects;

@Entity
@Table(name = "application_hiring_manager", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "user_id" }) })
public class ApplicationHiringManager implements UniqueEntity, UserAssignment<ApplicationAssignedUserReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationHiringManager withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationHiringManager withUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(application, user);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ApplicationHiringManager other = (ApplicationHiringManager) object;
        return Objects.equal(application, other.getApplication()) && Objects.equal(user, other.getUser());
    }

    @Override
    public Class<ApplicationAssignedUserReassignmentProcessor> getUserReassignmentProcessor() {
        return ApplicationAssignedUserReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return application.getUser().equals(user);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("application", application).addProperty("user", user);
    }

}
