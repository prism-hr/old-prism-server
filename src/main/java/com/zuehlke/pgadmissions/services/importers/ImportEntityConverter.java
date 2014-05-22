package com.zuehlke.pgadmissions.services.importers;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;

import com.google.common.base.Function;
import com.zuehlke.pgadmissions.domain.ImportedEntity;

public class ImportEntityConverter<E extends ImportedEntity> implements Function<Object, E> {

    private Class<E> importedEntityType;

    private ImportEntityConverter(Class<E> importedEntityType) {
        this.importedEntityType = importedEntityType;
    }

    public static <E extends ImportedEntity> ImportEntityConverter<E> create(Class<E> importedEntityType) {
        return new ImportEntityConverter<E>(importedEntityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E apply(Object input) {
        try {
            E importedEntity = (E) ConstructorUtils.invokeConstructor(importedEntityType, null);
            String name = BeanUtils.getSimpleProperty(input, "name");
            String code = BeanUtils.getSimpleProperty(input, "code");
            importedEntity.setName(name);
            importedEntity.setCode(code);
            importedEntity.setEnabled(true);
            return importedEntity;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}