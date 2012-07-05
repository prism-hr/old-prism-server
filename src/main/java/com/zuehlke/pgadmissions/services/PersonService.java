package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

@Service
public class PersonService {

	
	private final PersonDAO personDAO;

	PersonService() {
		this(null);
	}

	@Autowired
	public PersonService(PersonDAO registryUserDAO) {
		this.personDAO = registryUserDAO;

	}


	public Person getRegistryUserWithId(Integer id) {
		return personDAO.getPersonWithId(id);
	}
	
	
	public List<Person> getAllRegistryUsers() {
		List<Person> allPersons = personDAO.getAllPersons();
		List<Person> allRegistryUsers = new ArrayList<Person>();
		for (Person person : allPersons) {
			if(!(person instanceof SuggestedSupervisor)){
				allRegistryUsers.add(person);
			}
		}
		return allRegistryUsers;
	}
	
	@Transactional
	public void save(Person registryUser) {
		personDAO.save(registryUser);
	}
}
