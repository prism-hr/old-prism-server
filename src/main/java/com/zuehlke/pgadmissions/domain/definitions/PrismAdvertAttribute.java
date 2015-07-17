package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.*;

import java.util.Map;

public enum PrismAdvertAttribute {

    CLOSING_DATE(AdvertClosingDate.class, "closingDate"),
    INDUSTRY_CATEGORY(AdvertIndustry.class, "industries"),
    FUNCTION_CATEGORY(AdvertFunction.class, "functions"),
    THEME_CATEGORY(AdvertTheme.class, "themes"),
    COMPETENCE_TARGET(AdvertCompetence.class, "competences"),
    INSTITUTION_TARGET(AdvertInstitution.class, "institutions"),
    DEPARTMENT_TARGET(AdvertDepartment.class, "departments"),
    PROGRAM_TARGET(AdvertProgram.class, "programs"),
    SUBJECT_AREA_TARGET(AdvertSubjectArea.class, "subjectAreas");

    private Class<? extends AdvertAttribute<?>> attributeClass;

    private String propertyName;

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

    PrismAdvertAttribute(Class<? extends AdvertAttribute<?>> attributeClass, String propertyName) {
        this.attributeClass = attributeClass;
        this.propertyName = propertyName;
    }

    public Class<? extends AdvertAttribute<?>> getAttributeClass() {
        return attributeClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static PrismAdvertAttribute getByPropertyName(String propertyName) {
        return byPropertyName.get(propertyName);
    }

}
