package com.zuehlke.pgadmissions.domain.comment;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceConfiguration;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "program_type", "locale", "action_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "program_type", "action_id" }), @UniqueConstraint(columnNames = { "program_id", "action_id" }) })
public class CommentCustomQuestion extends WorkflowResourceConfiguration {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_custom_question_id", nullable = false)
    @MapKeyColumn(name = "name", nullable = false)
    private Map<String, CommentCustomQuestionVersion> commentCustomQuestionVersions = Maps.newHashMap();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
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
    public final PrismProgramType getProgramType() {
        return programType;
    }

    @Override
    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
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
    public final Institution getInstitution() {
        return institution;
    }

    @Override
    public final void setInstitution(Institution institution) {
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public final Boolean getSystemDefault() {
        return systemDefault;
    }

    @Override
    public final void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public CommentCustomQuestion withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public CommentCustomQuestion withProgramType(PrismProgramType programType) {
        this.programType = programType;
        return this;
    }

    public CommentCustomQuestion withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public CommentCustomQuestion withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public CommentCustomQuestion withProgram(Program program) {
        this.program = program;
        return this;
    }

    public CommentCustomQuestion withAction(Action action) {
        this.action = action;
        return this;
    }

    public CommentCustomQuestion withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    public CommentCustomQuestion addCommentCustomQuestionVersion(String name, String content) {
        commentCustomQuestionVersions.put(name, new CommentCustomQuestionVersion().withCommentCustomQuestion(this).withName(name).withContent(content));
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("action", action);
    }

}
