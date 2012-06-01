package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class TimelineEntityTest {
	@Test
	public void shouldSortBytDate() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		
		MyTimelineEntity tlEntityOne = new MyTimelineEntity();
		tlEntityOne.setId(1);
		tlEntityOne.setDate(format.parse("01 01 2011"));
		
		MyTimelineEntity tlEntityTwo= new MyTimelineEntity();
		tlEntityTwo.setId(2);
		tlEntityTwo.setDate(format.parse("01 10 2011"));
		
		MyTimelineEntity tlEntityThree = new MyTimelineEntity();
		tlEntityThree.setId(3);
		tlEntityThree.setDate(format.parse("01 05 2011"));
	
		
		MyTimelineEntity tlEntityFour = new MyTimelineEntity();
		tlEntityFour.setId(4);
		List<TimelineEntity> timelineEntities = Arrays.asList((TimelineEntity)tlEntityOne, (TimelineEntity)tlEntityTwo,  (TimelineEntity)tlEntityThree, (TimelineEntity)tlEntityFour);
		Collections.sort(timelineEntities);


		assertEquals(tlEntityTwo, timelineEntities.get(0));
		assertEquals(tlEntityThree, timelineEntities.get(1));
		assertEquals(tlEntityOne, timelineEntities.get(2));
		assertEquals(tlEntityFour, timelineEntities.get(3));
	}

	private class MyTimelineEntity extends TimelineEntity{
		private static final long serialVersionUID = 1L;
		private Date date;
		
		@Override
		Date getDate() {			
			return date;
		}

		@Override
		public void setId(Integer id) {
			this.id = id;			
		}

		@Override
		public Integer getId() {			
			return id;
		}

		public void setDate(Date date) {
			this.date = date;
		}
		
	}
}
