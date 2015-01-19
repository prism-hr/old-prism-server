package com.zuehlke.pgadmissions.rest.converter;

import java.util.Collections;
import java.util.List;

import org.dozer.DozerConverter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
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
        if(source == null){
            return null;
        }
        return Joiner.on("|").join(source);
    }

}
