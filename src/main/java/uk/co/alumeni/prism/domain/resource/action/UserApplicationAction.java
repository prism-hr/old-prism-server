package uk.co.alumeni.prism.domain.resource.action;

import javax.persistence.*;
import javax.ws.rs.core.Application;

@Entity
@Table(name = "user_application_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "application_id", "action_id"}))
public class UserApplicationAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
    
    public Application getApplication() {
        return application;
    }
    
    public void setApplication(Application application) {
        this.application = application;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("application", application);
    }
    
}
