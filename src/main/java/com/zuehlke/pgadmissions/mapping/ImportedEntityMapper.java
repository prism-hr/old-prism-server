package com.zuehlke.pgadmissions.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAgeRangeResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

@Service
@Transactional
public class ImportedEntityMapper {

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity<?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity) {
        Class<?> entityClass = entity.getClass();
        if (ImportedAgeRange.class.equals(entityClass)) {
            return (U) getImportedAgeRangeRepresentation((ImportedAgeRange) entity);
        } else if (ImportedAdvertDomicile.class.equals(entityClass)) {
            return (U) getImportedAdvertDomicileRepresentation((ImportedAdvertDomicile) entity);
        }

        return (U) getImportedEntitySimpleRepresentation((ImportedEntitySimple) entity, ImportedEntityResponse.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S, T extends ImportedEntity<S>, V extends ImportedEntityResponseDefinition<S>> V getImportedEntitySimpleRepresentation(T entity, Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);
        representation.setId(entity.getId());

        Class<?> entityNameClass = entity.getType().getEntityClassName();
        if (entityNameClass == null) {
            representation.setName(entity.getName());
        } else {
            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
            representation.setName(loader.loadLazy(((PrismLocalizableDefinition) Enum.valueOf((Class<Enum>) entityNameClass, entity.getName())).getDisplayProperty()));
        }

        return representation;
    }

    public ImportedAgeRangeResponse getImportedAgeRangeRepresentation(ImportedAgeRange ageRange) {
        ImportedAgeRangeResponse representation = getImportedEntitySimpleRepresentation(ageRange, ImportedAgeRangeResponse.class);
        representation.setLowerBound(ageRange.getLowerBound());
        representation.setUpperBound(ageRange.getUpperBound());
        return representation;
    }

    public ImportedAdvertDomicileResponse getImportedAdvertDomicileRepresentation(ImportedAdvertDomicile advertDomicile) {
        return getImportedEntitySimpleRepresentation(advertDomicile, ImportedAdvertDomicileResponse.class).withCurrency(advertDomicile.getCurrency());
    }

    public <T extends ImportedEntityRequest> List<T> getImportedEntityRepresentations(Class<T> requestClass, InputStream data) throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, requestClass);
        return objectMapper.readValue(data, collectionType);
    }

}
