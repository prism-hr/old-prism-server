package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.resource.System;

import javax.persistence.*;

@Entity
@Table(name = "user_system_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "system_id", "action_id"}))
public class UserSystemAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;
    
    public System getSystem() {
        return system;
    }
    
    public void setSystem(System system) {
        this.system = system;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("system", system);
    }
    
}
