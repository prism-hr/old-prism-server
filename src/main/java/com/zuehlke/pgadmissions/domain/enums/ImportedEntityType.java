package com.zuehlke.pgadmissions.domain.enums;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;

public enum ImportedEntityType {

    COUNTRY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Countries.class, "country", Country.class), //
    DISABILITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Disabilities.class, "disability", Disability.class), //
    DOMICILE(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Domiciles.class, "domicile", Domicile.class), //
    ETHNICITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Ethnicities.class, "ethnicity", Ethnicity.class), //
    NATIONALITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Nationalities.class, "nationality", Language.class), //
    PROGRAM(com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.class, "programmeOccurrence", Program.class), //
    QUALIFICATION_TYPE(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Qualifications.class, "qualification", QualificationType.class), //
    REFERRAL_SOURCE(com.zuehlke.pgadmissions.referencedata.v2.jaxb.SourcesOfInterest.class, "sourceOfInterest", SourcesOfInterest.class);

    private Class<?> jaxbClass;

    private String jaxbPropertyName;

    private Class<?> entityClass;

    private ImportedEntityType(Class<?> jaxbClass, String jaxbPropertyName, Class<?> entityClass) {
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
