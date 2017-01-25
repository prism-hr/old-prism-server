package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.resource.Project;

import javax.persistence.*;

@Entity
@Table(name = "user_project_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id", "action_id"}))
public class UserProjectAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("project", project);
    }
    
}
