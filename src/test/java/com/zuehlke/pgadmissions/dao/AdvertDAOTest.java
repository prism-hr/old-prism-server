package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldGetAllPrograms() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        Advert projectAdvert = new AdvertBuilder().isProgramAdvert(false).description("project").program(program).studyDuration(8).build();
        Advert programAdvert = new AdvertBuilder().isProgramAdvert(true).description("program").program(program).studyDuration(66).build();

        save(program, projectAdvert, programAdvert);
        flushAndClearSession();

        AdvertDAO advertDAO = new AdvertDAO(sessionFactory);
        Advert returnedAdvert = advertDAO.getProgramAdvert(program);

        assertEquals(true, returnedAdvert.getIsProgramAdvert());
        assertEquals("program", returnedAdvert.getDescription());
        assertEquals(66, (int) returnedAdvert.getStudyDuration());

        assertEquals("code1", returnedAdvert.getProgram().getCode());
    }

}
