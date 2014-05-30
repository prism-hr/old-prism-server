package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION")
public class CustomQuestion {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "comment_custom_question_version_id", nullable = false)
    private CustomQuestionVersion version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public CustomQuestionVersion getVersion() {
        return version;
    }

    public void setVersion(CustomQuestionVersion version) {
        this.version = version;
    }

}
