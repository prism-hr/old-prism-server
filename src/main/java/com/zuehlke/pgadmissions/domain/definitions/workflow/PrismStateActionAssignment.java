package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.collect.Lists;

public class PrismStateActionAssignment {
    
    private PrismRole role;
    
    private List<PrismStateActionEnhancement> enhancements = Lists.newArrayList();
    
    public PrismRole getRole() {
        return role;
    }

    public List<PrismStateActionEnhancement> getEnhancements() {
        return enhancements;
    }
    
    public PrismStateActionAssignment withRole(PrismRole role) {
        this.role = role;
        return this;
    }
    
    public PrismStateActionAssignment withEnhancements(List<PrismStateActionEnhancement> enhancements) {
        this.enhancements = enhancements;
        return this;
    }
    
}
