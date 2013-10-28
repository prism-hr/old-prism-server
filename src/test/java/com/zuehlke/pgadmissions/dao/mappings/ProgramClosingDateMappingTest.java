package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class ProgramClosingDateMappingTest extends AutomaticRollbackTestCase {

	private Program program;

	@Override
	public void setup(){
	    super.setup();
		program = storeAndLoadProgram("123CD", "titleCD");
	}
	
    @Test
    public void shouldSaveClosingDateWithProgram() throws Exception {
        ProgramClosingDate closingDate = createClosingDate(getDate("01-Jun-2013"), 10);
        Assert.assertNull(closingDate.getId());
        
        Integer closingDateId = addClosingDateToProgram(closingDate, program);
        
        assertNotNull(closingDateId);
        assertSame(closingDate.getProgram(), program);
        
        ProgramClosingDate closingDateDetails = load(ProgramClosingDate.class, closingDateId);
        assertSameClosingDates(closingDateDetails, closingDate);
    }

    @Test
    public void shouldLoadClosingDateWithProgram() throws Exception {
    	ProgramClosingDate closingDate = createClosingDate(getDate("02-Jun-2013"), 10);
    	
    	addClosingDateToProgram(closingDate, program);
    	
    	program = load(Program.class, program.getId());
    	
    	assertEquals(1, program.getClosingDates().size());
    	ProgramClosingDate closingDateDetails = program.getClosingDate(closingDate.getClosingDate());
    	assertSameClosingDates(closingDateDetails, closingDate);
    }

    @Test
    public void shouldDeleteClosingDateWithProgram() throws Exception {
    	ProgramClosingDate closingDate = createClosingDate(getDate("03-Jun-2013"), 10);
    	Integer closingDateId = addClosingDateToProgram(closingDate, program);

    	delete(program);
    	flushAndClearSession();
    	
    	program = load(Program.class, program.getId());
    	assertNull(program);

    	ProgramClosingDate closingDateDetails = load(ProgramClosingDate.class, closingDateId);
    	assertNull(closingDateDetails);
    }

    @Test
    public void shouldRemoveClosingDateFromProgram() throws Exception {
    	ProgramClosingDate closingDate = createClosingDate(getDate("04-Jun-2013"), 10);
    	Integer closingDateId = addClosingDateToProgram(closingDate, program);
    	
    	removeClosingDateFromProgram(closingDate, program);
    	assertFalse(program.getClosingDates().contains(closingDate));

    	flushAndClearSession();
    	
    	ProgramClosingDate closingDateDetails = load(ProgramClosingDate.class, closingDateId);
    	assertNull(closingDateDetails);
    }

    
    @Test
    public void shouldUpdateClosingDateOfProgram() throws Exception {
    	Date day = getDate("05-Jun-2013");
		ProgramClosingDate closingDate = createClosingDate(day, 10);
		addClosingDateToProgram(closingDate, program);
		
    	program = load(Program.class, program.getId());
    	
    	Date updatedDay = getDate("06-Jun-2013");
    	assertFalse(program.containsClosingDate(updatedDay));
    	
    	closingDate = program.getClosingDate(day);
    	closingDate.setClosingDate(updatedDay);
    	program.updateClosingDate(closingDate);
    	flushAndClearSession();
    	
    	program = load(Program.class, program.getId());
    	ProgramClosingDate closingDateDetails = program.getClosingDate(updatedDay);
    	assertSameClosingDates(closingDateDetails, closingDate);
    }
	

	private void removeClosingDateFromProgram(ProgramClosingDate closingDate, Program program) {
		program.removeClosingDate(closingDate.getId());
    	update(program);
	}

	private Integer addClosingDateToProgram(ProgramClosingDate closingDate, Program program) {
		program.addClosingDate(closingDate);
    	update(program);
    	
    	flushAndClearSession();
		return closingDate.getId();
	}
	
	private void assertSameClosingDates(ProgramClosingDate closingDateDetails,
			ProgramClosingDate closingDate) {
		assertEquals(closingDateDetails.getId(), closingDate.getId());
    	assertEquals(0,closingDateDetails.getClosingDate().compareTo(closingDate.getClosingDate()) );
    	assertEquals(closingDateDetails.getStudyPlaces(), closingDate.getStudyPlaces());
    	assertEquals(closingDateDetails.getProgram().getId(), closingDate.getProgram().getId());
	}

    private Date getDate(String strDate) {
    	try {
			return DateUtils.parseDate(strDate, new String[] {"dd-MMM-yyyy"});
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}

	private ProgramClosingDate createClosingDate(Date date, Integer places) {
		ProgramClosingDate closingDate = new ProgramClosingDate();
        closingDate.setClosingDate(date);
        closingDate.setStudyPlaces(places);
		return closingDate;
	}

	private Program storeAndLoadProgram(String code, String title) {
		Program program = new ProgramBuilder().code(code).title(title).build();
        save(program);
        flushAndClearSession();
		return program;
	}

	@SuppressWarnings("unchecked")
	protected <T> T load(Class<T> className, Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(className, id);
	}
	
	protected void update(Object... domainObjects) {
		for (Object domainObject : domainObjects) {
			sessionFactory.getCurrentSession().saveOrUpdate(domainObject);
		}
	}
	
	private void delete(Program program) {
		sessionFactory.getCurrentSession().delete(program);
	}
}
