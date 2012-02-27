package com.zuehlke.pgadmissions.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HibernateFlusher {
	
	@Transactional
	public void flush(){
		//do nothing! The transactional annotation is enough to make Hibernate sync w. db.
		
	}
}
