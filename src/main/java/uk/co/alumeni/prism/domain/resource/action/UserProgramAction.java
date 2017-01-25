package uk.co.alumeni.prism.domain.resource.action;

import uk.co.alumeni.prism.domain.resource.Program;

import javax.persistence.*;

@Entity
@Table(name = "user_program_action", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "program_id", "action_id"}))
public class UserProgramAction extends UserResourceAction {
    
    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
    
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("program", program);
    }
    
}
