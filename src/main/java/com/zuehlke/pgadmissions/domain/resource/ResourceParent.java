package com.zuehlke.pgadmissions.domain.resource;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public abstract class ResourceParent extends Resource {

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract DateTime getUpdatedTimestampSitemap();

    public abstract void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap);

}
