package com.zuehlke.pgadmissions.services;

import java.util.AbstractMap;
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
import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;

@Service
@Transactional
public abstract class PrismResourceService<R extends PrismResource> {

    private static final String DOMAIN_PACKAGE = "com.zuehlke.pgadmissions.domain";

    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private PrismResourceDAO prismResourceDAO;

    @SuppressWarnings("unchecked")
    public R getOrCreate(PrismResource parentResource, PrismResourceType childResourceType, AbstractMap.SimpleEntry<String, Object>... uniqueConstraints) throws Exception {
        String childResourceClassName = DOMAIN_PACKAGE + "." + StringUtils.capitalize(childResourceType.toString().toLowerCase());
        Class<R> childResourceClass = (Class<R>) Class.forName(childResourceClassName);
        R childResource = (R) prismResourceDAO.getDuplicateResource(childResourceClass, parentResource, uniqueConstraints);

        if (childResource == null) {
            try {
                childResource = (R) ConstructorUtils.invokeConstructor(childResourceClass, null);
                childResource.setParentResource(parentResource);

                for (Entry<String, Object> uniqueConstraint : uniqueConstraints) {
                    BeanUtils.setProperty(childResource, uniqueConstraint.getKey(), uniqueConstraint.getValue());
                }

                save(childResource);
            } catch (Exception e) {
                throw new Error("Could not create new prism resource of type: " + childResourceClass.getSimpleName(), e);
            }

        }

        return childResource;
    }

    public void save(R resource) {
        entityDAO.save(resource);
    }

}
