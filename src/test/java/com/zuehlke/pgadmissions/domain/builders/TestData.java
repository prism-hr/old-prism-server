package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class TestData {

    public static ImportedInstitution aImportedInstitution(Domicile domicile) {
        return new ImportedInstitution().withCode("AGH").withName("Akademia G\u00F3rniczo-Hutnicza").withDomicile(domicile)
                .withEnabled(true);
    }
    
    public static Institution aInstitution(InstitutionDomicile domicile) {
        return new Institution().withName("Akademia G\u00F3rniczo-Hutnicza").withDomicile(domicile)
                .withState(new State().withId(PrismState.INSTITUTION_APPROVED));
    }

    public static User aUser(UserAccount account) {
        return new User().withFirstName("Kuba").withLastName("Fibinger").withEmail("kuba@fibi.pl").withActivationCode("activation!").withAccount(account);
    }

    public static UserAccount aUserAccount() {
        return new UserAccount().withEnabled(true).withPassword("password");
    }

    public static Application anApplicationForm(User user, Program program, State state) {
        return new Application().withUser(user).withProgram(program).withState(state).withDueDate(new LocalDate().plusWeeks(2));
    }

    public static Address anAddress(Domicile domicile) {
        return new Address().withDomicile(domicile).withLine1("ul. Leszczynska 29").withTown("Bielsko-Biala").withRegion("woj. Slaskie");
    }

    public static Program aProgram(ProgramType programType, Institution institution, User user, State state) {
        return new Program().withCode("AAA").withTitle("Amazing program!").withState(state).withRequireProjectDefinition(false)
                .withStudyDuration(20).withProgramType(programType).withUser(user).withInstitution(institution);
    }

    public static State aState(PrismState stateValue) {
        return new State().withId(stateValue);
    }

    public static UserRole aUserRole(Application applicaton, Role role, User user, User requestingUser) {
        return new UserRole().withApplication(applicaton).withRole(role).withUser(user).withRequestingUser(requestingUser).withAssignedTimestamp(new DateTime());
    }

    public static Document aDocument() {
        return new Document().withFileName("dupa").withContent(new byte[0]).withContentType("application/pdf").withIsReferenced(false)
                .withType(DocumentType.CV);
    }

    public static Qualification aQualification(Application application, QualificationType qualificationType, Document document, ImportedInstitution institution) {
        return new Qualification().withAwardDate(new Date()).withGrade("").withTitle("").withLanguage("Abkhazian").withSubject("").withCompleted(true)
                .withStartDate(new Date()).withType(qualificationType).withApplication(application).withDocument(document).withExport(false).withInstitution(institution);
    }

}
