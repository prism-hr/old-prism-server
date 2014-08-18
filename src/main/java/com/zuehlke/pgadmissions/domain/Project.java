package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;

@Entity
@Table(name = "PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends Advert {
    
    @Column(name = "code")
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
    
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title", nullable = false)
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;
    
    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();
    
    @OneToMany(mappedBy = "project")
    private Set<Comment> comments = Sets.newHashSet();
    
    @OneToMany(mappedBy = "project")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
    
    public Set<Application> getApplications() {
        return applications;
    }

    public Set<Comment> getComments() {
        return comments;
    }
    
    public final Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Project withId(Integer id) {
        setId(id);
        return this;
    }
    
    public Project withImmediateStart(boolean immediateStart) {
        setImmediateStart(immediateStart);
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
    public LocalDate getDueDateBaseline() {
        AdvertClosingDate closingDate = getClosingDate();
        if (closingDate != null) {
            return closingDate.getClosingDate();
        }
        return new LocalDate();
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
        exclusions.put("state.id", PrismState.PROJECT_DISABLED_COMPLETED);
        exclusions.put("state.id", PrismState.PROJECT_REJECTED);
        exclusions.put("state.id", PrismState.PROJECT_WITHDRAWN);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

}
