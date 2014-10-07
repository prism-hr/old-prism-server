package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "APPLICATION_PROCESSING_SUMMARY", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "state_group_id" }),
        @UniqueConstraint(columnNames = { "program_id", "state_group_id" }), @UniqueConstraint(columnNames = { "project_id", "state_group_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationProcessingSummary implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "state_group_id", nullable = false)
    private StateGroup stateGroup;

    @Column(name = "instance_count", nullable = false)
    private Integer instanceCount;

    @Column(name = "instance_count_live", nullable = false)
    private Integer instanceCountLive;

    @Column(name = "instance_count_average_non_zero", nullable = false)
    private BigDecimal instanceCountAverageNonZero;

    @Column(name = "day_duration_average")
    private BigDecimal dayDurationAverage;

    public final Integer getId() {
        return Id;
    }

    public final void setId(Integer id) {
        Id = id;
    }

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public final Program getProgram() {
        return program;
    }

    public final void setProgram(Program program) {
        this.program = program;
    }

    public final Project getProject() {
        return project;
    }

    public final void setProject(Project project) {
        this.project = project;
    }

    public final StateGroup getStateGroup() {
        return stateGroup;
    }

    public final void setStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public final Integer getInstanceCount() {
        return instanceCount;
    }

    public final void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

    public final Integer getInstanceCountLive() {
        return instanceCountLive;
    }

    public final void setInstanceCountLive(Integer instanceCountLive) {
        this.instanceCountLive = instanceCountLive;
    }

    public final BigDecimal getInstanceCountAverageNonZero() {
        return instanceCountAverageNonZero;
    }

    public final void setInstanceCountAverageNonZero(BigDecimal instanceCountAverageNonZero) {
        this.instanceCountAverageNonZero = instanceCountAverageNonZero;
    }

    public final BigDecimal getDayDurationAverage() {
        return dayDurationAverage;
    }

    public final void setDayDurationAverage(BigDecimal dayDurationAverage) {
        this.dayDurationAverage = dayDurationAverage;
    }

    public Resource getResource() {
        if (institution != null) {
            return institution;
        } else if (program != null) {
            return program;
        }
        return project;
    }

    public void setResource(Resource resource) {
        this.institution = null;
        this.program = null;
        this.project = null;
        ReflectionUtils.setProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
    }

    public ApplicationProcessingSummary withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ApplicationProcessingSummary withStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        Resource resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCaseName(), resource).addProperty("stateGroup", stateGroup);
    }

}
