package uk.co.alumeni.prism.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.StateActionPendingReassignmentProcessor;

@Entity
@Table(name = "state_action_pending")
public class StateActionPending extends WorkflowResourceExecution implements UserAssignment<StateActionPendingReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private uk.co.alumeni.prism.domain.resource.System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "transition_state_id")
    private State transitionState;

    @ManyToOne
    @JoinColumn(name = "assign_user_role_id")
    private Role assignUserRole;

    @Lob
    @Column(name = "assign_user_list")
    private String assignUserList;

    @Lob
    @Column(name = "assign_user_message")
    private String assignUserMessage;

    @ManyToOne
    @JoinColumn(name = "template_comment_id")
    private Comment comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public Role getAssignUserRole() {
        return assignUserRole;
    }

    public void setAssignUserRole(Role assignUserRole) {
        this.assignUserRole = assignUserRole;
    }

    public String getAssignUserList() {
        return assignUserList;
    }

    public void setAssignUserList(String assignUserList) {
        this.assignUserList = assignUserList;
    }

    public String getAssignUserMessage() {
        return assignUserMessage;
    }

    public void setAssignUserMessage(String assignUserMessage) {
        this.assignUserMessage = assignUserMessage;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public StateActionPending withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public StateActionPending withUser(User user) {
        this.user = user;
        return this;
    }

    public StateActionPending withAction(Action action) {
        this.action = action;
        return this;
    }

    public StateActionPending withTransitionState(State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public StateActionPending withAssignUserRole(Role assignUserRole) {
        this.assignUserRole = assignUserRole;
        return this;
    }

    public StateActionPending withAssignUserList(String assignUserList) {
        this.assignUserList = assignUserList;
        return this;
    }

    public StateActionPending withAssignUserMessage(String assignUserMessage) {
        this.assignUserMessage = assignUserMessage;
        return this;
    }

    public StateActionPending withTemplateComment(Comment comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public Class<StateActionPendingReassignmentProcessor> getUserReassignmentProcessor() {
        return StateActionPendingReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}
