package com.zuehlke.pgadmissions.rest.converter;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;

public class AdvertCategoryConverter extends DozerConverter<AdvertFilterCategory, Object> {

    public AdvertCategoryConverter() {
        super(AdvertFilterCategory.class, Object.class);
    }

    @Override
    public Object convertTo(AdvertFilterCategory source, Object destination) {
        Object value = source.getValue();
        if(value instanceof String || value instanceof Enum) {
            return value;
        } else {
            try {
                return PropertyUtils.getSimpleProperty(value, "id");
            } catch (Exception e){
                throw new Error(e);
            }
        }
    }

    @Override
    public AdvertFilterCategory convertFrom(Object source, AdvertFilterCategory destination) {
        throw new UnsupportedOperationException();
    }
    
}
