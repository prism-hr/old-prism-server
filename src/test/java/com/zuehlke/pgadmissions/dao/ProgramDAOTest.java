package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;


import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class ProgramDAOTest extends AutomaticRollbackTestCase{

	
		@Test(expected=NullPointerException.class)
		public void shouldThrowNullPointerException(){
			ProgramDAO programDAO = new ProgramDAO();
			programDAO.getAllPrograms();
		}

		@Test
		public void shouldGetAllPrograms() {
			deleteTestData();
			
			Program program1 = new ProgramBuilder().id(1).code("code1").description("blahblab").title("another title").toProgram();
			Program program2= new ProgramBuilder().id(1).code("code2").description("blahblab").title("another title").toProgram();
			sessionFactory.getCurrentSession().save(program1);
			sessionFactory.getCurrentSession().save(program2);
			flushAndClearSession();
			ProgramDAO programDAO = new ProgramDAO(sessionFactory);
			Assert.assertEquals(2, programDAO.getAllPrograms().size());
		}

		private void deleteTestData() {
			sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from NATIONALITY").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from LANGUAGE_PROFICIENCY").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from TELEPHONE").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from SUPERVISOR").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from PROGRAM_APPROVER_LINK").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from PROGRAM_REVIEWER_LINK").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from PROGRAM_ADMINISTRATOR_LINK").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_REVIEW").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_PROGRAMME_DETAIL").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ADDRESS").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_REFEREE").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_PERSONAL_DETAIL").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from REGISTERED_USER").executeUpdate();

			sessionFactory.getCurrentSession().createSQLQuery("delete from PROGRAM_ADMINISTRATOR_LINK").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from PROJECT").executeUpdate();
			sessionFactory.getCurrentSession().createSQLQuery("delete from PROGRAM").executeUpdate();
		}
		
		@Test
		public void shouldGetProgramById() {
			Program program = new ProgramBuilder().id(1).code("code1").description("blahblab").title("another title").toProgram();
			
			sessionFactory.getCurrentSession().save(program);
			flushAndClearSession();
			
			ProgramDAO programDAO = new ProgramDAO(sessionFactory);
			assertEquals(program, programDAO.getProgramById(program.getId()));
		
		}
		
		@Test
		public void shouldSaveProgram() {
			Program program = new ProgramBuilder().code("code1").description("blahblab").title("another title").toProgram();
			sessionFactory.getCurrentSession().save(program);
			flushAndClearSession();
			
			ProgramDAO programDAO = new ProgramDAO(sessionFactory);
			programDAO.save(program);
			Assert.assertNotNull(program.getId());
		}
		
}
