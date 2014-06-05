package com.zuehlke.pgadmissions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.User;

@Service
public class PgAdmissionUserDetailsService implements UserDetailsService {

	private final UserDAO userDAO;

	public PgAdmissionUserDetailsService() {
	    this(null);
	}
	
	@Autowired
	public PgAdmissionUserDetailsService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDAO.getUserByEmail(username);
		if(user == null){
			throw new UsernameNotFoundException(username);
		}
		return user;
	}

}
