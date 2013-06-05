package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class AdvertDAOTest extends AutomaticRollbackTestCase {
	
	private AdvertDAO advertDAO;

	@Before
	public void setUp(){
		advertDAO = new AdvertDAO(sessionFactory);
	}

    @Test
    public void shouldGetProgramAdvert() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        Advert projectAdvert = new AdvertBuilder().isProgramAdvert(false).description("project").program(program).studyDuration(8).build();
        Advert programAdvert = new AdvertBuilder().isProgramAdvert(true).description("program").program(program).studyDuration(66).build();

        save(program, projectAdvert, programAdvert);
        flushAndClearSession();

        Advert returnedAdvert = advertDAO.getProgramAdvert(program);

        assertEquals(true, returnedAdvert.getIsProgramAdvert());
        assertEquals("program", returnedAdvert.getDescription());
        assertEquals(66, (int) returnedAdvert.getStudyDuration());

        assertEquals("code1", returnedAdvert.getProgram().getCode());
    }

    
    @Test
    public void shouldGetActiveAdverts() {
    	Program programWithInactiveProgramAdvert = new ProgramBuilder().code("inactive").title("another title").build();
    	Advert inactiveProgramAdvert = new AdvertBuilder().isProgramAdvert(true).description("inactive program").program(programWithInactiveProgramAdvert).studyDuration(9).active(false).build();

    	Program  programWithActiveProgramAdvert = new ProgramBuilder().code("program").title("another title").build();
    	Advert programAdvert = new AdvertBuilder().isProgramAdvert(true).description("program").program(programWithActiveProgramAdvert).studyDuration(66).build();
    	
    	Program  programWithProjectAdvert = new ProgramBuilder().code("project").title("another title").build();
    	Advert projectAdvert = new AdvertBuilder().isProgramAdvert(false).description("project").program(programWithInactiveProgramAdvert).studyDuration(8).build();

    	save(programWithInactiveProgramAdvert,programWithActiveProgramAdvert, programWithProjectAdvert);
    	save(inactiveProgramAdvert, programAdvert, projectAdvert);
    	flushAndClearSession();
    	
    	List<Advert> activeAdverts = advertDAO.getActiveProgramAdverts();
    	assertThat(activeAdverts.size(), greaterThanOrEqualTo(1));
    	assertTrue(advertInList(programAdvert, activeAdverts));
    }

	private boolean advertInList(Advert programAdvert,
			List<Advert> activeAdverts) {
		for(Advert loadedAdvert:activeAdverts){
			if(loadedAdvert.getId()==programAdvert.getId()){
		    	assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
		    	assertThat(loadedAdvert.getDescription(), equalTo(programAdvert.getDescription()));
		    	assertThat(loadedAdvert.getStudyDuration(), equalTo(programAdvert.getStudyDuration()));
		    	assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
		    	return true;
			}
		}
		return false;
	}

}
