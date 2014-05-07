package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

public class NotificationTemplateDAOTest extends AutomaticRollbackTestCase {

    private NotificationTemplateDAO dao;

    @Test
    public void defaultEmailTemnplateShouldHaveNullVersion() {
        NotificationTemplate result = dao.getById(NotificationTemplateId.APPLICATION_PROVIDE_REFERENCE_REQUEST);
        assertNotNull(result);
        assertNotNull(result.getVersion());
    }

    @Override
    public void setup() {
        super.setup();
        dao = new NotificationTemplateDAO(sessionFactory);
    }

}
