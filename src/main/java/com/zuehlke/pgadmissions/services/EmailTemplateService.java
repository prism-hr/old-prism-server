package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EmailTemplateDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Service("EmailTemplateService")
@Transactional
public class EmailTemplateService {
	
	private final EmailTemplateDAO emailTemplateDAO;

	
	public EmailTemplateService() {
		this(null);
	}
	
	@Autowired
	public EmailTemplateService(EmailTemplateDAO emailTemplateDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
	}
	
	public EmailTemplate getEmailTemplateByName(EmailTemplateName name) {
		return emailTemplateDAO.getByName(name);
	}
	
	public List<EmailTemplate> getAllEmailTemplates() {
		return emailTemplateDAO.getAll();
	}
	
	public void updateEmailTemplate(EmailTemplate template) {
		EmailTemplate existing = emailTemplateDAO.getByName(template.getName());
		existing.setContent(template.getContent());
		emailTemplateDAO.save(existing);
	}
}
