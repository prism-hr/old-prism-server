package uk.co.alumeni.prism.rest.representation.user;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewInstructionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;

public class UserActivityRepresentation {

    private List<ResourceActivityRepresentation> resourceActivities;

    private List<AppointmentActivityRepresentation> appointmentActivities;

    private List<ResourceUnverifiedUserRepresentation> unverifiedUserActivities;

    private List<AdvertTargetRepresentation> advertTargetActivities;

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

    public List<ResourceUnverifiedUserRepresentation> getUnverifiedUserActivities() {
        return unverifiedUserActivities;
    }

    public void setUnverifiedUserActivities(List<ResourceUnverifiedUserRepresentation> unverifiedUserActivities) {
        this.unverifiedUserActivities = unverifiedUserActivities;
    }

    public List<AdvertTargetRepresentation> getAdvertTargetActivities() {
        return advertTargetActivities;
    }

    public void setAdvertTargetActivities(List<AdvertTargetRepresentation> advertTargetActivities) {
        this.advertTargetActivities = advertTargetActivities;
    }

    public UserActivityRepresentation withResourceActivities(List<ResourceActivityRepresentation> resourceActivities) {
        this.resourceActivities = resourceActivities;
        return this;
    }

    public UserActivityRepresentation withAppointmentActivities(List<AppointmentActivityRepresentation> appointmentActivities) {
        this.appointmentActivities = appointmentActivities;
        return this;
    }

    public UserActivityRepresentation withUnverifiedUserActivities(List<ResourceUnverifiedUserRepresentation> unverifiedUserActivities) {
        this.unverifiedUserActivities = unverifiedUserActivities;
        return this;
    }

    public UserActivityRepresentation withAdvertTargetActivities(List<AdvertTargetRepresentation> advertTargetActivities) {
        this.advertTargetActivities = advertTargetActivities;
        return this;
    }

    public boolean isNotEmpty() {
        return CollectionUtils.isNotEmpty(resourceActivities) || CollectionUtils.isNotEmpty(appointmentActivities) || CollectionUtils.isNotEmpty(unverifiedUserActivities)
                || CollectionUtils.isNotEmpty(advertTargetActivities);
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

        private ResourceRepresentationRelation resource;

        private CommentInterviewAppointmentRepresentation appointment;

        private CommentInterviewInstructionRepresentation instruction;

        public ResourceRepresentationRelation getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationRelation resource) {
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

        public AppointmentActivityRepresentation withResource(ResourceRepresentationRelation resource) {
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

    public static class ResourceUnverifiedUserRepresentation {

        private ResourceRepresentationConnection resource;

        private List<UserRepresentationUnverified> users;

        public ResourceRepresentationConnection getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationConnection resource) {
            this.resource = resource;
        }

        public List<UserRepresentationUnverified> getUsers() {
            return users;
        }

        public void setUsers(List<UserRepresentationUnverified> users) {
            this.users = users;
        }

        public ResourceUnverifiedUserRepresentation withResource(ResourceRepresentationConnection resource) {
            this.resource = resource;
            return this;
        }

        public ResourceUnverifiedUserRepresentation withUsers(List<UserRepresentationUnverified> users) {
            this.users = users;
            return this;
        }

    }

}
