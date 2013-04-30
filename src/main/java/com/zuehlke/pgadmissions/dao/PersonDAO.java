package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Person;

@Repository
@SuppressWarnings("unchecked")
public class PersonDAO {

	private final SessionFactory sessionFactory;

	public PersonDAO() {
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

	public List<Person> getAllPersons() {
		return (List<Person>) sessionFactory.getCurrentSession().createCriteria(Person.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public void delete(Person person) {
		sessionFactory.getCurrentSession().delete(person);
	}
}
