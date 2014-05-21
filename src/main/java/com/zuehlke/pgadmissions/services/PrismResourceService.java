package com.zuehlke.pgadmissions.services;

import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang.StringUtils;
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
    
    private static final String DOMAIN_PACKAGE = "com.zuehlke.pgadmissions.domain";

    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private PrismResourceDAO prismResourceDAO;

    @SuppressWarnings("unchecked")
    public PrismResource getOrCreate(User creator, PrismResource parentResource, PrismResourceType childResourceType,
            Entry<String, Object>... uniqueConstraints) throws Exception {
        String childResourceClassName = DOMAIN_PACKAGE + "." + StringUtils.capitalize(childResourceType.toString().toLowerCase());
        Class<? extends PrismResource> childResourceClass = (Class<? extends PrismResource>) Class.forName(childResourceClassName);
        PrismResource childResource = prismResourceDAO.getDuplicateResource(childResourceClass, creator, parentResource, uniqueConstraints);

        if (childResource == null) {
            try {
                childResource = (PrismResource) ConstructorUtils.invokeConstructor(childResourceClass, null);
                childResource.setParentResource(parentResource);

                for (Entry<String, Object> uniqueConstraint : uniqueConstraints) {
                    BeanUtils.setProperty(childResource, uniqueConstraint.getKey(), uniqueConstraint.getValue());
                }

                save(childResource, creator);
            } catch (Exception e) {
                throw new Error("Could not create new prism resource of type: " + childResourceClass.getSimpleName(), e);
            }

        }

        return childResource;
    }

    protected void save(PrismResource resource, User creator) {
        resource.setUser(creator);
        entityDAO.save(resource);
    }

}
