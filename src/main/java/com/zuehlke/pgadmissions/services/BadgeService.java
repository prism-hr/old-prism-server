package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;

@Service
@Transactional
public class BadgeService {

	private final BadgeDAO badgeDAO;

	public BadgeService(){
		this(null);
	}
	
	@Autowired
	public BadgeService(BadgeDAO badgeDAO) {
		this.badgeDAO = badgeDAO;
	}

	public void save(Badge badge) {
	    if (badgeDAO.getDuplicateBadges(badge).isEmpty()) {
            badgeDAO.save(badge);
	    }
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
		Date now = new Date();
		List<Badge> badges = badgeDAO.getBadgesByProgram(program);
		List<String> programProjectTitles = new ArrayList<String>();
		for (Badge badge : badges) {
			Date badgeClosingDate = badge.getClosingDate();
			if(badgeClosingDate != null && badgeClosingDate.before(now)){
				continue;
			}
			if(!StringUtils.isBlank(badge.getProjectTitle())){
				programProjectTitles.add(badge.getProjectTitle());
			}
		}
		HashSet<String> removedDuplicates = new HashSet<String>(programProjectTitles);
		List<String> titles = new ArrayList<String>(removedDuplicates);
		Collections.sort(titles);
		return titles;
	}
	
	public List<String> getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(Program program, String searchTerm) {
	    LinkedHashSet<String> uniqueProjectTitles = new LinkedHashSet<String>();
	    for (Badge badge : badgeDAO.getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(program, searchTerm)) {
	        uniqueProjectTitles.add(badge.getProjectTitle());
	    }
	    return new ArrayList<String>(uniqueProjectTitles);
    }
}
