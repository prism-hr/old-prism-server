package uk.co.alumeni.prism.domain.resource;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.workflow.State;

import javax.persistence.*;

@Entity
@Table(name = "resource_previous_state", uniqueConstraints = {@UniqueConstraint(columnNames = {"system_id", "state_id"}),
        @UniqueConstraint(columnNames = {"institution_id", "state_id"}), @UniqueConstraint(columnNames = {"department_id", "state_id"}),
        @UniqueConstraint(columnNames = {"program_id", "state_id"}), @UniqueConstraint(columnNames = {"project_id", "state_id"}),
        @UniqueConstraint(columnNames = {"application_id", "state_id"})})
public class ResourcePreviousState extends ResourceStateDefinition {

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
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "primary_state", nullable = false)
    private Boolean primaryState;

    @Column(name = "created_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate createdDate;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public uk.co.alumeni.prism.domain.resource.System getSystem() {
        return system;
    }

    @Override
    public void setSystem(uk.co.alumeni.prism.domain.resource.System system) {
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

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final void setState(State state) {
        this.state = state;
    }

    @Override
    public final Boolean getPrimaryState() {
        return primaryState;
    }

    @Override
    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    @Override
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public ResourcePreviousState withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ResourcePreviousState withState(State state) {
        this.state = state;
        return this;
    }

    public ResourcePreviousState withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

}
