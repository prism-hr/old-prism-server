package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "ACTION_CONFIGURATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "program_type", "locale", "action_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "action_id" }), @UniqueConstraint(columnNames = { "program_id", "action_id" }) })
public class ActionConfiguration extends WorkflowResourceConfiguration {

    @Id
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

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;
    
    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;
    
    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "start_state_group_id", nullable = false)
    private StateGroup startStateGroup;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    public final System getSystem() {
        return system;
    }

    @Override
    public final void setSystem(System system) {
        this.system = system;
    }

    @Override
    public final Institution getInstitution() {
        return institution;
    }

    @Override
    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public final Program getProgram() {
        return program;
    }

    @Override
    public final void setProgram(Program program) {
        this.program = program;
    }
    
    @Override
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }
    
    @Override
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final Action getAction() {
        return action;
    }

    public final void setAction(Action action) {
        this.action = action;
    }

    public final StateGroup getStartStateGroup() {
        return startStateGroup;
    }

    public final void setStartStateGroup(StateGroup startStateGroup) {
        this.startStateGroup = startStateGroup;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public ActionConfiguration withResource(Resource resource) {
        setResource(resource);
        return this;
    }
    
    public ActionConfiguration withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
    public ActionConfiguration withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public ActionConfiguration withAction(Action action) {
        this.action = action;
        return this;
    }

    public ActionConfiguration withStartStateGroup(StateGroup startStateGroup) {
        this.startStateGroup = startStateGroup;
        return this;
    }

    public ActionConfiguration withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("action", action);
    }

}
