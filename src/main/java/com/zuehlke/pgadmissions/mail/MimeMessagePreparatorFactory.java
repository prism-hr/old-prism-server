package com.zuehlke.pgadmissions.mail;

import java.util.Arrays;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
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

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, String templatename,
			Map<String, Object> model, InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(new InternetAddress[] { toAddress }, ccAddresses, subject, templatename, model, replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress, String subject, String templatename, Map<String, Object> model,
			InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(new InternetAddress[] { toAddress }, null, subject, templatename, model, replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress[] toAddresses,//
			String subject, String templatename, Map<String, Object> model, InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(toAddresses, null, subject, templatename, model, replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress[] toAddresses, InternetAddress[] ccAddresses,//
			String subject, String templatename, Map<String, Object> model, InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
		if (prod) {
			return new ProductionMessagePreparator(toAddresses, ccAddresses, subject, templatename, model, replyToAddress, attachments);
		}
		return new DevelopmentMessagePreparator(toAddresses, ccAddresses, subject, templatename, model, replyToAddress, attachments);
	}

	MimeMessageHelper getMessageHelper(MimeMessage mimeMessage, boolean isMultipart) throws MessagingException {
		return new MimeMessageHelper(mimeMessage, isMultipart);
	}

	class ProductionMessagePreparator implements MimeMessagePreparator {
	    
		protected InternetAddress[] toAddresses;
		protected InternetAddress[] ccAddresses;
		private final String subject;
		private final String templatename;
		private final Map<String, Object> model;
		private final InternetAddress replyToAddress;
		private final PdfAttachmentInputSource[] attachments;

		public ProductionMessagePreparator(InternetAddress[] toAddresses, InternetAddress[] ccAddresses,//
				String subject, String templatename, Map<String, Object> model, InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
			this.toAddresses = toAddresses;
			this.ccAddresses = ccAddresses;
			this.subject = subject;
			this.templatename = templatename;
			this.model = model;
			this.replyToAddress = replyToAddress;
			this.attachments = attachments;
		}

		protected String getSubject() {
			return subject;
		}

		protected InternetAddress[] getCCAddresses() {
			return ccAddresses;
		}

		@Override
		public final void prepare(MimeMessage mimeMessage) throws Exception {
			MimeMessageHelper messageHelper = getMessageHelper(mimeMessage, true);
			StringBuilder logStringBuilder = new StringBuilder();
			for (InternetAddress address : toAddresses) {
				logStringBuilder.append(address.toString() + ", ");
			}

			log.info("Email \"" + getSubject() + "\" will be sent to " + logStringBuilder.toString());
			
			messageHelper.setTo(toAddresses);
			if (!ArrayUtils.isEmpty(getCCAddresses())) {
				messageHelper.setCc(getCCAddresses());
			}
			if (replyToAddress != null) {
				messageHelper.setReplyTo(replyToAddress);
			}
			messageHelper.setSubject(getSubject());
			messageHelper.setFrom(Environment.getInstance().getEmailFromAddress());

			for (PdfAttachmentInputSource attachment : attachments) {	
				messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");					
			}
			
			HtmlToPlainText htmlFormatter = new HtmlToPlainText();
			
			String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(config.getConfiguration().getTemplate(templatename), model);
			String plainText = htmlFormatter.getPlainText(htmlText);
			plainText = plainText + "\n\nIf the links do not work in your email client copy and paste them into your browser.";
			messageHelper.setText(plainText, htmlText);
		}
	}

	class DevelopmentMessagePreparator extends ProductionMessagePreparator {

		public DevelopmentMessagePreparator(InternetAddress[] toAddresses, InternetAddress[] ccAddresses,//
				String subject, String templatename, Map<String, Object> model, InternetAddress replyToAddress, PdfAttachmentInputSource... attachments) {
		
		    super(toAddresses, ccAddresses, subject, templatename, model, replyToAddress, attachments);
			for (InternetAddress internetAddress : toAddresses) {
				internetAddress.setAddress(Environment.getInstance().getEmailToAddress());
			}
			if (replyToAddress != null) {
				replyToAddress.setAddress(Environment.getInstance().getEmailToAddress());
			}
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
