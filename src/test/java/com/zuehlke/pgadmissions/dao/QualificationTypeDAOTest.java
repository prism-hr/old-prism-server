package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;

public class QualificationTypeDAOTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldGetNationalityById() {
		QualificationType dom1 = new QualificationTypeBuilder().enabled(true).name("AAAAA").toQualificationTitle();
		QualificationType dom2 = new QualificationTypeBuilder().enabled(true).name("BBBBB").toQualificationTitle();
		
		save(dom1, dom2);
		flushAndClearSession();
		
		Integer id = dom1.getId();
		
		QualificationTypeDAO qualificationTitleDAO = new QualificationTypeDAO(sessionFactory);
		QualificationType title = qualificationTitleDAO.getQualificationTypeById(id);
		
		assertEquals(dom1, title);
	}
}
