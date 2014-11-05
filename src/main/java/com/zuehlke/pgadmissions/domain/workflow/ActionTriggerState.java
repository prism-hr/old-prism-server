package com.zuehlke.pgadmissions.domain.workflow;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

@Entity
@Table(name = "ACTION_TRIGGER_STATE", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "system_id", "locale", "program_type", "action_id", "state_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "action_id", "state_id" }),
        @UniqueConstraint(columnNames = { "program_id", "action_id", "state_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActionTriggerState extends WorkflowResourceConfiguration {

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
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final System getSystem() {
        return system;
    }

    public final void setSystem(System system) {
        this.system = system;
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

    public final PrismLocale getLocale() {
        return locale;
    }

    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final PrismProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final Action getAction() {
        return action;
    }

    public final void setAction(Action action) {
        this.action = action;
    }

    public final State getState() {
        return state;
    }

    public final void setState(State state) {
        this.state = state;
    }

    @Override
    public Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public ActionTriggerState withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public ActionTriggerState withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public ActionTriggerState withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public ActionTriggerState withAction(Action action) {
        this.action = action;
        return this;
    }

    public ActionTriggerState withState(State state) {
        this.state = state;
        return this;
    }

    public ActionTriggerState withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("action", action).addProperty("state", state);
    }

}
