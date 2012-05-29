package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.RegistryUser;

@Service
public class RegistryUserService {

	
	private final RegistryUserDAO registryUserDAO;

	RegistryUserService() {
		this(null);
	}

	@Autowired
	public RegistryUserService(RegistryUserDAO registryUserDAO) {
		this.registryUserDAO = registryUserDAO;

	}


	public RegistryUser getRegistryUserWithId(Integer id) {
		return registryUserDAO.getRegistryUserWithId(id);
	}
	
	
	public List<RegistryUser> getAllRegistryUsers() {
		return registryUserDAO.getAllRegistryUsers();
	}
	
	@Transactional
	public void save(RegistryUser registryUser) {
		registryUserDAO.save(registryUser);
	}
}
