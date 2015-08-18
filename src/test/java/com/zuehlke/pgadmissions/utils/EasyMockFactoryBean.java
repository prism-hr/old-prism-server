package com.zuehlke.pgadmissions.utils;

import org.easymock.EasyMock;
import org.springframework.beans.factory.FactoryBean;

public class EasyMockFactoryBean<T> implements FactoryBean<T> {

    private Class<T> classToBeMocked;

    public EasyMockFactoryBean(Class<T> classToBeMocked) {
        this.classToBeMocked = classToBeMocked;
    }

    @Override
    public T getObject() throws Exception {
        return EasyMock.createMock(classToBeMocked);
    }

    @Override
    public Class<?> getObjectType() {
        return classToBeMocked;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
