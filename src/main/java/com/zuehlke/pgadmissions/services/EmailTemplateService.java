package com.zuehlke.pgadmissions.services;

import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EmailTemplateDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;

@Service("EmailTemplateService")
@Transactional
public class EmailTemplateService {
	
    @Autowired
	private EmailTemplateDAO emailTemplateDAO;

	public EmailTemplate getDefaultEmailTemplate(EmailTemplateName name) {
		return emailTemplateDAO.getDefaultByName(name);
	}
	
	public EmailTemplate getLastestEmailTemplate(EmailTemplateName name) {
		return emailTemplateDAO.getLatestByName(name);
	}

	public EmailTemplate getEmailTemplateVersion(EmailTemplateName name, Date version) {
		return emailTemplateDAO.getByNameAndVersion(name, version);
	}
	
	public List<EmailTemplate> getAllEmailTemplates() {
		return emailTemplateDAO.getAll();
	}
	
	public List<EmailTemplate> getEmailTemplates(EmailTemplateName name) {
		return emailTemplateDAO.getByName(name);
	}
	
	public EmailTemplate getActiveEmailTemplate(EmailTemplateName name) {
		return emailTemplateDAO.getActiveByName(name);
	}
	
	public void activateEmailTemplate(EmailTemplate template) throws EmailTemplateException {
		activateEmailTemplate(template.getName(), template.getId());
	}
	
	public void activateEmailTemplate(EmailTemplateName name, Long idToActivate) throws EmailTemplateException {
		EmailTemplate toActivate = emailTemplateDAO.getById(idToActivate);
		if (toActivate == null) {
			throw new EmailTemplateException("Could not find template with id: \""+idToActivate+"\"");
		}
		if (!isTrue(toActivate.getActive())) {
			EmailTemplate active = emailTemplateDAO.getActiveByName(name);
			if (active == null) {
				throw new EmailTemplateException("There is no active template for template name: \""+name.displayValue()+"\"");
			}
			active.setActive(false);
			toActivate.setActive(true);
			emailTemplateDAO.save(active);
			emailTemplateDAO.save(toActivate);
		}
	}
	
	public Map<Long, String> getEmailTemplateVersions(EmailTemplateName name) {
		Map<Long, Date> versions = emailTemplateDAO.getVersionsByName(name);
		
		Map<Long, String> result = new HashMap<Long, String>();
		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy - HH:mm:ss");
		for (Map.Entry<Long, Date> idDate : versions.entrySet()) {
			if (idDate.getValue() == null) {
				result.put(idDate.getKey(), "original template");
			}
			else {
				result.put(idDate.getKey(), formatter.format(idDate.getValue()));
			}
		}
		return result;
	}
	
	public void saveNewEmailTemplate(EmailTemplate template) {
		template.setVersion(new Date(Calendar.getInstance().getTimeInMillis()));
		emailTemplateDAO.save(template);
	}

	public EmailTemplate getEmailTemplate(Long id) {
		return emailTemplateDAO.getById(id);
	}

	public void deleteTemplateVersion(Long id) throws EmailTemplateException {
		deleteTemplateVersion(emailTemplateDAO.getById(id));
	}
	
	public void deleteTemplateVersion(EmailTemplate template) throws EmailTemplateException{
		if (isTrue(template.getActive())) {
			throw new EmailTemplateException("Cannot remove active template!");
		}
		if (template.getVersion()==null) {
			throw new EmailTemplateException("Cannot remove original template!");
		}
		emailTemplateDAO.remove(template);
	}

	public EmailTemplate saveNewEmailTemplate(EmailTemplateName templateName, String content, String subject) {
		EmailTemplate template = new EmailTemplate();
		template.setContent(content);
		template.setName(templateName);
		template.setSubject(subject);
		saveNewEmailTemplate(template);
		return template;
	}

	protected boolean newSubjectCompliesToDefaultSubject(String defaultSubject, String newSubject) {
	    Pattern pattern = Pattern.compile("\\%(\\d+)\\$\\s");
	    Matcher newMatcher = pattern.matcher(newSubject);
	    Matcher defaultMatcher = pattern.matcher(newSubject);
	    if (newMatcher.groupCount()!=defaultMatcher.groupCount()){
	        return false;
	    }
	        
        return true;
    }

	@Deprecated
	public String processTemplateContent(String templateContent) {
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");  
	    Matcher matcher = pattern.matcher(templateContent);
	    String result = templateContent;
	    while (matcher.find()) {
	    	String ftlVariable = matcher.group(1);
	    	String keyWord = ftlVariable.replace("?", "\\?");
	    	int indexOfQuestionMark = ftlVariable.indexOf("?");
	    	String newString = indexOfQuestionMark != -1 ?ftlVariable.substring(0, indexOfQuestionMark) : ftlVariable;
	    	result = result.replaceAll("\\$\\{"+keyWord+"\\}", newString);
	    }
	    return result;
	}

    public String getSubjectForTemplate(EmailTemplateName templateName) {
        EmailTemplate activeEmailTemplate = getActiveEmailTemplate(templateName);
        if(activeEmailTemplate != null){
            return activeEmailTemplate.getSubject();
        }
        throw new RuntimeException("No email template found: " + templateName);
    }
    
}