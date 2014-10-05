package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_id", "action_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "action_id" }), @UniqueConstraint(columnNames = { "program_id", "action_id" }) })
public class CommentCustomQuestion extends WorkflowResource {

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

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @OneToOne
    @JoinColumn(name = "comment_custom_question_version_id")
    private CommentCustomQuestionVersion version;

    @Column(name = "is_enabled")
    private Boolean enabled;

    @OneToMany(mappedBy = "commentCustomQuestion")
    private Set<CommentCustomQuestionVersion> versions = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

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

    public CommentCustomQuestionVersion getVersion() {
        return version;
    }

    public void setVersion(CommentCustomQuestionVersion version) {
        this.version = version;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<CommentCustomQuestionVersion> getVersions() {
        return versions;
    }

    public CommentCustomQuestion withSystem(System system) {
        this.system = system;
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

    public CommentCustomQuestion withVersion(CommentCustomQuestionVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("action", action);
    }

}
