package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.utils.DateUtils;

public class ProgramClosingDateTest {

	Program program;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp(){
		program = new ProgramBuilder().code("123").title("title").build();
	}
	
	@Test
	public void shouldAddClosingDateToProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
		Date day = DateUtils.truncateToDay(new Date());
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(day).build();
		program.addClosingDate(closingDate);

		assertThat(program.getClosingDates().size(), is(1));
		assertThat(program.containsClosingDate(day), is(true));
		assertThat(closingDate.getProgram(), equalTo(program));
		assertThat(program.getClosingDate(day), equalTo(closingDate));
		
	}

	@Test
	public void shouldThrowExceptionWhenAddingAnExistingClosingDateToProgram(){
		Date day = DateUtils.truncateToDay(new Date());
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(day).build();
		program.addClosingDate(closingDate);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(containsString("Already Exists"));

		program.addClosingDate(closingDate);
		
	}
	
	@Test
	public void shouldRemoveClosingDateFromProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
		Date day = DateUtils.truncateToDay(new Date());
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).build();
		program.addClosingDate(closingDate);

		ProgramClosingDate sameClosingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).build();
		program.removeClosingDate(sameClosingDate);
		assertThat(program.getClosingDates().size(), is(0));
		assertThat(program.containsClosingDate(day), is(false));
		assertThat(program.getClosingDate(day), nullValue());
	}

	@Test
	public void shouldUpdateClosingDateFromProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
		Date day = DateUtils.truncateToDay(new Date());
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).build();
		program.addClosingDate(closingDate);
		
		ProgramClosingDate updatedClosingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).studyPlaces(5).build();
		program.updateClosingDate(updatedClosingDate);
		assertThat(program.getClosingDates().size(), is(1));
		assertThat(program.containsClosingDate(day), is(true));
		assertThat(program.getClosingDate(day), is(closingDate));
		assertNotSame(closingDate, updatedClosingDate);
		assertThat(closingDate.getClosingDate(), equalTo(updatedClosingDate.getClosingDate()));
		assertThat(closingDate.getStudyPlaces(), equalTo(updatedClosingDate.getStudyPlaces()));
	}
}
