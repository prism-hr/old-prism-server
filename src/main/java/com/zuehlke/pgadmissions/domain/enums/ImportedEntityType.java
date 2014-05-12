package com.zuehlke.pgadmissions.domain.enums;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationType;

public enum ImportedEntityType {

    COUNTRY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Countries.class,  Country.class), //
    DISABILITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Disabilities.class, Disability.class), //
    DOMICILE(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Domiciles.class, Domicile.class), //
    ETHNICITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Ethnicities.class, Ethnicity.class), //
    NATIONALITY(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Nationalities.class, Language.class), //
    PROGRAM(com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.class, Program.class), //
    QUALIFICATION_TYPE(com.zuehlke.pgadmissions.referencedata.v2.jaxb.Qualifications.class, QualificationType.class);

    private Class<?> jaxbClass;
    
    private Class<?> entityClass;

    private ImportedEntityType(Class<?> jaxbClass, Class<?> entityClass) {
        this.jaxbClass = jaxbClass;
        this.entityClass = entityClass;
    }

    public Class<?> getJaxbClass() {
        return jaxbClass;
    }
    
    public Class<?> getEntityClass() {
        return entityClass;
    }

}
