package uk.co.alumeni.prism.domain.user;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.activity.Activity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.WorkflowResourceExecution;
import uk.co.alumeni.prism.workflow.user.UserRoleReassignmentProcessor;

import javax.persistence.*;

@Entity
@Table(name = "user_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"system_id", "user_id", "role_id"}),
        @UniqueConstraint(columnNames = {"institution_id", "user_id", "role_id"}),
        @UniqueConstraint(columnNames = {"department_id", "user_id", "role_id"}), //
        @UniqueConstraint(columnNames = {"program_id", "user_id", "role_id"}), @UniqueConstraint(columnNames = {"project_id", "user_id", "role_id"}), //
        @UniqueConstraint(columnNames = {"application_id", "user_id", "role_id"})})
public class UserRole extends WorkflowResourceExecution implements Activity, UserAssignment<UserRoleReassignmentProcessor>, InvitationEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "requested")
    private Boolean requested;

    @ManyToOne
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Column(name = "assigned_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime assignedTimestamp;

    @Column(name = "accepted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime acceptedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getRequested() {
        return requested;
    }

    public void setRequested(Boolean requested) {
        this.requested = requested;
    }

    @Override
    public Invitation getInvitation() {
        return invitation;
    }

    @Override
    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public DateTime getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public DateTime getAcceptedTimestamp() {
        return acceptedTimestamp;
    }

    public void setAcceptedTimestamp(DateTime acceptedTimestamp) {
        this.acceptedTimestamp = acceptedTimestamp;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public UserRole withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public UserRole withUser(User user) {
        this.user = user;
        return this;
    }

    public UserRole withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserRole withRequested(Boolean requested) {
        this.requested = requested;
        return this;
    }

    public UserRole withInvitation(Invitation invitation) {
        this.invitation = invitation;
        return this;
    }

    public UserRole withAssignedTimestamp(DateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
        return this;
    }

    @Override
    public Class<UserRoleReassignmentProcessor> getUserReassignmentProcessor() {
        return UserRoleReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("user", user).addProperty("role", role);
    }

}
