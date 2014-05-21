package com.zuehlke.pgadmissions.services;

import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.dao.PrismResourceDAO;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;

@Service
@Transactional
public abstract class PrismResourceService {

    @Autowired
    private EntityDAO entityDAO;
    
    @Autowired
    private PrismResourceDAO prismResourceDAO;
    
    public PrismResource getOrCreate(User creator, PrismResource parentResource, PrismResourceType type, Entry<String, Object>... uniqueConstraints)
            throws Exception {
        Class<? extends PrismResource> childResourceType = (Class<? extends PrismResource>) getResourceType(type.toString());
        PrismResource childResource = prismResourceDAO.getDuplicateResource(childResourceType, creator, parentResource, uniqueConstraints);

        if (childResource == null) {
            try {
                childResource = (PrismResource) ConstructorUtils.invokeConstructor(childResourceType, null);
                childResource.setUser(creator);
                childResource.setParentResource(parentResource);
                
                for (Entry<String, Object> uniqueConstraint : uniqueConstraints) {
                    BeanUtils.setProperty(childResource, uniqueConstraint.getKey(), uniqueConstraint.getValue());
                }
              
                save(childResource);
            } catch (Exception e) {
                throw new Error("Could not create new prism resource of type: " + childResourceType.getSimpleName(), e);
            }
            
        }

        return childResource;
    }

    protected void save(PrismResource resource) {
        entityDAO.save(resource);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PrismResource> getResourceType(String resourceType) {
        try {
            return (Class<? extends PrismResource>) Class.forName("com.zuehlke.pgadmissions.domain." + resourceType.toLowerCase());
        } catch (ClassNotFoundException e) {
            throw new Error("Resource not an identifiable prism resource type", e);
        }
    }

}
