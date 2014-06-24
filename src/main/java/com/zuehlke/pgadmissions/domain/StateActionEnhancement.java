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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismActionEnhancementType;

@Entity
@Table(name = "STATE_ACTION_ENHANCEMENT", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_assignment_id", "action_enhancement_type" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateActionEnhancement implements IUniqueResource {
    
    @Id
    @GeneratedValue
    private Integer Id;
    
    @ManyToOne
    @JoinColumn(name = "state_action_assignment_id", nullable = false)
    private StateActionAssignment stateActionAssignment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_enhancement_type", nullable = false)
    private PrismActionEnhancementType enhancementType;
    
    @ManyToOne
    @JoinColumn(name = "delegated_action_id")
    private Action delegatedAction;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    
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
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public StateActionEnhancement withStateActionAssignment(StateActionAssignment stateActionAssignment) {
        this.stateActionAssignment = stateActionAssignment;
        return this;
    }
    
    public StateActionEnhancement withEnhancementType(PrismActionEnhancementType enhancementType) {
        this.enhancementType = enhancementType;
        return this;
    }
    
    public StateActionEnhancement withDelegatedAction(Action delegatedAction) {
        this.delegatedAction = delegatedAction;
        return this;
    }
    
    public StateActionEnhancement withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("stateActionAssignment", stateActionAssignment);
        properties.put("enhancementType", enhancementType);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
