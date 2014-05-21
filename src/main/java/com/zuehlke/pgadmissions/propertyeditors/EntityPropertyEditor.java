package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.services.EntityService;

public class EntityPropertyEditor<E> extends PropertyEditorSupport {

    private Class<E> entityClass;

    @Autowired
    private EntityService entityService;

    public EntityPropertyEditor(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        if (strId == null || StringUtils.isBlank(strId)) {
            setValue(null);
            return;
        }

        setValue(entityService.getById(entityClass, Integer.parseInt(strId)));
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }

        try {
            return BeanUtils.getSimpleProperty(getValue(), "id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
