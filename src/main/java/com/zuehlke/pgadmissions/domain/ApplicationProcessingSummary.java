package com.zuehlke.pgadmissions.domain;

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
    
    @Column(name = "instance_total", nullable = false)
    private Integer instanceTotal;
    
    @Column(name = "instance_total_live", nullable = false)
    private Integer instanceTotalLive;
    
    @Column(name = "instance_count_percentile_05", nullable = false)
    private Integer instanceCountCount05;
    
    @Column(name = "instance_count_percentile_20", nullable = false)
    private Integer instanceCountCount20;

    @Column(name = "instance_count_percentile_35", nullable = false)
    private Integer instanceCountCount35;
    
    @Column(name = "instance_count_percentile_50", nullable = false)
    private Integer instanceCountCount50;
    
    @Column(name = "instance_count_percentile_65", nullable = false)
    private Integer instanceCountCount65;
    
    @Column(name = "instance_count_percentile_80", nullable = false)
    private Integer instanceCountCount80;
    
    @Column(name = "instance_count_percentile_95", nullable = false)
    private Integer instanceCountCount95;
    
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

    public final Integer getInstanceTotal() {
        return instanceTotal;
    }

    public final void setInstanceTotal(Integer instanceTotal) {
        this.instanceTotal = instanceTotal;
    }

    public final Integer getInstanceTotalLive() {
        return instanceTotalLive;
    }

    public final void setInstanceTotalLive(Integer instanceTotalLive) {
        this.instanceTotalLive = instanceTotalLive;
    }

    public final Integer getInstanceCountCount05() {
        return instanceCountCount05;
    }

    public final void setInstanceCountCount05(Integer instanceCountCount05) {
        this.instanceCountCount05 = instanceCountCount05;
    }

    public final Integer getInstanceCountCount20() {
        return instanceCountCount20;
    }

    public final void setInstanceCountCount20(Integer instanceCountCount20) {
        this.instanceCountCount20 = instanceCountCount20;
    }

    public final Integer getInstanceCountCount35() {
        return instanceCountCount35;
    }

    public final void setInstanceCountCount35(Integer instanceCountCount35) {
        this.instanceCountCount35 = instanceCountCount35;
    }

    public final Integer getInstanceCountCount50() {
        return instanceCountCount50;
    }

    public final void setInstanceCountCount50(Integer instanceCountCount50) {
        this.instanceCountCount50 = instanceCountCount50;
    }

    public final Integer getInstanceCountCount65() {
        return instanceCountCount65;
    }

    public final void setInstanceCountCount65(Integer instanceCountCount65) {
        this.instanceCountCount65 = instanceCountCount65;
    }

    public final Integer getInstanceCountCount80() {
        return instanceCountCount80;
    }

    public final void setInstanceCountCount80(Integer instanceCountCount80) {
        this.instanceCountCount80 = instanceCountCount80;
    }

    public final Integer getInstanceCountCount95() {
        return instanceCountCount95;
    }

    public final void setInstanceCountCount95(Integer instanceCountCount95) {
        this.instanceCountCount95 = instanceCountCount95;
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
