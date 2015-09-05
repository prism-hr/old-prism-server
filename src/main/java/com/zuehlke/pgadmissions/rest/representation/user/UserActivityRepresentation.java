package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;

public class UserActivityRepresentation {

    private ResourcesActivityRepresentation resourceActivities;

    private List<AppointmentActivityRepresentation> appointmentActivities;

    private List<ConnectionActivityRepresentation> staffConnectionActivities;

    private List<ConnectionActivityRepresentation> partnerConnectionActivities;

    public ResourcesActivityRepresentation getResourceActivities() {
        return resourceActivities;
    }

    public void setResourceActivities(ResourcesActivityRepresentation resourceActivities) {
        this.resourceActivities = resourceActivities;
    }

    public List<AppointmentActivityRepresentation> getAppointmentActivities() {
        return appointmentActivities;
    }

    public void setAppointmentActivities(List<AppointmentActivityRepresentation> appointmentActivities) {
        this.appointmentActivities = appointmentActivities;
    }

    public List<ConnectionActivityRepresentation> getStaffConnectionActivities() {
        return staffConnectionActivities;
    }

    public void setStaffConnectionActivities(List<ConnectionActivityRepresentation> connectionRepresentations) {
        this.staffConnectionActivities = connectionRepresentations;
    }

    public List<ConnectionActivityRepresentation> getPartnerConnectionActivities() {
        return partnerConnectionActivities;
    }

    public void setPartnerConnectionActivities(List<ConnectionActivityRepresentation> partnerConnectionActivities) {
        this.partnerConnectionActivities = partnerConnectionActivities;
    }

    public static class ResourcesActivityRepresentation {

        private List<ResourceActivityRepresentation> scopes;

        public List<ResourceActivityRepresentation> getScopes() {
            return scopes;
        }

        public void setScopes(List<ResourceActivityRepresentation> scopes) {
            this.scopes = scopes;
        }

        public static class ResourceActivityRepresentation {

            private Integer updateCount;

            private List<ActionActivityRepresentation> actions;

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

            }

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

    }

    public static class ConnectionActivityRepresentation {

        private ResourceRepresentationActivity resource;

        private List<UserRepresentationSimple> user;

        public ResourceRepresentationActivity getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
        }

        public List<UserRepresentationSimple> getUser() {
            return user;
        }

        public void setUser(List<UserRepresentationSimple> user) {
            this.user = user;
        }

    }

}
