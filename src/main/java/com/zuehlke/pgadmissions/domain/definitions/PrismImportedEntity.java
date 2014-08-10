package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.FundingSource;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.RejectionReason;
import com.zuehlke.pgadmissions.domain.ResidenceState;
import com.zuehlke.pgadmissions.domain.Title;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.FundingSources;
import com.zuehlke.pgadmissions.referencedata.jaxb.Genders;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.RejectionReasons;
import com.zuehlke.pgadmissions.referencedata.jaxb.ResidenceStates;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.Titles;

public enum PrismImportedEntity {

    COUNTRY(Countries.class, "country", Country.class, "xml/defaultEntities/country.xml"), //
    DISABILITY(Disabilities.class, "disability", Disability.class, "xml/defaultEntities/disability.xml"), //
    DOMICILE(Domiciles.class, "domicile", Domicile.class, "xml/defaultEntities/domicile.xml"), //
    ETHNICITY(Ethnicities.class, "ethnicity", Ethnicity.class, "xml/defaultEntities/ethnicity.xml"), //
    NATIONALITY(Nationalities.class, "nationality", Language.class, "xml/defaultEntities/nationality.xml"), //
    PROGRAM(ProgrammeOccurrences.class, "programmeOccurrence", Program.class, null), //
    QUALIFICATION_TYPE(Qualifications.class, "qualification", QualificationType.class, "xml/defaultEntities/qualificationType.xml"), //
    REFERRAL_SOURCE(SourcesOfInterest.class, "sourceOfInterest", ReferralSource.class, "xml/defaultEntities/sourceOfInterest.xml"),
    FUNDING_SOURCE(FundingSources.class, "fundingSource", FundingSource.class, "xml/defaultEntities/fundingSource.xml"), //
    LANGUAGE_QUALIFICATION_TYPE(LanguageQualificationTypes.class, "languageQualificationType", LanguageQualificationType.class, "xml/defaultEntities/languageQualificationType.xml"), //
    TITLE(Titles.class, "title", Title.class, "xml/defaultEntities/title.xml"), //
    INSTITUTION(Institutions.class, "institution", ImportedInstitution.class, "xml/defaultEntities/institution.xml"), //
    GENDER(Genders.class, "gender", Gender.class, "xml/defaultEntities/gender.xml"),
    REJECTION_REASON(RejectionReasons.class, "rejectionReason", RejectionReason.class, "xml/defaultEntities/rejectionReason.xml"),
    RESIDENCE_STATE(ResidenceStates.class, "residenceState", ResidenceState.class, "xml/defaultEntities/residenceState.xml");

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;
    
    private String defaultLocation;

    private PrismImportedEntity(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass, String defaultLocation) {
        this.jaxbClass = jaxbClass;
        this.jaxbPropertyName = jaxbPropertyName;
        this.entityClass = entityClass;
        this.defaultLocation = defaultLocation;
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

    public void setDefaultLocation(String defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

}
