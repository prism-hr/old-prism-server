package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class WorkflowResource implements IUniqueResource {
    
    public abstract Object getId();
    
    public abstract void setId(Object id);
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("id", getId());
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
