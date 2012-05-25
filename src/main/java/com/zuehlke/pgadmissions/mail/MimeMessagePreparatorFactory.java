package com.zuehlke.pgadmissions.mail;

import java.util.Arrays;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.utils.Environment;

public class MimeMessagePreparatorFactory {
	static final Logger log = Logger.getLogger(MimeMessagePreparatorFactory.class);

	final FreeMarkerConfig config;
	final boolean prod;

	MimeMessagePreparatorFactory() {
		this(null, true);
	}

	public MimeMessagePreparatorFactory(FreeMarkerConfig config, boolean prod) {
		this.config = config;
		this.prod = prod;
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress,// 
			String subject, String templatename, Map<String, Object> model) {
		return getMimeMessagePreparator(toAddress, null, subject, templatename, model);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress, InternetAddress[] ccAddresses,//
			String subject, String templatename, Map<String, Object> model) {

		if (prod) {
			return new ProductionMessagePreparator(toAddress, ccAddresses, subject, templatename, model);
		}
		return new DevelopmentMessagePreparator(toAddress, ccAddresses, subject, templatename, model);
	}

	class ProductionMessagePreparator implements MimeMessagePreparator {

		protected InternetAddress toAddress;
		protected InternetAddress[] ccAddresses;
		private final String subject;
		private final String templatename;
		private final Map<String, Object> model;

		public ProductionMessagePreparator(InternetAddress toAddress, InternetAddress[] ccAddresses,// 
				String subject, String templatename, Map<String, Object> model) {
			this.toAddress = toAddress;
			this.ccAddresses = ccAddresses;
			this.subject = subject;
			this.templatename = templatename;
			this.model = model;
		}

		protected String getSubject() {
			return subject;
		}

		protected InternetAddress[] getCCAddresses() {
			return ccAddresses;
		}

		@Override
		public final void prepare(MimeMessage mimeMessage) throws Exception {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

			log.info("Email \"" + getSubject() + "\" will be send to " + toAddress);
			message.setTo(toAddress);
			if (!ArrayUtils.isEmpty(getCCAddresses())) {
				message.setCc(getCCAddresses());
			}
			message.setSubject(getSubject());
			message.setFrom(Environment.getInstance().getEmailFromAddress()); // could be
			String text = FreeMarkerTemplateUtils.processTemplateIntoString(config.getConfiguration().getTemplate(templatename), model);
			message.setText(text, true);
		}

	}

	class DevelopmentMessagePreparator extends ProductionMessagePreparator {

		public DevelopmentMessagePreparator(InternetAddress toAddress, InternetAddress[] ccAddresses,// 
				String subject, String templatename, Map<String, Object> model) {
			super(toAddress, ccAddresses, subject, templatename, model);
			super.toAddress.setAddress(Environment.getInstance().getEmailToAddress());
		}

		@Override
		protected String getSubject() {
			if (!ArrayUtils.isEmpty(ccAddresses)) {
				return super.getSubject() + " <NON-PROD-Message: CC to: " + Arrays.toString(ccAddresses) + ">";
			}
			return super.getSubject();
		}

		@Override
		protected InternetAddress[] getCCAddresses() {
			return null;
		}
	}
}
