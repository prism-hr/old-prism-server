package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class TestData {

    public static Institution aQualificationInstitution() {
        return new Institution().withCode("AGH").withName("Akademia G\u00F3rniczo-Hutnicza").withDomicileCode("PL")
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED));
    }

}
