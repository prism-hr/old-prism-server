package com.zuehlke.pgadmissions.domain;

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

import com.zuehlke.pgadmissions.domain.enums.ActionVisibilityExclusionRule;

@Entity
@Table(name = "ACTION_VISIBILITY_EXCLUSION", uniqueConstraints = { @UniqueConstraint(columnNames = { "action_id", "role_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ActionVisibilityExclusion {

    @Id
    @GeneratedValue
    private Integer id;

    @JoinColumn(name = "action_id", nullable = false)
    @ManyToOne
    private Action action;

    @JoinColumn(name = "role_id", nullable = false)
    @ManyToOne
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_visibility_exclusion_rule_id", nullable = false)
    private ActionVisibilityExclusionRule rule;

    @Column(name = "precedence")
    private Boolean precedence;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ActionVisibilityExclusionRule getRule() {
        return rule;
    }

    public void setRule(ActionVisibilityExclusionRule rule) {
        this.rule = rule;
    }

    public Boolean getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Boolean precedence) {
        this.precedence = precedence;
    }

}
