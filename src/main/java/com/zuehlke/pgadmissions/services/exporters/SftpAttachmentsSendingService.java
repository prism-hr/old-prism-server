package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.exceptions.DocumentExportException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;

@Service
public class SftpAttachmentsSendingService {

	private final JSchFactory jSchFactory;
	private final PdfDocumentBuilder pdfDocumentBuilder;
    private final String targetFolder;

	@Autowired
	public SftpAttachmentsSendingService(JSchFactory jSchFactory, PdfDocumentBuilder pdfDocumentBuilder, @Value("${xml.data.export.sftp.folder}") final String targetFolder) {
		this.jSchFactory = jSchFactory;
		this.pdfDocumentBuilder = pdfDocumentBuilder;
        this.targetFolder = targetFolder;
	}

	public void sendApplicationFormDocuments(ApplicationForm applicationForm) throws DocumentExportException, JSchException,
			IOException, SftpException {
		Session session = null;
		ChannelSftp sftpChannel = null;
		OutputStream sftpOs = null;
		try {
			session = jSchFactory.getInstance();
			session.connect();
			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
            sftpChannel.cd(targetFolder);
			sftpOs = sftpChannel.put(applicationForm.getUclBookingReferenceNumber() + ".zip");
			writeZipEntries(applicationForm, applicationForm.getUclBookingReferenceNumber(), sftpOs);
		} finally {
			IOUtils.closeQuietly(sftpOs);
			if (sftpChannel != null && sftpChannel.isConnected())
				sftpChannel.disconnect();
			if (session != null && session.isConnected())
				session.disconnect();
		}
	}

	private void writeZipEntries(ApplicationForm applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, DocumentExportException {
		ZipOutputStream zos = null;
		try {			
			zos = new ZipOutputStream(sftpOs);
			addTranscriptFiles(zos, applicationForm, referenceNumber);
			addReserchProposal(zos, applicationForm, referenceNumber);
			addLanguageTestCertificate(zos, applicationForm, referenceNumber);
			addCV(zos, applicationForm, referenceNumber);
			addReferences(zos, applicationForm, referenceNumber);
		}
		finally {
			IOUtils.closeQuietly(zos);
		}
	}

	private void addReferences(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, DocumentExportException {
		List<ReferenceComment> references = applicationForm.getReferencesToSend();
		String filename;
		switch(references.size()) {
		case 2:
			filename = PorticoDocumentNameMappings.getReferenceFilename(referenceNumber, 2) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			pdfDocumentBuilder.writePdf(references.get(1), zos);
			zos.closeEntry();
		case 1:
			filename = PorticoDocumentNameMappings.getReferenceFilename(referenceNumber, 1) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.putNextEntry(new ZipEntry(filename));
			pdfDocumentBuilder.writePdf(references.get(0), zos);
			zos.closeEntry();
		case 0:
			break;
		default:
			throw new DocumentExportException("There should be at most 2 references marked for sending to UCL");		
		}
	}

	private void addCV(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException {
		Document cv = applicationForm.getCv();
		if(cv != null) {
			String filename = PorticoDocumentNameMappings.getCVFilename(referenceNumber) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(cv.getContent());
			zos.closeEntry();
		}
		
	}

	private void addLanguageTestCertificate(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, DocumentExportException {
		List<LanguageQualification> languageQualifications = applicationForm.getLanguageQualificationToSend();
		if(languageQualifications.size() > 1)
			throw new DocumentExportException("There should be at most 1 languageQualification marked for sending to UCL");
		if(!languageQualifications.isEmpty()) {			
			String filename  = PorticoDocumentNameMappings.getEnglishLanguageCertificateFilename(referenceNumber) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(languageQualifications.get(0).getLanguageQualificationDocument().getContent());
			zos.closeEntry();
		}
	}

	private void addReserchProposal(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException {
		Document personalStatement = applicationForm.getPersonalStatement();
		if(personalStatement != null) {
			String filename = PorticoDocumentNameMappings.getResearchProposalFilename(referenceNumber) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(personalStatement.getContent());
			zos.closeEntry();
		}
	}

	private void addTranscriptFiles(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, DocumentExportException {
		List<Document> qualifications = applicationForm.getQualificationsToSend();
		String filename;
		switch(qualifications.size()) {
		case 2:
			filename = PorticoDocumentNameMappings.getTranscriptFilename(referenceNumber, 2) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(qualifications.get(1).getContent());
			zos.closeEntry();
		case 1:
			filename = PorticoDocumentNameMappings.getTranscriptFilename(referenceNumber, 1) + ".pdf";
			zos.putNextEntry(new ZipEntry(filename));
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(qualifications.get(0).getContent());
			zos.closeEntry();
		case 0:
			break;//todo: check if business ruless force us to have at least one transcript file attached - it yes, throw DocumentExportException
		default:
			throw new DocumentExportException("There should be at most 2 qualifications marked for sending to UCL");		
		}
	}

}
