package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismConfiguration;

@Entity
@Table(name = "WORKFLOW_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "configuration_parameter" }),
        @UniqueConstraint(columnNames = { "institution_id", "configuration_parameter" }),
        @UniqueConstraint(columnNames = { "program_id", "configuration_parameter" }) })
public class WorkflowConfiguration extends WorkflowResourceConfiguration {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(name = "configuration_parameter", nullable = false)
    private PrismConfiguration parameter;

    @Column(name = "minimum_required")
    private Integer minimumRequired;
    
    @Column(name = "maximum_required")
    private Integer maximumRequired;

    public Integer getId() {
        return id;
    }

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
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    public PrismConfiguration getParameter() {
        return parameter;
    }

    public void setParameter(PrismConfiguration parameter) {
        this.parameter = parameter;
    }
    
    public final Integer getMinimumRequired() {
        return minimumRequired;
    }

    public final void setMinimumRequired(Integer minimumRequired) {
        this.minimumRequired = minimumRequired;
    }

    public final Integer getMaximumRequired() {
        return maximumRequired;
    }

    public final void setMaximumRequired(Integer maximumRequired) {
        this.maximumRequired = maximumRequired;
    }

    public WorkflowConfiguration withSystem(System system) {
        this.system = system;
        return this;
    }
    
    public WorkflowConfiguration withParameter(PrismConfiguration parameter) {
        this.parameter = parameter;
        return this;
    }
    
    public WorkflowConfiguration withMinimumRequired(Integer minimumRequired) {
        this.minimumRequired = minimumRequired;
        return this;
    }
    
    public WorkflowConfiguration withMaximumRequired(Integer maximumRequired) {
        this.maximumRequired = maximumRequired;
        return this;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        if (system != null) {
            properties.put("system", system);
        } else if (institution != null) {
            properties.put("institution", institution);
        } else if (program != null) {
            properties.put("program", program);
        }
        properties.put("parameter", parameter);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}