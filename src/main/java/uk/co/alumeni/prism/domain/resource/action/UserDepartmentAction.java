package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.resource.Department;

import javax.persistence.*;

@Entity
@Table(name = "user_department_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "department_id", "action_id"}))
public class UserDepartmentAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    public Department getDepartment() {
        return department;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("department", department);
    }
    
}
