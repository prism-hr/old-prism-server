package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.BadgeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class BadgeServiceTest {

	
	
	@Test
	public void shouldGetBadgesFromDAO(){
		Program program = new ProgramBuilder().id(1).toProgram();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		Badge badge = new BadgeBuilder().id(1).toBadge();
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge));
		EasyMock.replay(badgeDAOMock);
		
		assertEquals(badge, service.getAllBadges(program).get(0));
		
	}
	
	@Test
	public void shouldGetReturnDistingClosingDatesForAllBadgesOfProgram() throws ParseException{
		Program program = new ProgramBuilder().id(1).toProgram();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);

		Badge badge1 = new BadgeBuilder().id(1).closingDate(null).toBadge();
		Badge badge2 = new BadgeBuilder().id(2).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).toBadge();
		Badge badge3 = new BadgeBuilder().id(3).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).toBadge();
		Badge badge4 = new BadgeBuilder().id(4).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).toBadge();
		Badge badge5 = new BadgeBuilder().id(5).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).toBadge();
		
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge1, badge2, badge3, badge4, badge5));
		EasyMock.replay(badgeDAOMock);
		
		assertEquals(1, service.getAllClosingDatesByProgram(program).size());
		
	}
	
	@Test
	public void shouldGetReturnDistingProjectTitlesForAllBadgesOfProgram() throws ParseException{
		Program program = new ProgramBuilder().id(1).toProgram();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		
		Badge badge1 = new BadgeBuilder().id(1).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("same title").toBadge();
		Badge badge2 = new BadgeBuilder().id(2).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("").toBadge();
		Badge badge3 = new BadgeBuilder().id(3).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("same title").toBadge();
		Badge badge4 = new BadgeBuilder().id(4).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("same title").toBadge();
		Badge badge5 = new BadgeBuilder().id(5).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("same title").toBadge();
		
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge1, badge2, badge3, badge4, badge5));
		EasyMock.replay(badgeDAOMock);
		
		assertEquals(1, service.getAllProjectTitlesByProgram(program).size());
		
	}
}
