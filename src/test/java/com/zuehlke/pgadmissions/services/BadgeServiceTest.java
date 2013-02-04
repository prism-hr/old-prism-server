package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		Program program = new ProgramBuilder().id(1).build();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		Badge badge = new BadgeBuilder().id(1).build();
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge));
		EasyMock.replay(badgeDAOMock);
		assertEquals(badge, service.getAllBadges(program).get(0));
	}
	
	@Test
	public void shouldGetReturnDistingClosingDatesForAllBadgesOfProgram() throws ParseException{
		Program program = new ProgramBuilder().id(1).build();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -10);
		Badge badge1 = new BadgeBuilder().id(1).closingDate(null).build();
		Badge badge2 = new BadgeBuilder().id(2).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).build();
		Badge badge3 = new BadgeBuilder().id(3).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).build();
		Badge badge4 = new BadgeBuilder().id(4).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).build();
		Badge badge5 = new BadgeBuilder().id(5).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).build();
		Badge badge6 = new BadgeBuilder().id(6).closingDate(cal.getTime()).build();
		
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge1, badge2, badge3, badge4, badge5, badge6));
		EasyMock.replay(badgeDAOMock);
		assertEquals(1, service.getAllClosingDatesByProgram(program).size());
	}
	
	
	@Test
	public void shouldGetClosingDatesInDecsendingOrder() throws ParseException{
		Program program = new ProgramBuilder().id(1).build();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -10);
		Badge badge1 = new BadgeBuilder().id(1).closingDate(null).build();
		Badge badge2 = new BadgeBuilder().id(2).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/09")).build();
		Badge badge3 = new BadgeBuilder().id(3).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2019/09/09")).build();
		Badge badge4 = new BadgeBuilder().id(4).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2034/09/09")).build();
		Badge badge5 = new BadgeBuilder().id(5).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).build();
		Badge badge6 = new BadgeBuilder().id(6).closingDate(cal.getTime()).build();
		
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge1, badge2, badge3, badge4, badge5, badge6));
		EasyMock.replay(badgeDAOMock);
		
		List<Date> allBadgesByClosingDate = service.getAllClosingDatesByProgram(program);
		assertEquals(4, allBadgesByClosingDate.size());
		assertEquals(badge4.getClosingDate(), allBadgesByClosingDate.get(0));
		assertEquals(badge5.getClosingDate(), allBadgesByClosingDate.get(1));
		assertEquals(badge3.getClosingDate(), allBadgesByClosingDate.get(2));
		assertEquals(badge2.getClosingDate(), allBadgesByClosingDate.get(3));
	}
	
	@Test
	public void shouldGetReturnDistingProjectTitlesForAllBadgesOfProgram() throws ParseException{
		Program program = new ProgramBuilder().id(1).build();
		BadgeDAO badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		BadgeService service = new BadgeService(badgeDAOMock);
		
		Badge badge1 = new BadgeBuilder().id(1).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("same title").build();
		Badge badge2 = new BadgeBuilder().id(2).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("").build();
		Badge badge3 = new BadgeBuilder().id(3).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("same title").build();
		Badge badge4 = new BadgeBuilder().id(4).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("same title").build();
		Badge badge5 = new BadgeBuilder().id(5).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("same title").build();
		Badge badge6 = new BadgeBuilder().id(6).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2030/09/09")).projectTitle("different title1").build();
		Badge badge7 = new BadgeBuilder().id(7).closingDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/09")).projectTitle("different title").build();
		
		EasyMock.expect(badgeDAOMock.getBadgesByProgram(program)).andReturn(Arrays.asList(badge1, badge2, badge3, badge4, badge5, badge6, badge7));
		EasyMock.replay(badgeDAOMock);
		assertEquals(2, service.getAllProjectTitlesByProgram(program).size());
	}
}
