package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;

@Service
public class BadgeService {

	private final BadgeDAO badgeDAO;

	BadgeService(){
		this(null);
	}
	
	@Autowired
	public BadgeService(BadgeDAO badgeDAO) {
		this.badgeDAO = badgeDAO;
	
	}

	public List<Badge> getAllBadges(Program program) {
		return badgeDAO.getBadgesByProgram(program);
	}
	
	public List<Date> getAllClosingDatesByProgram(Program program) {
		Date now = new Date();
		List<Badge> badges = badgeDAO.getBadgesByProgram(program);
		List<Date> programClosingDates = new ArrayList<Date>();
		for (Badge badge : badges) {
			Date badgeClosingDate = badge.getClosingDate();
			if(badgeClosingDate != null && !badgeClosingDate.before(now)){
				programClosingDates.add(badge.getClosingDate());
			}
		}
		HashSet<Date> removedDuplicates = new HashSet<Date>(programClosingDates);
		List<Date> dates = new ArrayList<Date>(removedDuplicates);
		Collections.sort(dates);
		Collections.reverse(dates);
		return dates;
	}
	
	
	public List<String> getAllProjectTitlesByProgram(Program program) {
		List<Badge> badges = badgeDAO.getBadgesByProgram(program);
		List<String> programProjectTitles = new ArrayList<String>();
		for (Badge badge : badges) {
			if(!StringUtils.isEmpty(badge.getProjectTitle())){
				programProjectTitles.add(badge.getProjectTitle());
			}
		}
		HashSet<String> removedDuplicates = new HashSet<String>(programProjectTitles);
		List<String> titles = new ArrayList<String>(removedDuplicates);
		Collections.sort(titles);
		return titles;
	}

}
