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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_PROCESSING", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "state_group_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationProcessing implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "state_group_id", nullable = false)
    private StateGroup stateGroup;

    @Column(name = "instance_count", nullable = false)
    private Integer instanceCount;

    @Column(name = "day_duration_average")
    private BigDecimal dayDurationAverage;

    @Column(name = "last_updated_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastUpdatedDate;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
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

    public final BigDecimal getDayDurationAverage() {
        return dayDurationAverage;
    }

    public final void setDayDurationAverage(BigDecimal dayDurationSum) {
        this.dayDurationAverage = dayDurationSum;
    }

    public final LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public final void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public ApplicationProcessing withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationProcessing withStateGroup(StateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

    public ApplicationProcessing withInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
        return this;
    }

    public ApplicationProcessing withDayDurationAverage(BigDecimal dayDurationAverage) {
        this.dayDurationAverage = dayDurationAverage;
        return this;
    }

    public ApplicationProcessing withLastUpdateDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("application", application).addProperty("stateGroup", stateGroup);
    }

}
