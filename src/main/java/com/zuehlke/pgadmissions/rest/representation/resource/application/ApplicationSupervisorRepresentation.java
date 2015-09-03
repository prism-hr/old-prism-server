package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationSupervisorRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private Boolean acceptedSupervision;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public Boolean getAcceptedSupervision() {
        return acceptedSupervision;
    }

    public void setAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
    }
        
    public ApplicationSupervisorRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationSupervisorRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }
    
    public ApplicationSupervisorRepresentation withAcceptedSupervisor(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
        return this;
    }
    
    public ApplicationSupervisorRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    
    
}
