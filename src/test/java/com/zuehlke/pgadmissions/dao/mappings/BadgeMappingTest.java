package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.BadgeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class BadgeMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadBadgeWithProgram() throws Exception {

        Program program = new ProgramBuilder().code("123").title("title").build();
        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();
        
        
        Badge badge = new BadgeBuilder().closingDate(new Date()).program(program).projectTitle("title").build();
        

        sessionFactory.getCurrentSession().save(badge);
        assertNotNull(badge.getId());
        Integer id = badge.getId();
        Badge badgeDetails = (Badge) sessionFactory.getCurrentSession().get(Badge.class, id);

        assertSame(badge, badgeDetails);

        flushAndClearSession();
        badgeDetails = (Badge) sessionFactory.getCurrentSession().get(Badge.class, id);

        assertNotSame(badge, badgeDetails);
        assertEquals(badge.getId(), badgeDetails.getId());
        assertEquals("title", badgeDetails.getProjectTitle());
        assertEquals(program.getId(), badgeDetails.getProgram().getId());
    }
}
