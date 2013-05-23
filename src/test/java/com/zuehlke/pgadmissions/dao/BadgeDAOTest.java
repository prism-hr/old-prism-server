package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.BadgeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class BadgeDAOTest extends AutomaticRollbackTestCase {

    private BadgeDAO badgeDAO;
    private ProgramDAO programDAO;
    private Program program;

    @Before
    public void prepare() {
        badgeDAO = new BadgeDAO(sessionFactory);
        programDAO = new ProgramDAO(sessionFactory); 
        program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);
        flushAndClearSession();
    }
    
    @Test
    public void shouldReturnNextClosingDateForProgram() {
        DateTime closingDates = new DateTime(2013, 05, 20, 00, 00);
        Badge badge1 = new BadgeBuilder()
            .closingDate(closingDates.minusMonths(1).toDate()).build();
        Badge badge2 = new BadgeBuilder()
            .closingDate(closingDates.plusMonths(1).toDate()).build();
        Badge badge3 = new BadgeBuilder()
            .closingDate(closingDates.plusMonths(2).toDate()).build();
        Program program = new ProgramBuilder().code("code").build();
        badge1.setProgram(program);
        badge2.setProgram(program);
        
        save(badge1, badge2, badge3, program);
        flushAndClearSession();
        
        Date result = badgeDAO.getNextClosingDateForProgram(program, closingDates.toDate());
        
        Assert.assertNotNull(result);
        
        Assert.assertEquals(0, result.compareTo(badge2.getClosingDate()));
    }
    
    @Test
    public void shouldReturnNullIfThereIsNoClosingDateAvailableForProgram() {
        DateTime closingDates = new DateTime(2013, 05, 20, 00, 00);
        Badge badge1 = new BadgeBuilder()
        .closingDate(closingDates.minusMonths(1).toDate()).build();
        Badge badge2 = new BadgeBuilder()
        .closingDate(closingDates.plusMonths(1).toDate()).build();
        Badge badge3 = new BadgeBuilder()
        .closingDate(closingDates.plusMonths(2).toDate()).build();
        Program program = new ProgramBuilder().code("code").build();
        badge1.setProgram(program);
        badge2.setProgram(program);
        
        save(badge1, badge2, badge3, program);
        flushAndClearSession();
        
        Date result = badgeDAO.getNextClosingDateForProgram(program, closingDates.plusMonths(3).toDate());
        
        Assert.assertNull(result);
    }
    
    @Test
    public void shouldReturnNullIfProgramHasNoClosingDates() {
        DateTime closingDates = new DateTime(2013, 05, 20, 00, 00);
        Program program = new ProgramBuilder().code("code").build();
        
        save(program);
        flushAndClearSession();
        
        Date result = badgeDAO.getNextClosingDateForProgram(program, closingDates.toDate());
        
        Assert.assertNull(result);
        
    }

    @Test
    public void shouldSaveAndLoadBadgeByProgram() {
        Badge badge = new BadgeBuilder().closingDate(new Date()).program(program).projectTitle("title").build();

        assertNull(badge.getId());

        badgeDAO.save(badge);

        assertNotNull(badge.getId());
        List<Badge> badgesByProgram = badgeDAO.getBadgesByProgram(program);
        assertSame(badge, badgesByProgram.get(0));

        flushAndClearSession();

        badgesByProgram = badgeDAO.getBadgesByProgram(program);
        assertNotSame(badge, badgesByProgram.get(0));
        assertEquals(badge.getId(), badgesByProgram.get(0).getId());
    }
    
    @Test
    public void shouldReturnBadges() {
        Badge badge = new BadgeBuilder().closingDate(new Date()).program(program).projectTitle("title").build();
        save(badge);
        flushAndClearSession();
        
        Program program = programDAO.getProgramByCode("doesntexist");
        assertEquals(1, program.getBadges().size());
        assertEquals("title", program.getBadges().get(0).getProjectTitle());
    }
}
