package com.zuehlke.pgadmissions.domain;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;

public class ProgramClosingDateTest {

	Program program;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Date day;

	@Before
	public void setUp(){
		day = com.zuehlke.pgadmissions.utils.DateUtils.truncateToDay(new Date());
		program = new ProgramBuilder().code("123").title("title").build();
	}
	
	@Test
	public void shouldAddClosingDateToProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(day).build();
		program.addClosingDate(closingDate);

		assertThat(program.getClosingDates().size(), is(1));
		assertThat(program.containsClosingDate(day), is(true));
		assertThat(closingDate.getProgram(), equalTo(program));
		assertThat(program.getClosingDate(day), equalTo(closingDate));
		
	}

	@Test
	public void shouldThrowExceptionWhenAddingAnExistingClosingDateToProgram(){
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(day).build();
		program.addClosingDate(closingDate);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(containsString("Already Exists"));

		program.addClosingDate(closingDate);
		
	}
	
	@Test
	public void shouldRemoveClosingDateFromProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).build();
		program.addClosingDate(closingDate);

		ProgramClosingDate sameClosingDate = new ProgramClosingDateBuilder().id(5).closingDate(day).build();
		program.removeClosingDate(sameClosingDate.getId());
		assertThat(program.getClosingDates().size(), is(0));
		assertThat(program.containsClosingDate(day), is(false));
		assertThat(program.getClosingDate(day), nullValue());
	}

	@Test
	public void shouldUpdateClosingDateFromProgram(){
		assertThat(program.getClosingDates().size(), is(0));
		
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
	
	@Test
	public void shouldAllowUpdateSameDateUpdateValues(){
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(1).closingDate(day).build();
		program.addClosingDate(closingDate);

		ProgramClosingDate updatedDate = new ProgramClosingDateBuilder().id(1).closingDate(day).studyPlaces(3).build();
		program.updateClosingDate(updatedDate);
		
		assertThat(closingDate.getStudyPlaces(), equalTo(updatedDate.getStudyPlaces()));
	}

	@Test
	public void shouldAllowUpdateSameClosingDateToANonExistingDate(){
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(1).closingDate(day).build();
		program.addClosingDate(closingDate);
		
		ProgramClosingDate updatedDate = new ProgramClosingDateBuilder().id(1).closingDate(DateUtils.addDays(day, 1)).studyPlaces(3).build();
		program.updateClosingDate(updatedDate);
		
		assertThat(closingDate.getClosingDate(), equalTo(updatedDate.getClosingDate()));
	}
	
	@Test
	public void shouldThrowExceptionWhenEditingClosingDateToAnExistingDate(){
		ProgramClosingDate closingDate = new ProgramClosingDateBuilder().id(1).closingDate(day).build();
		ProgramClosingDate anotherClosingDate = new ProgramClosingDateBuilder().id(2).closingDate(DateUtils.addDays(day, 2)).build();
		program.addClosingDate(closingDate);
		program.addClosingDate(anotherClosingDate);
		ProgramClosingDate updatingDate = new ProgramClosingDateBuilder().id(1).closingDate(DateUtils.addDays(day, 2)).build();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(containsString("Already Exists"));

		program.updateClosingDate(updatingDate);
		
	}
}
