package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.referencedata.jaxb.*;

public enum PrismImportedEntityType {

    COUNTRY(Countries.class, "country", Country.class), //
    DISABILITY(Disabilities.class, "disability", Disability.class), //
    DOMICILE(Domiciles.class, "domicile", Domicile.class), //
    ETHNICITY(Ethnicities.class, "ethnicity", Ethnicity.class), //
    NATIONALITY(Nationalities.class, "nationality", Language.class), //
    PROGRAM(ProgrammeOccurrences.class, "programmeOccurrence", Program.class), //
    QUALIFICATION_TYPE(Qualifications.class, "qualification", QualificationType.class), //
    REFERRAL_SOURCE(SourcesOfInterest.class, "sourceOfInterest", ReferralSource.class),

    FUNDING_SOURCE(FundingSources.class, "fundingSource", FundingSource.class),
    LANGUAGE_QUALIFICATION_TYPE(LanguageQualificationTypes.class, "languageQualificationType", LanguageQualificationType.class),
    TITLE(Titles.class, "title", Title.class),
    INSTITUTION(Institutions.class, "institution", ImportedInstitution.class);

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private PrismImportedEntityType(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass) {
        this.jaxbClass = jaxbClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
    }

    public Class<?> getJaxbClass() {
        return jaxbClass;
    }

    public String getJaxbPropertyName() {
        return jaxbPropertyName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

}
