package com.zuehlke.pgadmissions.domain.location;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.UniqueEntity;

public abstract class GeocodableLocation implements UniqueEntity {

    public abstract Object getId();

    public abstract GeographicLocation getLocation();

    public abstract void setLocation(GeographicLocation location);

    public abstract String getLocationString();

    public boolean isGeocoded() {
        return getLocation() != null;
    }

    protected String buildLocationString(String... tokens) {
        return Joiner.on(", ").join(filterLocationTokens(tokens));
    }

    protected List<String> filterLocationTokens(String... tokens) {
        List<String> filteredTokens = Lists.newLinkedList();
        for (String token : tokens) {
            if (token != null) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", getId());
    }

}
