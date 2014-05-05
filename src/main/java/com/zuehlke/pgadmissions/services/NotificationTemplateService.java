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

import com.zuehlke.pgadmissions.dao.NotificationTemplateDAO;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.exceptions.NotificationTemplateException;

@Transactional
public class NotificationTemplateService {
	
    @Autowired
	private NotificationTemplateDAO notificationTemplateDAO;

	public NotificationTemplate getDefaultEmailTemplate(NotificationTemplateId name) {
		return notificationTemplateDAO.getDefaultByName(name);
	}
	
	public NotificationTemplate getLastestEmailTemplate(NotificationTemplateId name) {
		return notificationTemplateDAO.getLatestByName(name);
	}

	public NotificationTemplate getEmailTemplateVersion(NotificationTemplateId name, Date version) {
		return notificationTemplateDAO.getByNameAndVersion(name, version);
	}
	
	public List<NotificationTemplate> getAllEmailTemplates() {
		return notificationTemplateDAO.getAll();
	}
	
	public List<NotificationTemplate> getEmailTemplates(NotificationTemplateId name) {
		return notificationTemplateDAO.getByName(name);
	}
	
	public NotificationTemplate getActiveEmailTemplate(NotificationTemplateId name) {
		return notificationTemplateDAO.getActiveByName(name);
	}
	
	public void activateEmailTemplate(NotificationTemplate template) throws NotificationTemplateException {
		activateEmailTemplate(template.getName(), template.getId());
	}
	
	public void activateEmailTemplate(NotificationTemplateId name, Long idToActivate) throws NotificationTemplateException {
		NotificationTemplate toActivate = notificationTemplateDAO.getById(idToActivate);
		if (toActivate == null) {
			throw new NotificationTemplateException("Could not find template with id: \""+idToActivate+"\"");
		}
		if (!isTrue(toActivate.getActive())) {
			NotificationTemplate active = notificationTemplateDAO.getActiveByName(name);
			if (active == null) {
				throw new NotificationTemplateException("There is no active template for template name: \""+name.displayValue()+"\"");
			}
			active.setActive(false);
			toActivate.setActive(true);
			notificationTemplateDAO.save(active);
			notificationTemplateDAO.save(toActivate);
		}
	}
	
	public Map<Long, String> getEmailTemplateVersions(NotificationTemplateId name) {
		Map<Long, Date> versions = notificationTemplateDAO.getVersionsByName(name);
		
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
	
	public void saveNewEmailTemplate(NotificationTemplate template) {
		template.setVersion(new Date(Calendar.getInstance().getTimeInMillis()));
		notificationTemplateDAO.save(template);
	}

	public NotificationTemplate getEmailTemplate(Long id) {
		return notificationTemplateDAO.getById(id);
	}

	public void deleteTemplateVersion(Long id) throws NotificationTemplateException {
		deleteTemplateVersion(notificationTemplateDAO.getById(id));
	}
	
	public void deleteTemplateVersion(NotificationTemplate template) throws NotificationTemplateException{
		if (isTrue(template.getActive())) {
			throw new NotificationTemplateException("Cannot remove active template!");
		}
		if (template.getVersion()==null) {
			throw new NotificationTemplateException("Cannot remove original template!");
		}
		notificationTemplateDAO.remove(template);
	}

	public NotificationTemplate saveNewEmailTemplate(NotificationTemplateId templateName, String content, String subject) {
		NotificationTemplate template = new NotificationTemplate();
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

    //Not used now, may be in the future
	@Deprecated
	public String processTemplateContent(String templateContent) {
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");  
	    Matcher matcher = pattern.matcher(templateContent);
	    String result = templateContent;
	    while (matcher.find()) {
	    	String ftlVariable = matcher.group(1);
	    	String keyWord = ftlVariable.replace("?", "\\?");
	    	int indexOfQuestionMark = ftlVariable.indexOf("?");
	    	String newString = indexOfQuestionMark != -1 
	    			?
	    				ftlVariable.substring(0, indexOfQuestionMark)
	    			:
	    				ftlVariable;
	    				result = result.replaceAll("\\$\\{"+keyWord+"\\}", newString);
	    }
	    return result;
	}

    public String getSubjectForTemplate(NotificationTemplateId templateName) {
        NotificationTemplate activeEmailTemplate = getActiveEmailTemplate(templateName);
        if(activeEmailTemplate != null){
            return activeEmailTemplate.getSubject();
        }
        throw new RuntimeException("No email template found: " + templateName);
    }
}
