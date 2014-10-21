package com.zuehlke.pgadmissions.rest.converter;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeparatedStringToListConverter extends DozerConverter<String, List> {

    public SeparatedStringToListConverter() {
        super(String.class, List.class);
    }

    @Override
    public List convertTo(String source, List destination) {
        if(source == null){
            return Collections.emptyList();
        }
        return Lists.newArrayList(Splitter.on('|').split(source));
    }

    @Override
    public String convertFrom(List source, String destination) {
        throw new UnsupportedOperationException();
    }
}
