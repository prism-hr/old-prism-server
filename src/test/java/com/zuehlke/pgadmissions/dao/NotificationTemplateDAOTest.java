package com.zuehlke.pgadmissions.dao;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;

public class NotificationTemplateDAOTest extends AutomaticRollbackTestCase {

    private NotificationTemplateDAO dao;

    @Test
    public void defaultEmailTemnplateShouldHaveNullVersion() {
        NotificationTemplate result = dao.getById(PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST);
        assertNotNull(result);
        assertNotNull(result.getVersion());
    }

    @Override
    public void setup() {
        super.setup();
        dao = new NotificationTemplateDAO(sessionFactory);
    }

}
