package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;

public class UserActivityRepresentation {

    private List<ResourceActivityRepresentation> resourceActivities;

    private List<AppointmentActivityRepresentation> appointmentActivities;

    private List<ConnectionActivityRepresentation> connectionActivities;

    public List<ResourceActivityRepresentation> getResourceActivities() {
        return resourceActivities;
    }

    public void setResourceActivities(List<ResourceActivityRepresentation> resourceActivities) {
        this.resourceActivities = resourceActivities;
    }

    public List<AppointmentActivityRepresentation> getAppointmentActivities() {
        return appointmentActivities;
    }

    public void setAppointmentActivities(List<AppointmentActivityRepresentation> appointmentActivities) {
        this.appointmentActivities = appointmentActivities;
    }

    public List<ConnectionActivityRepresentation> getConnectionActivities() {
        return connectionActivities;
    }

    public void setConnectionActivities(List<ConnectionActivityRepresentation> connectionActivities) {
        this.connectionActivities = connectionActivities;
    }

    public UserActivityRepresentation withResourceActivities(List<ResourceActivityRepresentation> resourceActivities) {
        this.resourceActivities = resourceActivities;
        return this;
    }

    public UserActivityRepresentation withAppointmentActivities(List<AppointmentActivityRepresentation> appointmentActivities) {
        this.appointmentActivities = appointmentActivities;
        return this;
    }

    public UserActivityRepresentation withConnectionActivities(List<ConnectionActivityRepresentation> connectionActivities) {
        this.connectionActivities = connectionActivities;
        return this;
    }

    public static class ResourceActivityRepresentation {

        private PrismScope scope;

        private Integer updateCount;

        private List<ActionActivityRepresentation> actions;

        public PrismScope getScope() {
            return scope;
        }

        public void setScope(PrismScope scope) {
            this.scope = scope;
        }

        public Integer getUpdateCount() {
            return updateCount;
        }

        public void setUpdateCount(Integer updateCount) {
            this.updateCount = updateCount;
        }

        public List<ActionActivityRepresentation> getActions() {
            return actions;
        }

        public void setActions(List<ActionActivityRepresentation> actions) {
            this.actions = actions;
        }

        public static class ActionActivityRepresentation {

            private ActionRepresentation action;

            private Integer urgentCount;

            public ActionRepresentation getAction() {
                return action;
            }

            public void setAction(ActionRepresentation action) {
                this.action = action;
            }

            public Integer getUrgentCount() {
                return urgentCount;
            }

            public void setUrgentCount(Integer urgentCount) {
                this.urgentCount = urgentCount;
            }

            public ActionActivityRepresentation withAction(ActionRepresentation action) {
                this.action = action;
                return this;
            }

            public ActionActivityRepresentation withUrgentCount(Integer urgentCount) {
                this.urgentCount = urgentCount;
                return this;
            }

        }

        public ResourceActivityRepresentation withScope(PrismScope scope) {
            this.scope = scope;
            return this;
        }

        public ResourceActivityRepresentation withUpdateCount(Integer updateCount) {
            this.updateCount = updateCount;
            return this;
        }

        public ResourceActivityRepresentation withActions(List<ActionActivityRepresentation> actions) {
            this.actions = actions;
            return this;
        }

    }

    public static class AppointmentActivityRepresentation {

        private ResourceRepresentationActivity resource;

        private CommentInterviewAppointmentRepresentation appointment;

        private CommentInterviewInstructionRepresentation instruction;

        public ResourceRepresentationActivity getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
        }

        public CommentInterviewAppointmentRepresentation getAppointment() {
            return appointment;
        }

        public void setAppointment(CommentInterviewAppointmentRepresentation appointment) {
            this.appointment = appointment;
        }

        public CommentInterviewInstructionRepresentation getInstruction() {
            return instruction;
        }

        public void setInstruction(CommentInterviewInstructionRepresentation instruction) {
            this.instruction = instruction;
        }

        public AppointmentActivityRepresentation withResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
            return this;
        }

        public AppointmentActivityRepresentation withAppointment(CommentInterviewAppointmentRepresentation appointment) {
            this.appointment = appointment;
            return this;
        }

        public AppointmentActivityRepresentation withInstruction(CommentInterviewInstructionRepresentation instruction) {
            this.instruction = instruction;
            return this;
        }

    }

    public static class ConnectionActivityRepresentation {

        private ResourceRepresentationActivity resource;

        private List<UserRepresentationSimple> users;

        public ResourceRepresentationActivity getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
        }

        public List<UserRepresentationSimple> getUsers() {
            return users;
        }

        public void setUsers(List<UserRepresentationSimple> users) {
            this.users = users;
        }

        public ConnectionActivityRepresentation withResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
            return this;
        }

        public ConnectionActivityRepresentation withUsers(List<UserRepresentationSimple> users) {
            this.users = users;
            return this;
        }

    }

}