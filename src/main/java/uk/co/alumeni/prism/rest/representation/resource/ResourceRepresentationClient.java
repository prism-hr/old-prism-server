package uk.co.alumeni.prism.rest.representation.resource;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public interface ResourceRepresentationClient {

    UserRepresentationSimple getUser();

    void setUser(UserRepresentationSimple user);

    DateTime getCreatedTimestamp();

    void setCreatedTimestamp(DateTime createdTimestamp);

}
