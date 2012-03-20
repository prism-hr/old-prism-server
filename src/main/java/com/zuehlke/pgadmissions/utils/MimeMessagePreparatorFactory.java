package com.zuehlke.pgadmissions.utils;

import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


public class MimeMessagePreparatorFactory {

	private final FreeMarkerConfig config;
	private final boolean prod;
	
	MimeMessagePreparatorFactory() {
		this(null, true);
	}
	
	public MimeMessagePreparatorFactory(FreeMarkerConfig config, boolean prod) {
		this.config = config;
		this.prod  = prod;
	}

	public MimeMessagePreparator getMimeMessagePreparator(final InternetAddress toAddress, final String subject, final String templatename,
			final Map<String, Object> model) {
		return new MimeMessagePreparator() {

			public void prepare(MimeMessage mimeMessage) throws Exception {

				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				if(!prod){
					toAddress.setAddress(Environment.getInstance().getEmailToAddress());
				}
				message.setTo(toAddress);
				message.setSubject(subject);
				message.setFrom(Environment.getInstance().getEmailFromAddress()); // could be
				String text = FreeMarkerTemplateUtils.processTemplateIntoString(config.getConfiguration().getTemplate(templatename), model);

				message.setText(text, true);
			}
		};
	}
}
