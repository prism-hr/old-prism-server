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

import com.zuehlke.pgadmissions.domain.enums.PrismActionEnhancementType;

@Entity
@Table(name = "STATE_ACTION_ENHANCEMENT", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_assignment_id", "action_enhancement_type_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateActionEnhancement {
    
    @Id
    @GeneratedValue
    private Integer Id;
    
    @ManyToOne
    @JoinColumn(name = "state_action_assignment_id", nullable = false)
    private StateActionAssignment stateActionAssignment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_enhancement_type_id", nullable = false)
    private PrismActionEnhancementType enhancementType;
    
    @ManyToOne
    @JoinColumn(name = "delegated_action_id")
    private Action delegatedAction;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public StateActionAssignment getStateActionAssignment() {
        return stateActionAssignment;
    }

    public void setStateActionAssignment(StateActionAssignment stateActionAssignment) {
        this.stateActionAssignment = stateActionAssignment;
    }

    public PrismActionEnhancementType getEnhancementType() {
        return enhancementType;
    }

    public void setEnhancementType(PrismActionEnhancementType enhancementType) {
        this.enhancementType = enhancementType;
    }

    public Action getDelegatedAction() {
        return delegatedAction;
    }

    public void setDelegatedAction(Action delegatedAction) {
        this.delegatedAction = delegatedAction;
    }
    
}
