package com.zuehlke.pgadmissions.rest.representation.resource;

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
