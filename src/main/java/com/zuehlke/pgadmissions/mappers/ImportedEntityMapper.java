package com.zuehlke.pgadmissions.mappers;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedAgeRangeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedLanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedSubjectAreaRepresentation;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Service
@Transactional
public class ImportedEntityMapper {

    @Inject
    private ImportedEntityService importedEntityService;

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntityRepresentation(T entity) {
        return getImportedEntityRepresentation(entity, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntityRepresentation(
            T entity, Institution institution) {

        Class<T> entityClass = (Class<T>) entity.getClass();
        if (ImportedAgeRange.class.equals(entityClass)) {
            return getImportedAgeRangeRepresentation(entity, institution);
        } else if (ImportedInstitution.class.equals(entityClass)) {
            return getImportedInstitutionRepresentation(entity, institution);
        } else if (ImportedLanguageQualificationType.class.equals(entityClass)) {
            return getImportedLanguageQualificationTypeRepresentation((ImportedLanguageQualificationType) entity, institution);
        } else if (ImportedProgram.class.equals(entityClass)) {
            return getImportedProgramRepresentation((ImportedProgram) entity, institution);
        } else if (ImportedSubjectArea.class.equals(entityClass)) {
            return getImportedSubjectAreaRepresentation((ImportedSubjectArea) entity, institution);
        }

        return getImportedEntitySimpleRepresentation(entity, institution);
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntitySimpleRepresentation(
            T entity) {
        return getImportedEntityRepresentation(entity, null);
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntitySimpleRepresentation(
            T entity, Institution institution) {
        ImportedEntitySimpleRepresentation representation = new ImportedEntitySimpleRepresentation().withId(entity.getId()).withName(entity.getName());

        if (institution != null) {
            V mapping = importedEntityService.getEnabledImportedEntityMapping(institution, entity);
            representation.setCode(mapping.getCode());
        }

        return representation;
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedAgeRangeRepresentation(
            T entity) {
        return getImportedAgeRangeRepresentation(entity, null);
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedAgeRangeRepresentation(
            T entity, Institution institution) {
        ImportedAgeRange ageRange = (ImportedAgeRange) entity;
        ImportedAgeRangeRepresentation representation = (ImportedAgeRangeRepresentation) getImportedEntitySimpleRepresentation(entity, institution);
        representation.setLowerBound(ageRange.getLowerBound());
        representation.setUpperBound(ageRange.getUpperBound());
        return representation;
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedInstitutionRepresentation getImportedInstitutionRepresentation(
            T entity) {
        return getImportedInstitutionRepresentation(entity, null);
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedInstitutionRepresentation getImportedInstitutionRepresentation(
            T entity, Institution institution) {
        ImportedInstitution importedInstitution = (ImportedInstitution) entity;
        ImportedInstitutionRepresentation representation = (ImportedInstitutionRepresentation) getImportedEntitySimpleRepresentation(entity, institution);
        representation.setDomicile(importedInstitution.getDomicile().getId());
        representation.setUcasId(importedInstitution.getUcasId());
        representation.setFacebookId(importedInstitution.getFacebookId());
        return representation;
    }

    public ImportedLanguageQualificationTypeRepresentation getImportedLanguageQualificationTypeRepresentation(
            ImportedLanguageQualificationType languageQualificationType) {
        return getImportedLanguageQualificationTypeRepresentation(languageQualificationType, null);
    }

    public ImportedLanguageQualificationTypeRepresentation getImportedLanguageQualificationTypeRepresentation(
            ImportedLanguageQualificationType languageQualificationType, Institution institution) {
        ImportedLanguageQualificationTypeRepresentation representation = (ImportedLanguageQualificationTypeRepresentation) getImportedEntitySimpleRepresentation(
                languageQualificationType, institution);
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

    public ImportedProgramRepresentation getImportedProgramRepresentation(ImportedProgram program) {
        return getImportedProgramRepresentation(program, null);
    }

    public ImportedProgramRepresentation getImportedProgramRepresentation(ImportedProgram program, Institution institution) {
        ImportedProgramRepresentation representation = (ImportedProgramRepresentation) getImportedEntitySimpleRepresentation(program, institution);
        representation.setInstitution(program.getInstitution().getId());
        representation.setLevel(program.getLevel());
        representation.setQualification(program.getQualification());
        representation.setHomepage(program.getHomepage());
        return representation;
    }

    public ImportedSubjectAreaRepresentation getImportedSubjectAreaRepresentation(ImportedSubjectArea subjectArea) {
        return getImportedSubjectAreaRepresentation(subjectArea);
    }

    public ImportedSubjectAreaRepresentation getImportedSubjectAreaRepresentation(ImportedSubjectArea subjectArea, Institution institution) {
        ImportedSubjectArea parentSubjectArea = subjectArea.getParentSubjectArea();
        ImportedSubjectAreaRepresentation representation = (ImportedSubjectAreaRepresentation) getImportedEntitySimpleRepresentation(subjectArea, institution);
        representation.setParentSubjectArea(parentSubjectArea == null ? null : parentSubjectArea.getId());
        return representation;
    }

}
