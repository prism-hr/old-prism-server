package com.zuehlke.pgadmissions.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityMappingDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAgeRangeResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedInstitutionResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedLanguageQualificationTypeResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedSubjectAreaResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.mapping.helpers.ImportedEntityTransformer;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ImportedEntityMapper {

    private Map<Resource, PropertyLoader> loaders = Maps.newHashMap();

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ObjectMapper objectMapper;

    public <T extends ImportedEntity<?, ?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity) {
        return getImportedEntityRepresentation(entity, null);
    }

    @SuppressWarnings({ "unchecked" })
    public <T extends ImportedEntity<?, ?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity, Institution institution) {
        Class<?> entityClass = entity.getClass();
        if (ImportedAgeRange.class.equals(entityClass)) {
            return (U) getImportedAgeRangeRepresentation((ImportedAgeRange) entity, institution);
        } else if (ImportedAdvertDomicile.class.equals(entityClass)) {
            return (U) getImportedAdvertDomicileRepresentation((ImportedAdvertDomicile) entity, institution);
        } else if (ImportedInstitution.class.equals(entityClass)) {
            return (U) getImportedInstitutionRepresentation((ImportedInstitution) entity, institution);
        } else if (ImportedLanguageQualificationType.class.equals(entityClass)) {
            return (U) getImportedLanguageQualificationTypeRepresentation((ImportedLanguageQualificationType) entity, institution);
        } else if (ImportedProgram.class.equals(entityClass)) {
            return (U) getImportedProgramRepresentation((ImportedProgram) entity, institution);
        } else if (ImportedSubjectArea.class.equals(entityClass)) {
            return (U) getImportedSubjectAreaRepresentation((ImportedSubjectArea) entity, institution);
        }

        return (U) getImportedEntitySimpleRepresentation((ImportedEntitySimple) entity, institution, ImportedEntityResponse.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S, T extends ImportedEntity<S, U>, U extends ImportedEntityMapping<T>, V extends ImportedEntityResponseDefinition<S> & ImportedEntityMappingDefinition> V getImportedEntitySimpleRepresentation(
            T entity, Institution institution, Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);
        representation.setId(entity.getId());

        boolean institutionNull = institution == null;
        Class<?> entityNameClass = (Class<?>) entity.getType().getEntityClassName();
        if (entityNameClass == null) {
            representation.setName(entity.getName());
        } else {
            Resource resource = institutionNull ? systemService.getSystem() : institution;
            PropertyLoader loader = loaders.get(resource);
            if (loader == null) {
                loader = applicationContext.getBean(PropertyLoader.class).localize(resource);
                loaders.put(institution, loader);
            }

            representation.setName(loader.load(((PrismLocalizableDefinition) Enum.valueOf((Class<Enum>) entityNameClass, entity.getName())).getDisplayProperty()));
        }

        if (institutionNull) {
            U mapping = importedEntityService.getEnabledImportedEntityMapping(institution, entity);
            if (mapping != null) {
                representation.setCode(mapping.getCode());
            }
        }

        return representation;
    }

    public ImportedAgeRangeResponse getImportedAgeRangeRepresentation(ImportedAgeRange ageRange, Institution institution) {
        ImportedAgeRangeResponse representation = getImportedEntitySimpleRepresentation(ageRange, institution, ImportedAgeRangeResponse.class);

        representation.setLowerBound(ageRange.getLowerBound());
        representation.setUpperBound(ageRange.getUpperBound());

        return representation;
    }

    public ImportedAdvertDomicileResponse getImportedAdvertDomicileRepresentation(ImportedAdvertDomicile advertDomicile, Institution institution) {
        return getImportedEntitySimpleRepresentation(advertDomicile, institution, ImportedAdvertDomicileResponse.class).withCurrency(
                advertDomicile.getCurrency());
    }

    public ImportedEntityResponse getImportedInstitutionSimpleRepresentation(ImportedInstitution importedInstitution) {
        return getImportedEntitySimpleRepresentation(importedInstitution, null, ImportedInstitutionResponse.class);
    }

    public ImportedInstitutionResponse getImportedInstitutionRepresentation(ImportedInstitution importedInstitution, Institution institution) {
        ImportedInstitutionResponse representation = getImportedEntitySimpleRepresentation(importedInstitution, institution, ImportedInstitutionResponse.class);

        representation.setDomicile(getImportedEntitySimpleRepresentation(importedInstitution.getDomicile(), institution, ImportedEntityResponse.class));
        representation.setUcasId(importedInstitution.getUcasId());
        representation.setFacebookId(importedInstitution.getFacebookId());

        return representation;
    }

    public ImportedLanguageQualificationTypeResponse getImportedLanguageQualificationTypeRepresentation(
            ImportedLanguageQualificationType languageQualificationType, Institution institution) {
        ImportedLanguageQualificationTypeResponse representation = getImportedEntitySimpleRepresentation(languageQualificationType, institution,
                ImportedLanguageQualificationTypeResponse.class);

        representation.setMinimumOverallScore(languageQualificationType.getMinimumOverallScore());
        representation.setMaximumOverallScore(languageQualificationType.getMaximumOverallScore());
        representation.setMinimumReadingScore(languageQualificationType.getMinimumReadingScore());
        representation.setMaximumReadingScore(languageQualificationType.getMaximumReadingScore());
        representation.setMinimumWritingScore(languageQualificationType.getMinimumWritingScore());
        representation.setMaximumWritingScore(languageQualificationType.getMaximumWritingScore());
        representation.setMinimumSpeakingScore(languageQualificationType.getMinimumSpeakingScore());
        representation.setMaximumSpeakingScore(languageQualificationType.getMaximumSpeakingScore());
        representation.setMinimumListeningScore(languageQualificationType.getMinimumListeningScore());
        representation.setMaximumListeningScore(languageQualificationType.getMaximumListeningScore());

        return representation;
    }

    public ImportedEntityResponse getImportedProgramSimpleRepresentation(ImportedProgram program) {
        return getImportedEntitySimpleRepresentation(program, null, ImportedEntityResponse.class);
    }

    public ImportedProgramResponse getImportedProgramRepresentation(ImportedProgram program, Institution institution) {
        ImportedProgramResponse representation = getImportedEntitySimpleRepresentation(program, institution, ImportedProgramResponse.class);

        representation.setInstitution(getImportedInstitutionRepresentation(program.getInstitution(), institution));

        ImportedEntitySimple qualificationType = program.getQualificationType();
        representation.setQualificationType(qualificationType == null ? null : getImportedEntitySimpleRepresentation(qualificationType, institution,
                ImportedEntityResponse.class));

        representation.setLevel(program.getLevel());
        representation.setQualification(program.getQualification());
        representation.setHomepage(program.getHomepage());

        return representation;
    }

    public ImportedSubjectAreaResponse getImportedSubjectAreaRepresentation(ImportedSubjectArea subjectArea, Institution institution) {
        ImportedSubjectArea parentSubjectArea = subjectArea.getParentSubjectArea();
        ImportedSubjectAreaResponse representation = getImportedEntitySimpleRepresentation(subjectArea, institution,
                ImportedSubjectAreaResponse.class);
        representation.setParentSubjectArea(parentSubjectArea == null ? null : parentSubjectArea.getId());
        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntityRequest, U extends ImportedEntity<?, ?>> U transformImportedEntity(T source, PrismImportedEntity targetEntity) {
        U target = BeanUtils.instantiate((Class<U>) targetEntity.getEntityClass());
        target.setName(source.getName());

        Class<? extends ImportedEntityTransformer<? extends ImportedEntityRequest, ? extends ImportedEntity<?, ?>>> transformerClass = targetEntity
                .getTransformerClass();
        if (transformerClass != null) {
            ImportedEntityTransformer<T, U> transformer = (ImportedEntityTransformer<T, U>) applicationContext.getBean(targetEntity.getTransformerClass());
            transformer.transform(source, target);
        }

        return target;
    }

    public <T extends ImportedEntityRequest> List<T> getImportedEntityRepresentations(PrismImportedEntity type, InputStream data) throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type.getRequestClass());
        return objectMapper.readValue(data, collectionType);
    }

}
