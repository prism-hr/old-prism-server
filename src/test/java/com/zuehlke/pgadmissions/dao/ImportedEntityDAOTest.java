package com.zuehlke.pgadmissions.dao;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Disability;

public class ImportedEntityDAOTest extends AutomaticRollbackTestCase {

    private ImportedEntityDAO importedEntityDAO;

    @Test(expected = ConstraintViolationException.class)
    public void shouldFailToInsertDuplicateName() {
        Disability disability = new Disability();
        disability.setCode("dupa");
        disability.setName("nazwa");
        
        importedEntityDAO.save(disability);

        Disability disability1 = new Disability();
        disability1.setCode("dupa2");
        disability1.setName("nazwa");
        importedEntityDAO.save(disability1);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldFailToInsertDuplicateCode() {
        Disability disability = new Disability();
        disability.setCode("dupa");
        disability.setName("nazwa");
        
        importedEntityDAO.save(disability);
        
        Disability disability1 = new Disability();
        disability1.setCode("dupa");
        disability1.setName("nazwa2");
        importedEntityDAO.save(disability1);
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void shouldFailToUpdateDuplicateCode() {
        Disability disability = new Disability();
        disability.setCode("dupa");
        disability.setName("nazwa");
        
        importedEntityDAO.save(disability);
        
        Disability disability1 = new Disability();
        disability1.setCode("dupa2");
        disability1.setName("nazwa2");
        importedEntityDAO.save(disability1);
        
        
        Disability returned = importedEntityDAO.getById(disability1.getId());
        returned.setCode("dupa");
        importedEntityDAO.update(returned);
        
        flushAndClearSession();
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void shouldFailToUpdateDuplicateName() {
        Disability disability = new Disability();
        disability.setCode("dupa");
        disability.setName("nazwa");
        
        importedEntityDAO.save(disability);
        
        Disability disability1 = new Disability();
        disability1.setCode("dupa2");
        disability1.setName("nazwa2");
        importedEntityDAO.save(disability1);
        
        
        Disability returned = importedEntityDAO.getById(disability1.getId());
        returned.setName("nazwa");
        importedEntityDAO.update(returned);
        
        flushAndClearSession();
    }


    @Before
    public void prepare() {
        importedEntityDAO = new ImportedEntityDAO(sessionFactory);
    }

}
