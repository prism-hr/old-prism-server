package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.AppointmentTimeslot;

@Repository
public class AppointmentTimeslotDAO {

    private final SessionFactory sessionFactory;

    public AppointmentTimeslotDAO() {
        this(null);
    }

    @Autowired
    public AppointmentTimeslotDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public AppointmentTimeslot getTimeslotById(Integer id) {
        return (AppointmentTimeslot) sessionFactory.getCurrentSession().get(AppointmentTimeslot.class, id);
    }

}