package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

public class ActionBuilder {

    private ApplicationFormAction id;

    public ActionBuilder id(ApplicationFormAction id) {
        this.id = id;
        return this;
    }

    public Action build() {
        Action action = new Action();
        action.setId(id);
        return action;
    }

}