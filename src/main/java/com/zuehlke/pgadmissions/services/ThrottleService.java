package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.ThrottleDAO;
import com.zuehlke.pgadmissions.domain.Throttle;

@Service
@Transactional
public class ThrottleService {

	private ThrottleDAO repository;

	@Autowired
	public ThrottleService(ThrottleDAO repository) {
		this.repository = repository;
	}
	
	public ThrottleService() {
		this(null);
	}
	
	public void updateThrottle(Throttle throttle) {
		if (throttle.getId()==null || throttle.getId()<=0) {
			repository.save(throttle);
		}
		else {
			repository.update(throttle);
		}
	}
	
	public Throttle getThrottle(Integer id) {
		return repository.getById(id);
	}
	
	public Throttle getThrottle() {
		return repository.get();
	}
}
