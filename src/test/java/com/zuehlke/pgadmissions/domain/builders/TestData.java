package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class TestData {

    public static Institution aQualificationInstitution() {
        return new Institution().withCode("AGH").withName("Akademia G\u00F3rniczo-Hutnicza").withDomicileCode("PL")
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED));
    }
    
    public static User aUser(UserAccount account) {
        return new User().withFirstName("Kuba").withLastName("Fibinger").withEmail("kuba@fibi.pl").withActivationCode("activation!").withAccount(account);
    }

    public static UserAccount aUserAccount() {
        return new UserAccount().withEnabled(true).withPassword("password");
    }
    
}
