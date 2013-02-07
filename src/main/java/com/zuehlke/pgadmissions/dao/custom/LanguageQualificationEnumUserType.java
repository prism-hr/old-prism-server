package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class LanguageQualificationEnumUserType extends EnumUserType<LanguageQualificationEnum> {

    public LanguageQualificationEnumUserType(){
        super(LanguageQualificationEnum.class);
    }
}
