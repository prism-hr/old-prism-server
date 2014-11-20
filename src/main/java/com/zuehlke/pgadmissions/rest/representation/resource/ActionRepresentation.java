package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ActionRepresentation {

    private PrismAction name;

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    private Set<StateTransitionRepresentation> stateTransitions = Sets.newLinkedHashSet();

    public PrismAction getName() {
        return name;
    }

    public final void setName(PrismAction name) {
        this.name = name;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public final void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public ActionRepresentation withName(PrismAction name) {
        this.name = name;
        return this;
    }

    public ActionRepresentation withRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }

    public ActionRepresentation withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

    public final Set<StateTransitionRepresentation> getStateTransitions() {
        return stateTransitions;
    }

    public void addStateTransition(StateTransitionRepresentation stateTransition) {
        stateTransitions.add(stateTransition);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ActionRepresentation other = (ActionRepresentation) object;
        return Objects.equal(name, other.getName());
    }

    public static class StateTransitionRepresentation {

        private PrismState transitionStateId;

        private Set<RoleTransitionRepresentation> roleTransitions = Sets.newLinkedHashSet();

        public final PrismState getTransitionStateId() {
            return transitionStateId;
        }

        public final Set<RoleTransitionRepresentation> getRoleTransitions() {
            return roleTransitions;
        }

        public ActionRepresentation.StateTransitionRepresentation withTransitionStateId(PrismState transitionStateId) {
            this.transitionStateId = transitionStateId;
            return this;
        }

        public void addRoleTransition(RoleTransitionRepresentation roleTransition) {
            roleTransitions.add(roleTransition);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(transitionStateId);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final StateTransitionRepresentation other = (StateTransitionRepresentation) object;
            return Objects.equal(transitionStateId, other.getTransitionStateId()) && roleTransitions.containsAll(other.getRoleTransitions());
        }

        public static class RoleTransitionRepresentation {

            private PrismRole roleId;

            private PrismRoleTransitionType roleTransitionType;

            private Integer minimumPermitted;

            private Integer maximumPermitted;

            public final PrismRole getRoleId() {
                return roleId;
            }

            public final PrismRoleTransitionType getRoleTransitionType() {
                return roleTransitionType;
            }

            public final Integer getMinimumPermitted() {
                return minimumPermitted;
            }

            public final Integer getMaximumPermitted() {
                return maximumPermitted;
            }

            public RoleTransitionRepresentation withRoleId(PrismRole roleId) {
                this.roleId = roleId;
                return this;
            }

            public RoleTransitionRepresentation withRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
                this.roleTransitionType = roleTransitionType;
                return this;
            }

            public RoleTransitionRepresentation withMinimumPermitted(Integer minimumPermitted) {
                this.minimumPermitted = minimumPermitted;
                return this;
            }

            public RoleTransitionRepresentation withMaximumPermitted(Integer maximumPermitted) {
                this.maximumPermitted = maximumPermitted;
                return this;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(roleId, roleTransitionType);
            }

            @Override
            public boolean equals(Object object) {
                if (object == null) {
                    return false;
                }
                if (getClass() != object.getClass()) {
                    return false;
                }
                final RoleTransitionRepresentation other = (RoleTransitionRepresentation) object;
                return Objects.equal(roleId, other.getRoleId()) && Objects.equal(roleTransitionType, other.getRoleTransitionType());
            }

        }

    }

}
