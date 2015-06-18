package com.zuehlke.pgadmissions.rest.converter;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;

public class AdvertCategoryConverter extends DozerConverter<AdvertAttribute, Object> {

    public AdvertCategoryConverter() {
        super(AdvertAttribute.class, Object.class);
    }

    @Override
    public Object convertTo(AdvertAttribute source, Object destination) {
        Object value = source.getValue();
        if (value instanceof String || value instanceof Enum || value instanceof LocalDate) {
            return value;
        } else {
            try {
                return PropertyUtils.getSimpleProperty(value, "id");
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    @Override
    public AdvertAttribute convertFrom(Object source, AdvertAttribute destination) {
        throw new UnsupportedOperationException();
    }

}
