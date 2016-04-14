package uk.co.alumeni.prism.rest.representation.resource;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ResourceRepresentationSummary implements ResourceRepresentationClient {

    private UserRepresentationSimple user;

    private DateTime createdTimestamp;

    @Override
    public UserRepresentationSimple getUser() {
        return user;
    }

    @Override
    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

}
