package com.zuehlke.pgadmissions.mappers;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
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
            return getImportedAgeRangeRepresentation((ImportedAgeRange) entity, institution);
        } else if (ImportedInstitution.class.equals(entityClass)) {
            return getImportedInstitutionRepresentation((ImportedInstitution) entity, institution);
        } else if (ImportedLanguageQualificationType.class.equals(entityClass)) {
            return getImportedLanguageQualificationTypeRepresentation((ImportedLanguageQualificationType) entity, institution);
        } else if (ImportedProgram.class.equals(entityClass)) {
            return getImportedProgramRepresentation((ImportedProgram) entity, institution);
        } else if (ImportedSubjectArea.class.equals(entityClass)) {
            return getImportedSubjectAreaRepresentation((ImportedSubjectArea) entity, institution);
        }

        return getImportedEntitySimpleRepresentation(entity, institution, ImportedEntitySimpleRepresentation.class);
    }

    private <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>, W extends ImportedEntitySimpleRepresentation> W getImportedEntitySimpleRepresentation(
            T entity, Institution institution, Class<W> returnType) {
        W representation = BeanUtils.instantiate(returnType);

        representation.setId(entity.getId());
        representation.setName(entity.getName());

        if (institution != null) {
            V mapping = importedEntityService.getEnabledImportedEntityMapping(institution, entity);
            representation.setCode(mapping.getCode());
        }

        return representation;
    }

    private ImportedEntitySimpleRepresentation getImportedAgeRangeRepresentation(ImportedAgeRange ageRange, Institution institution) {
        ImportedAgeRangeRepresentation representation = getImportedEntitySimpleRepresentation(ageRange, institution, ImportedAgeRangeRepresentation.class);

        representation.setLowerBound(ageRange.getLowerBound());
        representation.setUpperBound(ageRange.getUpperBound());

        return representation;
    }

    public ImportedInstitutionRepresentation getImportedInstitutionRepresentation(ImportedInstitution importedInstitution) {
        return getImportedInstitutionRepresentation(importedInstitution, null);
    }

    public ImportedInstitutionRepresentation getImportedInstitutionRepresentation(ImportedInstitution importedInstitution, Institution institution) {
        ImportedInstitutionRepresentation representation = getImportedEntitySimpleRepresentation(importedInstitution, institution,
                ImportedInstitutionRepresentation.class);

        representation.setDomicile(getImportedEntitySimpleRepresentation(importedInstitution.getDomicile(), institution,
                ImportedEntitySimpleRepresentation.class));
        representation.setUcasId(importedInstitution.getUcasId());
        representation.setFacebookId(importedInstitution.getFacebookId());

        return representation;
    }

    public ImportedLanguageQualificationTypeRepresentation getImportedLanguageQualificationTypeRepresentation(
            ImportedLanguageQualificationType languageQualificationType, Institution institution) {
        ImportedLanguageQualificationTypeRepresentation representation = getImportedEntitySimpleRepresentation(languageQualificationType, institution,
                ImportedLanguageQualificationTypeRepresentation.class);

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
        ImportedProgramRepresentation representation = getImportedEntitySimpleRepresentation(program, institution, ImportedProgramRepresentation.class);

        representation.setInstitution(getImportedInstitutionRepresentation(program.getInstitution(), institution));
        representation.setQualificationType(getImportedEntitySimpleRepresentation(program.getQualificationType(), institution,
                ImportedEntitySimpleRepresentation.class));
        representation.setLevel(program.getLevel());
        representation.setQualification(program.getQualification());
        representation.setHomepage(program.getHomepage());

        return representation;
    }

    public ImportedSubjectAreaRepresentation getImportedSubjectAreaRepresentation(ImportedSubjectArea subjectArea, Institution institution) {
        ImportedSubjectArea parentSubjectArea = subjectArea.getParentSubjectArea();
        ImportedSubjectAreaRepresentation representation = getImportedEntitySimpleRepresentation(subjectArea, institution,
                ImportedSubjectAreaRepresentation.class);
        representation.setParentSubjectArea(parentSubjectArea == null ? null : parentSubjectArea.getId());
        return representation;
    }

}
