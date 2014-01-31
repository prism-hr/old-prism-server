package com.zuehlke.pgadmissions.dao;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgramClosingDate;

@Repository
public class ProgramClosingDateDAO {
    
   private final SessionFactory sessionFactory;
   
   public ProgramClosingDateDAO() {
       this(null);
   }
   
   @Autowired
   public ProgramClosingDateDAO(SessionFactory sessionFactory) {
       this.sessionFactory = sessionFactory;
   }
   
   public ProgramClosingDate getById(final Integer id) {
       return (ProgramClosingDate) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
           .add(Restrictions.eq("id", id)).uniqueResult();
   }
   
   public ProgramClosingDate getByDate(final Date date) {
       return (ProgramClosingDate) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
               .add(Restrictions.eq("closingDate", date)).uniqueResult();
   }

   public void updateClosingDate(ProgramClosingDate closingDate) {
       sessionFactory.getCurrentSession().update(closingDate);
   }
   
   public void deleteClosingDate(ProgramClosingDate closingDate) {
       sessionFactory.getCurrentSession().delete(closingDate);
   }
    
}
