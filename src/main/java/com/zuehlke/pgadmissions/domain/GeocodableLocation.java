package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

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
        String locationString = "";
        for (String token : tokens) {
            if (token != null) {
                locationString = locationString + (locationString.equals("") ? locationString : ", ") + token;
            }
        }
        return locationString;
    }
    
}
