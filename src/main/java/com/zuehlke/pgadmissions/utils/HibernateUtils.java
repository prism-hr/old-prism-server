package com.zuehlke.pgadmissions.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import com.google.common.base.Objects;

public class HibernateUtils {

    @SuppressWarnings("unchecked")
    public static <T> T unproxy(T entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }

        return entity;
    }

    public static <T> boolean sameEntities(T entity1, T entity2) {
        Object id1 = null;
        Object id2 = null;

        try {
            id1 = PropertyUtils.getSimpleProperty(entity1, "id");
        } catch (Exception e) {
            throw new IllegalStateException("A problem ocurred when getting id of the entity of class: " + entity1.getClass(), e);
        }

        try {
            id2 = PropertyUtils.getSimpleProperty(entity2, "id");
        } catch (Exception e) {
            throw new IllegalStateException("A problem ocurred when getting id of the entity of class: " + entity2.getClass(), e);
        }

        return Objects.equal(id1, id2);
    }

    public static <T> boolean containsEntity(Iterable<T> iterable, T entity) {
        for (T e : iterable) {
            if (sameEntities(e, entity)) {
                return true;
            }
        }
        return false;
    }
}