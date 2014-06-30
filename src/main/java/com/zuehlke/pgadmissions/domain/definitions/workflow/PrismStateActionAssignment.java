package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateActionAssignment {

    private PrismRole role;

    private List<PrismStateActionEnhancement> enhancements = Lists.newArrayList();

    public PrismRole getRole() {
        return role;
    }

    public List<PrismStateActionEnhancement> getEnhancements() {
        return enhancements == null ? new ArrayList<PrismStateActionEnhancement>() : enhancements;
    }

    public PrismStateActionAssignment withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateActionAssignment withEnhancements(List<PrismStateActionEnhancement> enhancements) {
        this.enhancements = enhancements;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, enhancements);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismStateActionAssignment other = (PrismStateActionAssignment) obj;
        final List<PrismStateActionEnhancement> otherEnhancements = other.getEnhancements();
        return Objects.equal(role, other.getRole()) && enhancements.size() == otherEnhancements.size() && enhancements.containsAll(otherEnhancements);
    }

}
