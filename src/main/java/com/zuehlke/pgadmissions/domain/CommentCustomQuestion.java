package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "program", "action_id" }) })
public class CommentCustomQuestion implements IUniqueResource {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "comment_custom_question_version_id", nullable = true)
    private CustomQuestionVersion version;
    
    @Column(name = "is_enabled")
    private boolean enabled;

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
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CommentCustomQuestion withProgram(Program program) {
        this.program = program;
        return this;
    }
    
    public CommentCustomQuestion withAction(Action action) {
        this.action = action;
        return this;
    }
    
    public CommentCustomQuestion withVersion(CustomQuestionVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("program", program);
        properties1.put("action", action);
        propertiesWrapper.add(properties1);
        return new ResourceSignature(propertiesWrapper);
    }

}
