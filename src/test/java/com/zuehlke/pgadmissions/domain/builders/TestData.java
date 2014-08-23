package com.zuehlke.pgadmissions.domain.builders;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class TestData {

    public static ImportedInstitution aImportedInstitution(Domicile domicile) {
        return new ImportedInstitution().withCode("AGH").withName("Akademia G\u00F3rniczo-Hutnicza").withDomicile(domicile).withEnabled(true);
    }

    public static Institution aInstitution(User user, System system, InstitutionDomicile domicile) {
        return new Institution().withUser(user).withSystem(system).withTitle("Akademia G\u00F3rniczo-Hutnicza")
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withHomepage("www.agh.edu.pl")
                .withAddress(new InstitutionAddress().withCountry(domicile));
    }

    public static User aUser(UserAccount account) {
        return new User().withFirstName("Kuba").withLastName("Fibinger").withEmail("kuba@fibi.pl").withActivationCode("activation!").withAccount(account);
    }

    public static UserAccount aUserAccount() {
        return new UserAccount().withEnabled(true).withPassword("password");
    }

    public static Application anApplicationForm(System system, Institution institution, Program program, User user, State state) {
        return new Application().withSystem(system).withInstitution(institution).withUser(user).withProgram(program).withState(state)
                .withDueDate(new LocalDate().plusWeeks(2)).withCreatedTimestamp(new DateTime());
    }

    public static Address anAddress(Domicile domicile) {
        return new Address().withDomicile(domicile).withLine1("ul. Leszczynska 29").withTown("Bielsko-Biala").withRegion("woj. Slaskie");
    }

    public static Program aProgram(PrismProgramType programType, Institution institution, User user, State state) {
        return new Program().withCode("AAA").withTitle("Amazing program!").withState(state).withRequireProjectDefinition(false).withProgramType(programType)
                .withUser(user).withInstitution(institution);
    }

    public static State aState(PrismState stateValue) {
        return new State().withId(stateValue);
    }

    public static ApplicationQualification aQualification(Application application, QualificationType qualificationType, Document document,
            ImportedInstitution institution) {
        return new ApplicationQualification().withAwardDate(new LocalDate()).withGrade("").withTitle("").withLanguage("Abkhazian").withSubject("")
                .withCompleted(true).withStartDate(new LocalDate()).withType(qualificationType).withApplication(application).withDocument(document)
                .withInstitution(institution);
    }

}
