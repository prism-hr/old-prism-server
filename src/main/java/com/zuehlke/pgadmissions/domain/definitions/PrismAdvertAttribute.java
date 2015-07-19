package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.advert.*;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import org.joda.time.LocalDate;

import java.util.Map;

public enum PrismAdvertAttribute {

    CLOSING_DATE(AdvertClosingDate.class, LocalDate.class, "closingDate"),
    INDUSTRY_CATEGORY(AdvertIndustry.class, PrismAdvertIndustry.class, "industries"),
    FUNCTION_CATEGORY(AdvertFunction.class, PrismAdvertFunction.class, "functions"),
    THEME_CATEGORY(AdvertTheme.class, String.class, "themes"),
    COMPETENCE_TARGET(AdvertCompetence.class, Competence.class, "competences"),
    INSTITUTION_TARGET(AdvertInstitution.class, Institution.class, "institutions"),
    DEPARTMENT_TARGET(AdvertDepartment.class, Department.class, "departments"),
    PROGRAM_TARGET(AdvertProgram.class, Program.class, "programs"),
    SUBJECT_AREA_TARGET(AdvertSubjectArea.class, ImportedSubjectArea.class, "subjectAreas");

    private final Class<? extends AdvertAttribute<?>> attributeClass;

    private final Class<?> valueClass;

    private final String propertyName;

    private static Map<String, PrismAdvertAttribute> byPropertyName = Maps.newHashMap();

    static {
        for (PrismAdvertAttribute attribute : values()) {
            String propertyName = attribute.getPropertyName();
            if (byPropertyName.containsKey(propertyName)) {
                throw new Error();
            }
            byPropertyName.put(propertyName, attribute);
        }
    }

    PrismAdvertAttribute(Class<? extends AdvertAttribute<?>> attributeClass, Class<?> valueClass, String propertyName) {
        this.attributeClass = attributeClass;
        this.valueClass = valueClass;
        this.propertyName = propertyName;
    }

    public Class<? extends AdvertAttribute<?>> getAttributeClass() {
        return attributeClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static PrismAdvertAttribute getByPropertyName(String propertyName) {
        return byPropertyName.get(propertyName);
    }

}
