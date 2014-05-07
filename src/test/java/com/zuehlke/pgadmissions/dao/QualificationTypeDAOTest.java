package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;

public class QualificationTypeDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldGetNationalityById() {
        QualificationType dom1 = new QualificationTypeBuilder().enabled(true).name("AAAAA").code("AA")
                .build();
        QualificationType dom2 = new QualificationTypeBuilder().enabled(true).name("BBBBB").code("BB")
                .build();

        save(dom1, dom2);
        flushAndClearSession();

        Integer id = dom1.getId();

        QualificationTypeDAO qualificationTitleDAO = new QualificationTypeDAO(sessionFactory);
        QualificationType title = qualificationTitleDAO.getQualificationTypeById(id);

        assertEquals(dom1.getId(), title.getId());
    }

    @Test
    public void shouldGetNationalityByIdOnlyEnabled() {
        BigInteger numberOfCoutnries = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from QUALIFICATION_TYPE WHERE enabled = true").uniqueResult();
        
        QualificationType dom1 = new QualificationTypeBuilder().enabled(false).name("AAAAA").code("AA").build();
        QualificationType dom2 = new QualificationTypeBuilder().enabled(true).name("BBBBB").code("BB").build();

        save(dom1, dom2);
        
        flushAndClearSession();
        
        QualificationTypeDAO qualificationTitleDAO = new QualificationTypeDAO(sessionFactory);
        List<QualificationType> list = qualificationTitleDAO.getAllEnabledQualificationTypes();

        assertEquals(numberOfCoutnries.intValue() + 1, list.size());
    }
}
