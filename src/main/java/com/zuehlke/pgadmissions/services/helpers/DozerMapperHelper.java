package com.zuehlke.pgadmissions.services.helpers;

import com.google.common.base.Function;
import org.dozer.Mapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class DozerMapperHelper {

    @Inject
    private Mapper mapper;

    public <F, T> DozerMapperFunction<F, T> createFunction(Class<T> targetClass) {
        return new DozerMapperFunction<F, T>(targetClass);
    }

    public class DozerMapperFunction<F, T> implements Function<F, T> {

        private Class<T> targetClass;

        public DozerMapperFunction(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public T apply(F input) {
            return mapper.map(input, targetClass);
        }
    }

}
