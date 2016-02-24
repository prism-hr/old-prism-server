package uk.co.alumeni.prism.rest.representation.resource;

import org.joda.time.DateTime;

public class ResourceRepresentationSitemap extends ResourceRepresentationIdentity {

    public DateTime updatedTimestampSitemap;

    public DateTime getUpdatedTimestampSitemap() {
        return updatedTimestampSitemap;
    }

    public void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap) {
        this.updatedTimestampSitemap = updatedTimestampSitemap;
    }

}
