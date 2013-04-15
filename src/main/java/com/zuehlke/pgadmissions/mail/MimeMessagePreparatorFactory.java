package com.zuehlke.pgadmissions.mail;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.utils.Environment;

import freemarker.template.Template;

public class MimeMessagePreparatorFactory {

	final Logger log = LoggerFactory.getLogger(MimeMessagePreparatorFactory.class);

	final FreeMarkerConfig config;

	final boolean prod;

	MimeMessagePreparatorFactory() {
		this(null, true);
	}

	public MimeMessagePreparatorFactory(FreeMarkerConfig config, boolean prod) {
		this.config = config;
		this.prod = prod;
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress, InternetAddress[] ccAddresses,
			String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
			PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(new InternetAddress[] { toAddress }, ccAddresses, subject, templateName, templateContent,
				model, replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress toAddress, String subject,
			EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
			PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(new InternetAddress[] { toAddress }, null, subject, templateName, templateContent, model,
				replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(
			InternetAddress[] toAddresses,//
			String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
			PdfAttachmentInputSource... attachments) {
		return getMimeMessagePreparator(toAddresses, null, subject, templateName, templateContent, model, replyToAddress, attachments);
	}

	public MimeMessagePreparator getMimeMessagePreparator(InternetAddress[] toAddresses,
			InternetAddress[] ccAddresses,//
			String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
			PdfAttachmentInputSource... attachments) {
		if (prod) {
			return new ProductionMessagePreparator(toAddresses, ccAddresses, subject, templateName, templateContent, model,
					replyToAddress, attachments);
		}
		return new DevelopmentMessagePreparator(toAddresses, ccAddresses, subject, templateName, templateContent, model,
				replyToAddress, attachments);
	}

	MimeMessageHelper getMessageHelper(MimeMessage mimeMessage, boolean isMultipart) throws MessagingException {
		return new MimeMessageHelper(mimeMessage, isMultipart);
	}

	class ProductionMessagePreparator implements MimeMessagePreparator {
		protected InternetAddress[] toAddresses;
		protected InternetAddress[] ccAddresses;
		private final String subject;
		private final String templateContent;
		private final EmailTemplateName templateName;
		private final Map<String, Object> model;
		private final InternetAddress replyToAddress;
		private final PdfAttachmentInputSource[] attachments;

		public ProductionMessagePreparator(InternetAddress[] toAddresses, InternetAddress[] ccAddresses,
				String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
				PdfAttachmentInputSource... attachments) {
			this.subject = subject;
			this.templateName = templateName;
			this.templateContent = templateContent;
			this.model = model;
			this.replyToAddress = replyToAddress;
			this.attachments = attachments;

			if (toAddresses != null) {
				this.toAddresses = Arrays.copyOf(toAddresses, toAddresses.length);
			}

			if (ccAddresses != null) {
				this.ccAddresses = Arrays.copyOf(ccAddresses, ccAddresses.length);
			}
		}

		protected String getSubject() {
			return subject;
		}

		protected InternetAddress[] getCCAddresses() {
			return ccAddresses;
		}

		@Override
		public void prepare(MimeMessage mimeMessage) throws Exception {
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

			Template template = new Template(templateName.displayValue(), new StringReader(templateContent), config.getConfiguration());
			String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			String plainText = htmlFormatter.getPlainText(htmlText);
			plainText = plainText
					+ "\n\nIf the links do not work in your email client copy and paste them into your browser.";
			messageHelper.setText(plainText, htmlText);
		}
	}

	class DevelopmentMessagePreparator extends ProductionMessagePreparator {

		public DevelopmentMessagePreparator(InternetAddress[] toAddresses,
				InternetAddress[] ccAddresses,//
				String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model, InternetAddress replyToAddress,
				PdfAttachmentInputSource... attachments) {

			super(toAddresses, ccAddresses, subject, templateName, templateContent, model, replyToAddress, attachments);
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
