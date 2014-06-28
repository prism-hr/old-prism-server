package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert {

    @Column(name = "code", nullable = true, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;
    
    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;
    
    @OneToMany(mappedBy = "project")
    private Set<Comment> comments = Sets.newHashSet();

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Project withId(Integer id) {
        setId(id);
        return this;
    }

    public Project withInitialData(User user, Program program, String title) {
        setUser(Preconditions.checkNotNull(user));
        this.program = Preconditions.checkNotNull(program);
        this.title = Preconditions.checkNotNull(title);
        this.createdTimestamp = new DateTime();
        return this;
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
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return this;
    }

    @Override
    public void setProject(Project project) {
    }

    @Override
    public State getPreviousState() {
        return previousState;
    }

    @Override
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
    
    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("user", getUser());
        properties.put("program", program);
        properties.put("title", title);
        propertiesWrapper.add(properties);
        HashMultimap<String, Object> exclusions = HashMultimap.create();
        exclusions.put("state.id", PrismState.PROJECT_DISABLED);
        exclusions.put("state.id", PrismState.PROJECT_DISABLED_COMPLETED);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

    @Override
    public String generateCode() {
        return program.getCode() + "-" + createdTimestamp.getYear() + "-" + String.format("%010d", getId());
    }

    @Override
    public LocalDate getDueDateBaseline() {
        AdvertClosingDate closingDate = getClosingDate();
        if (closingDate != null) {
            return closingDate.getClosingDate();
        }
        return new LocalDate();
    }

}
