package com.zuehlke.pgadmissions.domain.builders;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ProgramState;

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

    public static ApplicationForm anApplicationForm(User user, Program program, State state) {
        return new ApplicationForm().withUser(user).withProgram(program).withState(state).withDueDate(new LocalDate().plusWeeks(2));
    }

    public static Address anAddress(Domicile domicile) {
        return new Address().withDomicile(domicile).withLine1("ul. Leszczynska 29").withTown("Bielsko-Biala").withRegion("woj. Slaskie");
    }

    public static Program aProgram(Institution institution) {
        return new Program().withCode("AAA").withTitle("Amazing program!").withState(ProgramState.PROGRAM_APPROVED).withRequireProjectDefinition(false)
                .withInstitution(institution);
    }

}
