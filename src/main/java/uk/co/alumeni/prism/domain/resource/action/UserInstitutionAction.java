package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.resource.Institution;

import javax.persistence.*;

@Entity
@Table(name = "user_institution_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "institution_id", "action_id"}))
public class UserInstitutionAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    public Institution getInstitution() {
        return institution;
    }
    
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("institution", institution);
    }
    
}
