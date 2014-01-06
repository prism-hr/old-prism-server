package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.domain.Person;

@Service
public class PersonService {

    private final PersonDAO personDAO;
    
    public PersonService() {
        this(null);
    }
    
    @Autowired
    public PersonService(PersonDAO durationDAO) {
        this.personDAO = durationDAO;
    }

    @Transactional
    public Person getPersonWithId(Integer id) {
        return personDAO.getPersonWithId(id);
    }
}
