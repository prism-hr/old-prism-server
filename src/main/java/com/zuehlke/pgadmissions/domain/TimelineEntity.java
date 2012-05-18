package com.zuehlke.pgadmissions.domain;

import java.util.Date;

public abstract class TimelineEntity extends DomainObject<Integer> implements Comparable<TimelineEntity> {


	private static final long serialVersionUID = -4978206988461238946L;

	abstract Date getDate();

	@Override
	public int compareTo(TimelineEntity otherTimelineEntity) {
		if(otherTimelineEntity.getDate() == null){
			return -1;
		}
		if(this.getDate() == null){
			return 1;
		}
		return otherTimelineEntity.getDate().compareTo(this.getDate());
	}
}