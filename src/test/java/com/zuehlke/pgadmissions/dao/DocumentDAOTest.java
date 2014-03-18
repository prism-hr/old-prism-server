package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class DocumentDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        Document document = new DocumentBuilder().id(1).build();
        DocumentDAO documentDAO = new DocumentDAO();
        documentDAO.save(document);
    }

    @Test
    public void shouldSaveDocument() {
        Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).build();
        DocumentDAO dao = new DocumentDAO(sessionFactory);
        dao.save(document);

        flushAndClearSession();

        Document reloadDocument = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
        assertEquals(document.getId(), reloadDocument.getId());
    }

    @Test
    public void shouldGetDocumentById() {
        Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).build();
        DocumentDAO dao = new DocumentDAO(sessionFactory);
        dao.save(document);

        flushAndClearSession();

        Document reloadDocument = dao.getDocumentbyId(document.getId());
        assertEquals(document.getId(), reloadDocument.getId());
    }

    @Before
    public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();
    }
}
