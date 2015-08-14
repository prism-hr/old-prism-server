package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Map;

import org.joda.time.LocalDate;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertDepartment;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertInstitution;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

public enum PrismAdvertAttribute {

    CLOSING_DATE(AdvertClosingDate.class, LocalDate.class, "closingDate"),
    INDUSTRY_CATEGORY(AdvertIndustry.class, PrismAdvertIndustry.class, "industries"),
    FUNCTION_CATEGORY(AdvertFunction.class, PrismAdvertFunction.class, "functions"),
    THEME_CATEGORY(AdvertTheme.class, String.class, "themes"),
    INSTITUTION_TARGET(AdvertInstitution.class, Institution.class, "institutions"),
    DEPARTMENT_TARGET(AdvertDepartment.class, Department.class, "departments"),
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
