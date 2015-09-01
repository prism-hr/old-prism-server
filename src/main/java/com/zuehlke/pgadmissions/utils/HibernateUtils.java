package com.zuehlke.pgadmissions.utils;

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
        return Objects.equal(PrismReflectionUtils.getProperty(entity1, "id"), PrismReflectionUtils.getProperty(entity2, "id"));
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
