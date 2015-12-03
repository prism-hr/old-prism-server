package uk.co.alumeni.prism.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismConnectionState;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class AdvertTargetRepresentation implements Comparable<AdvertTargetRepresentation> {

    private ResourceRepresentationConnection resource;

    private List<AdvertTargetConnectionRepresentation> connections;

    public ResourceRepresentationConnection getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationConnection resource) {
        this.resource = resource;
    }

    public List<AdvertTargetConnectionRepresentation> getConnections() {
        return connections;
    }

    public void setConnections(List<AdvertTargetConnectionRepresentation> connections) {
        this.connections = connections;
    }

    public AdvertTargetRepresentation withResource(ResourceRepresentationConnection resource) {
        this.resource = resource;
        return this;
    }

    @Override
    public int compareTo(AdvertTargetRepresentation other) {
        return resource.compareTo(other.getResource());
    }

    public static class AdvertTargetConnectionRepresentation implements Comparable<AdvertTargetConnectionRepresentation> {

        private Integer advertTargetId;

        private ResourceRepresentationConnection resource;

        private UserRepresentationSimple user;

        private PrismConnectionState connectState;

        private boolean canManage;

        private boolean severed;

        private boolean selected;

        public Integer getAdvertTargetId() {
            return advertTargetId;
        }

        public void setAdvertTargetId(Integer advertTargetId) {
            this.advertTargetId = advertTargetId;
        }

        public ResourceRepresentationConnection getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationConnection resource) {
            this.resource = resource;
        }

        public UserRepresentationSimple getUser() {
            return user;
        }

        public void setUser(UserRepresentationSimple user) {
            this.user = user;
        }

        public PrismConnectionState getConnectState() {
            return connectState;
        }

        public void setConnectState(PrismConnectionState connectState) {
            this.connectState = connectState;
        }

        public boolean isCanManage() {
            return canManage;
        }

        public void setCanManage(boolean canManage) {
            this.canManage = canManage;
        }

        public boolean isSevered() {
            return severed;
        }

        public void setSevered(boolean severed) {
            this.severed = severed;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public AdvertTargetConnectionRepresentation withAdvertTargetId(Integer advertTargetId) {
            this.advertTargetId = advertTargetId;
            return this;
        }

        public AdvertTargetConnectionRepresentation withResource(ResourceRepresentationConnection resource) {
            this.resource = resource;
            return this;
        }

        public AdvertTargetConnectionRepresentation withCanManage(boolean canManage) {
            this.canManage = canManage;
            return this;
        }

        public AdvertTargetConnectionRepresentation withSevered(boolean severed) {
            this.severed = severed;
            return this;
        }

        public AdvertTargetConnectionRepresentation withSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public AdvertTargetConnectionRepresentation withUser(UserRepresentationSimple user) {
            this.user = user;
            return this;
        }

        @Override
        public int compareTo(AdvertTargetConnectionRepresentation other) {
            int compare = compare(resource, other.getResource());
            return compare == 0 ? compare(user, other.getUser()) : compare;
        }

    }

}
