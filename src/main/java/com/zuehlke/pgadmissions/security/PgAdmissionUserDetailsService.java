package com.zuehlke.pgadmissions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
public class PgAdmissionUserDetailsService implements UserDetailsService {

	private final UserDAO userDAO;

	@Autowired
	public PgAdmissionUserDetailsService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		RegisteredUser user = userDAO.getUserByUsername(username);
		if(user == null){
			throw new UsernameNotFoundException(username);
		}
		return user;
	}

}
