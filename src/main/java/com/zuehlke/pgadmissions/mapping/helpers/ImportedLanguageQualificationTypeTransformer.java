package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;

@Component
public class ImportedLanguageQualificationTypeTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.request.ImportedLanguageQualificationTypeRequest, ImportedLanguageQualificationType> {

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.request.ImportedLanguageQualificationTypeRequest concreteSource,
            ImportedLanguageQualificationType concreteTarget) {
        concreteTarget.setMinimumOverallScore(concreteSource.getMaximumOverallScore());
        concreteTarget.setMaximumOverallScore(concreteSource.getMaximumOverallScore());
        concreteTarget.setMinimumReadingScore(concreteSource.getMinimumReadingScore());
        concreteTarget.setMaximumReadingScore(concreteSource.getMaximumReadingScore());
        concreteTarget.setMinimumWritingScore(concreteSource.getMinimumWritingScore());
        concreteTarget.setMaximumReadingScore(concreteSource.getMaximumReadingScore());
        concreteTarget.setMinimumSpeakingScore(concreteSource.getMinimumSpeakingScore());
        concreteTarget.setMaximumSpeakingScore(concreteSource.getMaximumSpeakingScore());
        concreteTarget.setMinimumListeningScore(concreteSource.getMinimumListeningScore());
        concreteTarget.setMaximumListeningScore(concreteSource.getMaximumListeningScore());
    }

}
