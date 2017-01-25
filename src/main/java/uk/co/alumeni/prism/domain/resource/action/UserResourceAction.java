package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;

import javax.persistence.*;

@MappedSuperclass
public class UserResourceAction implements UniqueEntity {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
    
    @Lob
    @Column(name = "action_enhancement")
    private String actionEnhancement;
    
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
    
    public Action getAction() {
        return action;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
    public String getActionEnhancement() {
        return actionEnhancement;
    }
    
    public void setActionEnhancement(String actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", user).addProperty("action", action);
    }
}
