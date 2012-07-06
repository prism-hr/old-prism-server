package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Person;

@Repository
public class PersonDAO {

	private final SessionFactory sessionFactory;

	PersonDAO() {
		this(null);
	}

	@Autowired
	public PersonDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Person getPersonWithId(Integer id) {
		return (Person) sessionFactory.getCurrentSession().get(Person.class, id);
	}

	public void save(Person person) {
		sessionFactory.getCurrentSession().saveOrUpdate(person);
	}

	@SuppressWarnings("unchecked")
	public List<Person> getAllPersons() {
		return (List<Person>) sessionFactory.getCurrentSession().createCriteria(Person.class).list();
	}

	public void delete(Person person) {
		sessionFactory.getCurrentSession().delete(person);

	}

}
