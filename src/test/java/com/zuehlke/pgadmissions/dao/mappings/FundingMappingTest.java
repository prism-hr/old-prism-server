package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class FundingMappingTest extends AutomaticRollbackTestCase {

    private ApplicationForm applicationForm;

    @Test
    public void shouldSaveAndLoadFunding() throws ParseException {

        Document document = new Document();
        document.setContent("s".getBytes());
        document.setFileName("name.txt");
        document.setContentType("bob");
        document.setType(DocumentType.PERSONAL_STATEMENT);
        sessionFactory.getCurrentSession().save(document);
        flushAndClearSession();
        Date awardDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/12/2011");
        
        Funding funding = new FundingBuilder().application(applicationForm).awardDate(awardDate).description("hello").type(FundingType.EMPLOYER).value("1000")
                .document(document).build();
        save(funding);
        Integer id = funding.getId();

        assertNotNull(id);
        Funding reloadedFunding = (Funding) sessionFactory.getCurrentSession().get(Funding.class, id);
        assertSame(funding, reloadedFunding);

        flushAndClearSession();
        reloadedFunding = (Funding) sessionFactory.getCurrentSession().get(Funding.class, id);
        assertNotSame(funding, reloadedFunding);
        assertEquals(applicationForm, funding.getApplication());
        assertEquals(awardDate, funding.getAwardDate());
        assertEquals("hello", funding.getDescription());
        assertEquals(FundingType.EMPLOYER, funding.getType());
        assertEquals("1000", funding.getValue());
        assertEquals(document, funding.getDocument());
    }

    @Before
    public void prepare() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a30").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(applicant);
        applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
        save(applicationForm);
        flushAndClearSession();
    }
}
