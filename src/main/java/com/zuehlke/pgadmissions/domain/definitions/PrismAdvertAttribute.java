package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Map;

import org.joda.time.LocalDate;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertDepartment;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertInstitution;
import com.zuehlke.pgadmissions.domain.advert.AdvertProgram;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.institution.Institution;

public enum PrismAdvertAttribute {

    CLOSING_DATE(AdvertClosingDate.class, LocalDate.class),
    INDUSTRY_CATEGORY(AdvertIndustry.class, PrismAdvertIndustry.class),
    FUNCTION_CATEGORY(AdvertFunction.class, PrismAdvertFunction.class),
    THEME_CATEGORY(AdvertTheme.class, String.class),
    COMPETENCE_TARGET(AdvertCompetence.class, Competence.class),
    INSTITUTION_TARGET(AdvertInstitution.class, Institution.class),
    DEPARTMENT_TARGET(AdvertDepartment.class, Department.class),
    PROGRAM_TARGET(AdvertProgram.class, ImportedProgram.class),
    SUBJECT_AREA_TARGET(AdvertSubjectArea.class, ImportedSubjectArea.class);

    private Class<? extends AdvertAttribute<?>> attributeClass;

    private Class<?> valueClass;

    private static Map<Class<?>, PrismAdvertAttribute> byValueClass = Maps.newHashMap();

    static {
        for (PrismAdvertAttribute attribute : values()) {
            Class<?> valueClass = attribute.getValueClass();
            if (byValueClass.containsKey(valueClass)) {
                throw new Error();
            }
            byValueClass.put(valueClass, attribute);
        }
    }

    private PrismAdvertAttribute(Class<? extends AdvertAttribute<?>> attributeClass, Class<?> valueClass) {
        this.attributeClass = attributeClass;
        this.valueClass = valueClass;
    }

    public Class<? extends AdvertAttribute<?>> getAttributeClass() {
        return attributeClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public static PrismAdvertAttribute getByValueClass(Class<?> valueClass) {
        return byValueClass.get(valueClass);
    }

}
