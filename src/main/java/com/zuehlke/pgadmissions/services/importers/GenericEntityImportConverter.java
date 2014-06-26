package com.zuehlke.pgadmissions.services.importers;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;

import com.google.common.base.Function;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.Institution;

public class GenericEntityImportConverter<E extends ImportedEntity> implements Function<Object, E> {

    private Class<E> importedEntityType;
    
    private Institution institution;

    protected GenericEntityImportConverter(Class<E> importedEntityType, Institution institution) {
        this.importedEntityType = importedEntityType;
        this.institution = institution;
    }

    public static <E extends ImportedEntity> GenericEntityImportConverter<E> create(Class<E> importedEntityType, Institution institution) {
        return new GenericEntityImportConverter<E>(importedEntityType, institution);
    }
    
    @Override
    public E apply(Object input) {
        try {
            E importedEntity = (E) ConstructorUtils.invokeConstructor(importedEntityType, null);
            String name = BeanUtils.getSimpleProperty(input, "name");
            String code = BeanUtils.getSimpleProperty(input, "code");
            importedEntity.setName(name);
            importedEntity.setCode(code);
            importedEntity.setEnabled(true);
            importedEntity.setInstitution(institution);
            setCustomProperties(input, importedEntity);
            return importedEntity;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    protected void setCustomProperties(Object input, E result) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception {
    }

}