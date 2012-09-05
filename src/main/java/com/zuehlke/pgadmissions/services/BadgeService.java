package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;

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
	
	public Set<Date> getAllClosingDatesByProgram(Program program) {
		List<Badge> badges = badgeDAO.getBadgesByProgram(program);
		List<Date> programClosingDates = new ArrayList<Date>();
		for (Badge badge : badges) {
			if(badge.getClosingDate() != null){
				programClosingDates.add(badge.getClosingDate());
			}
		}
		return new HashSet<Date>(programClosingDates);
	}
	
	public Set<String> getAllProjectTitlesByProgram(Program program) {
		List<Badge> badges = badgeDAO.getBadgesByProgram(program);
		List<String> programProject = new ArrayList<String>();
		for (Badge badge : badges) {
			if(!StringUtils.isEmpty(badge.getProjectTitle())){
				programProject.add(badge.getProjectTitle());
			}
		}
		return new HashSet<String>(programProject);
	}
	
}
