package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.Country;
import com.zuehlke.pgadmissions.domain.imported.Disability;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.Ethnicity;
import com.zuehlke.pgadmissions.domain.imported.FundingSource;
import com.zuehlke.pgadmissions.domain.imported.Gender;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.Language;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.imported.QualificationType;
import com.zuehlke.pgadmissions.domain.imported.ReferralSource;
import com.zuehlke.pgadmissions.domain.imported.RejectionReason;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.imported.Title;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.FundingSources;
import com.zuehlke.pgadmissions.referencedata.jaxb.Genders;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgramTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.StudyOptions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Titles;

public enum PrismImportedEntity {

    COUNTRY(Countries.class, "country", Country.class, "xml/defaultEntities/country.xml", "xsd/import/country.xsd"), //
    DISABILITY(Disabilities.class, "disability", Disability.class, "xml/defaultEntities/disability.xml", "xsd/import/disability.xsd"), //
    DOMICILE(Domiciles.class, "domicile", Domicile.class, "xml/defaultEntities/domicile.xml", "xsd/import/domicile.xsd"), //
    ETHNICITY(Ethnicities.class, "ethnicity", Ethnicity.class, "xml/defaultEntities/ethnicity.xml", "xsd/import/ethnicity.xsd"), //
    NATIONALITY(Nationalities.class, "nationality", Language.class, "xml/defaultEntities/nationality.xml", "xsd/import/nationality.xsd"), //
    PROGRAM(ProgrammeOccurrences.class, "programmeOccurrence", Program.class, null, "xsd/import/program.xsd"), //
    QUALIFICATION_TYPE(Qualifications.class, "qualification", QualificationType.class, "xml/defaultEntities/qualificationType.xml",
            "xsd/import/qualificationType.xsd"), //
    REFERRAL_SOURCE(SourcesOfInterest.class, "sourceOfInterest", ReferralSource.class, "xml/defaultEntities/referralSource.xml",
            "xsd/import/referralSource.xsd"), FUNDING_SOURCE(FundingSources.class, "fundingSource", FundingSource.class,
            "xml/defaultEntities/fundingSource.xml", "xsd/import/fundingSource.xsd"), //
    LANGUAGE_QUALIFICATION_TYPE(LanguageQualificationTypes.class, "languageQualificationType", ImportedLanguageQualificationType.class,
            "xml/defaultEntities/languageQualificationType.xml", "xsd/import/languageQualificationType.xsd"), //
    TITLE(Titles.class, "title", Title.class, "xml/defaultEntities/title.xml", "xsd/import/title.xsd"), //
    INSTITUTION(Institutions.class, "institution", ImportedInstitution.class, "xml/defaultEntities/institution.xml", "xsd/import/institution.xsd"), //
    GENDER(Genders.class, "gender", Gender.class, "xml/defaultEntities/gender.xml", "xsd/import/gender.xsd"), REJECTION_REASON(RejectionReasons.class,
            "rejectionReason", RejectionReason.class, "xml/defaultEntities/rejectionReason.xml", "xsd/import/rejectionReason.xsd"), //
    STUDY_OPTION(StudyOptions.class, "studyOption", StudyOption.class, "xml/defaultEntities/studyOption.xml", "xsd/import/studyOption.xsd"), //
    PROGRAM_TYPE(ProgramTypes.class, "programType", ProgramType.class, "xml/defaultEntities/programType.xml", "xsd/import/programType.xsd");

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private String defaultLocation;

    private String schemaLocation;

    private static final List<PrismImportedEntity> defaultLocations = Lists.newArrayList();

    static {
        for (PrismImportedEntity entity : values()) {
            if (entity.defaultLocation != null) {
                defaultLocations.add(entity);
            }
        }
    }

    private PrismImportedEntity(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass, String defaultLocation, String schemaLocation) {
        this.jaxbClass = jaxbClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
        this.defaultLocation = defaultLocation;
        this.schemaLocation = schemaLocation;
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

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public final String getSchemaLocation() {
        return schemaLocation;
    }

    public static final List<PrismImportedEntity> getDefaultLocations() {
        return defaultLocations;
    }

}
