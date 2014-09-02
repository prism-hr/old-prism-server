package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    
    @Column(name = "instance_sum", nullable = false)
    private Integer instanceSum;
    
    @Column(name = "instance_sum_live", nullable = false)
    private Integer instanceSumLive;
    
    @Column(name = "instance_count_average", nullable = false)
    private BigDecimal instanceCountAverage;
    
    @Column(name = "instance_count_percentile_05", nullable = false)
    private Integer instanceCount05;
    
    @Column(name = "instance_count_percentile_20", nullable = false)
    private Integer instanceCount20;

    @Column(name = "instance_count_percentile_35", nullable = false)
    private Integer instanceCount35;
    
    @Column(name = "instance_count_percentile_50", nullable = false)
    private Integer instanceCount50;
    
    @Column(name = "instance_count_percentile_65", nullable = false)
    private Integer instanceCount65;
    
    @Column(name = "instance_count_percentile_80", nullable = false)
    private Integer instanceCount80;
    
    @Column(name = "instance_count_percentile_95", nullable = false)
    private Integer instanceCount95;
    
    @Column(name = "day_duration_sum_average", nullable = false)
    private BigDecimal dayDurationSumAverage;
    
    @Column(name = "day_duration_sum_percentile_05", nullable = false)
    private Integer dayDurationSum05;
    
    @Column(name = "day_duration_sum_percentile_20", nullable = false)
    private Integer dayDurationSum20;

    @Column(name = "day_duration_sum_percentile_35", nullable = false)
    private Integer dayDurationSum35;
    
    @Column(name = "day_duration_sum_percentile_50", nullable = false)
    private Integer dayDurationSum50;
    
    @Column(name = "day_duration_sum_percentile_65", nullable = false)
    private Integer dayDurationSum65;
    
    @Column(name = "day_duration_sum_percentile_80", nullable = false)
    private Integer dayDurationSum80;
    
    @Column(name = "day_duration_sum_percentile_95", nullable = false)
    private Integer dayDurationSum95;
    
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

    public final BigDecimal getInstanceCountAverage() {
        return instanceCountAverage;
    }

    public final void setInstanceCountAverage(BigDecimal instanceCountAverage) {
        this.instanceCountAverage = instanceCountAverage;
    }

    public final Integer getInstanceSum() {
        return instanceSum;
    }

    public final void setInstanceSum(Integer instanceSum) {
        this.instanceSum = instanceSum;
    }

    public final Integer getInstanceSumLive() {
        return instanceSumLive;
    }

    public final void setInstanceSumLive(Integer instanceSumLive) {
        this.instanceSumLive = instanceSumLive;
    }

    public final Integer getInstanceCount05() {
        return instanceCount05;
    }

    public final void setInstanceCount05(Integer instanceCount05) {
        this.instanceCount05 = instanceCount05;
    }

    public final Integer getInstanceCount20() {
        return instanceCount20;
    }

    public final void setInstanceCount20(Integer instanceCount20) {
        this.instanceCount20 = instanceCount20;
    }

    public final Integer getInstanceCount35() {
        return instanceCount35;
    }

    public final void setInstanceCount35(Integer instanceCount35) {
        this.instanceCount35 = instanceCount35;
    }

    public final Integer getInstanceCount50() {
        return instanceCount50;
    }

    public final void setInstanceCount50(Integer instanceCount50) {
        this.instanceCount50 = instanceCount50;
    }

    public final Integer getInstanceCount65() {
        return instanceCount65;
    }

    public final void setInstanceCount65(Integer instanceCount65) {
        this.instanceCount65 = instanceCount65;
    }

    public final Integer getInstanceCount80() {
        return instanceCount80;
    }

    public final void setInstanceCount80(Integer instanceCount80) {
        this.instanceCount80 = instanceCount80;
    }

    public final Integer getInstanceCount95() {
        return instanceCount95;
    }

    public final void setInstanceCount95(Integer instanceCount95) {
        this.instanceCount95 = instanceCount95;
    }

    public final BigDecimal getDayDurationSumAverage() {
        return dayDurationSumAverage;
    }

    public final void setDayDurationSumAverage(BigDecimal dayDurationSumAverage) {
        this.dayDurationSumAverage = dayDurationSumAverage;
    }

    public final Integer getDayDurationSum05() {
        return dayDurationSum05;
    }

    public final void setDayDurationSum05(Integer dayDurationSum05) {
        this.dayDurationSum05 = dayDurationSum05;
    }

    public final Integer getDayDurationSum20() {
        return dayDurationSum20;
    }

    public final void setDayDurationSum20(Integer dayDurationSum20) {
        this.dayDurationSum20 = dayDurationSum20;
    }

    public final Integer getDayDurationSum35() {
        return dayDurationSum35;
    }

    public final void setDayDurationSum35(Integer dayDurationSum35) {
        this.dayDurationSum35 = dayDurationSum35;
    }

    public final Integer getDayDurationSum50() {
        return dayDurationSum50;
    }

    public final void setDayDurationSum50(Integer dayDurationSum50) {
        this.dayDurationSum50 = dayDurationSum50;
    }

    public final Integer getDayDurationSum65() {
        return dayDurationSum65;
    }

    public final void setDayDurationSum65(Integer dayDurationSum65) {
        this.dayDurationSum65 = dayDurationSum65;
    }

    public final Integer getDayDurationSum80() {
        return dayDurationSum80;
    }

    public final void setDayDurationSum80(Integer dayDurationSum80) {
        this.dayDurationSum80 = dayDurationSum80;
    }

    public final Integer getDayDurationSum95() {
        return dayDurationSum95;
    }

    public final void setDayDurationSum95(Integer dayDurationSum95) {
        this.dayDurationSum95 = dayDurationSum95;
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
        try {
            PropertyUtils.setSimpleProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public void setPercentileValue(String property, Integer percentile, Object value) {
        try {
            MethodUtils.invokeMethod(this, "set" + WordUtils.capitalize(property) + String.format("%02d", percentile), value);
        } catch (Exception e) {
            throw new Error(e);
        }
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
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        if (institution != null) {
            properties.put("institution", institution);
        } else if (program != null) {
            properties.put("program", program);
        } else if (project != null) {
            properties.put("project", project);
        }
        properties.put("stateGroup", stateGroup);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
