package uk.co.alumeni.prism.rest.representation.user;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewInstructionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;

public class UserActivityRepresentation {

    private PrismRoleCategory defaultRoleCategory;

    private List<ResourceActivityRepresentation> resourceActivities;

    private List<AppointmentActivityRepresentation> appointmentActivities;

    private List<ResourceUnverifiedUserRepresentation> unverifiedUserActivities;

    private List<AdvertTargetRepresentation> advertTargetActivities;

    public PrismRoleCategory getDefaultRoleCategory() {
        return defaultRoleCategory;
    }

    public void setDefaultRoleCategory(PrismRoleCategory defaultRoleCategory) {
        this.defaultRoleCategory = defaultRoleCategory;
    }

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

    public UserActivityRepresentation withDefaultRoleCategory(PrismRoleCategory defaultRoleCategory) {
        this.defaultRoleCategory = defaultRoleCategory;
        return this;
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

    public boolean hasNotifiableUpdates() {
        boolean hasResourceActivity = false;
        if (isNotEmpty(resourceActivities)) {
            for (ResourceActivityRepresentation resourceActivity : resourceActivities) {
                if (resourceActivity.getUpdateCount() > 0) {
                    hasResourceActivity = true;
                    break;
                } else {
                    List<ActionActivityRepresentation> actionActivities = resourceActivity.getActions();
                    if (isNotEmpty(actionActivities)) {
                        for (ActionActivityRepresentation actionActivity : actionActivities) {
                            if (actionActivity.getUrgentCount() > 0) {
                                hasResourceActivity = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return hasResourceActivity || isNotEmpty(appointmentActivities) || isNotEmpty(unverifiedUserActivities) || isNotEmpty(advertTargetActivities);
    }

    public boolean hasNotifiableReminders() {
        if (isNotEmpty(resourceActivities)) {
            for (ResourceActivityRepresentation resourceActivity : resourceActivities) {
                List<ActionActivityRepresentation> actionActivities = resourceActivity.getActions();
                if (isNotEmpty(actionActivities)) {
                    for (ActionActivityRepresentation actionActivity : actionActivities) {
                        if (actionActivity.getUrgentCount() > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static class ResourceActivityRepresentation {

        private PrismScope scope;

        private PrismRoleCategory defaultRoleCategory;

        private Boolean resourceCreator;

        private Integer count;

        private Integer updateCount;

        private List<ActionActivityRepresentation> actions;

        public PrismScope getScope() {
            return scope;
        }

        public void setScope(PrismScope scope) {
            this.scope = scope;
        }

        public PrismRoleCategory getDefaultRoleCategory() {
            return defaultRoleCategory;
        }

        public void setDefaultRoleCategory(PrismRoleCategory defaultRoleCategory) {
            this.defaultRoleCategory = defaultRoleCategory;
        }

        public Boolean getResourceCreator() {
            return resourceCreator;
        }

        public void setResourceCreator(Boolean resourceCreator) {
            this.resourceCreator = resourceCreator;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
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

        public ResourceActivityRepresentation withScope(PrismScope scope) {
            this.scope = scope;
            return this;
        }

        public ResourceActivityRepresentation withDefaultRoleCategory(PrismRoleCategory defaultRoleCategory) {
            this.defaultRoleCategory = defaultRoleCategory;
            return this;
        }

        public ResourceActivityRepresentation withResourceCreator(Boolean resourceCreator) {
            this.resourceCreator = resourceCreator;
            return this;
        }

        public ResourceActivityRepresentation withCount(Integer count) {
            this.count = count;
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
