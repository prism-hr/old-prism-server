package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class GeocodableLocation implements IUniqueEntity {

    public abstract Object getId();

    public abstract GeographicLocation getLocation();

    public abstract void setLocation(GeographicLocation location);

    public abstract String getLocationString();

    public boolean isGeocoded() {
        return getLocation() != null;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("id", getId());
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
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

}
