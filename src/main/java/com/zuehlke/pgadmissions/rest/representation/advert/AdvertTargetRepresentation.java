package com.zuehlke.pgadmissions.rest.representation.advert;

import static org.apache.commons.lang3.ObjectUtils.compare;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class AdvertTargetRepresentation implements Comparable<AdvertTargetRepresentation> {

    private ResourceRepresentationActivity resource;

    private List<AdvertTargetConnectionRepresentation> connections;

    public ResourceRepresentationActivity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
    }

    public List<AdvertTargetConnectionRepresentation> getConnections() {
        return connections;
    }

    public void setConnections(List<AdvertTargetConnectionRepresentation> connections) {
        this.connections = connections;
    }

    public AdvertTargetRepresentation withResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
        return this;
    }

    @Override
    public int compareTo(AdvertTargetRepresentation other) {
        return resource.compareTo(other.getResource());
    }

    public static class AdvertTargetConnectionRepresentation implements Comparable<AdvertTargetConnectionRepresentation> {

        private Integer advertTargetId;

        private ResourceRepresentationActivity resource;

        private UserRepresentationSimple user;

        private Boolean canManage;

        public Integer getAdvertTargetId() {
            return advertTargetId;
        }

        public void setAdvertTargetId(Integer advertTargetId) {
            this.advertTargetId = advertTargetId;
        }

        public ResourceRepresentationActivity getResource() {
            return resource;
        }

        public void setResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
        }

        public UserRepresentationSimple getUser() {
            return user;
        }

        public void setUser(UserRepresentationSimple user) {
            this.user = user;
        }

        public Boolean getCanManage() {
            return canManage;
        }

        public void setCanManage(Boolean canAccept) {
            this.canManage = canAccept;
        }

        public AdvertTargetConnectionRepresentation withAdvertTargetId(Integer advertTargetId) {
            this.advertTargetId = advertTargetId;
            return this;
        }

        public AdvertTargetConnectionRepresentation withResource(ResourceRepresentationActivity resource) {
            this.resource = resource;
            return this;
        }

        public AdvertTargetConnectionRepresentation withUser(UserRepresentationSimple user) {
            this.user = user;
            return this;
        }

        public AdvertTargetConnectionRepresentation withCanManage(Boolean canManage) {
            this.canManage = canManage;
            return this;
        }

        @Override
        public int compareTo(AdvertTargetConnectionRepresentation other) {
            int compare = compare(resource, other.getResource());
            return compare == 0 ? compare(user, other.getUser()) : compare;
        }

    }

}
