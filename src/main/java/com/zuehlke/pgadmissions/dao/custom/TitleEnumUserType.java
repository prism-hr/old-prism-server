package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.Title;

public class TitleEnumUserType extends EnumUserType<Title> {

    public TitleEnumUserType() {
        super(Title.class);
    }
}
