package com.zuehlke.pgadmissions.services.uclexport;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.exporters.JSchFactory;
import com.zuehlke.pgadmissions.services.exporters.PorticoDocumentNameMappings;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This service encapsulates:<ol>
 * <li>Packing all files attached to given application form into a single, properly formatted zip file (as expected by PORTICO system).</li>
 * <li>Sending this file via SSH/SFTP to UCL server (i.e. PORTICO).</li>
 * </ol>
 */
@Service
class SftpAttachmentsSendingService {

    public static class CouldNotCreateAttachmentsPack extends Exception {
        public CouldNotCreateAttachmentsPack(String message) {
            super(message);
        }
    }

    public static class LocallyDefinedSshConfigurationIsWrong extends Exception {
        public LocallyDefinedSshConfigurationIsWrong(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class CouldNotOpenSshConnectionToRemoteHost extends Exception {
        public CouldNotOpenSshConnectionToRemoteHost(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SftpTargetDirectoryNotAccessible extends Exception {
        public SftpTargetDirectoryNotAccessible(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SftpTransmissionFailedOrProtocolError extends Exception {
        public SftpTransmissionFailedOrProtocolError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final JSchFactory jSchFactory;
    private final PdfDocumentBuilder pdfDocumentBuilder;

    @Autowired
    private UclExportServiceImpl uclExportService;

    @Value("${xml.data.export.sftp.host}")
    private String sftpHost;

    @Value("${xml.data.export.sftp.port}")
    private String sftpPort;

    @Value("${xml.data.export.sftp.username}")
    private String sftpUsername;

    @Value("${xml.data.export.sftp.password}")
    private String sftpPassword;

    @Value("${xml.data.export.sftp.folder}")
    private String targetFolder;

    @Autowired
    public SftpAttachmentsSendingService(JSchFactory jSchFactory, PdfDocumentBuilder pdfDocumentBuilder) {
        this.jSchFactory = jSchFactory;
        this.pdfDocumentBuilder = pdfDocumentBuilder;
    }

    /**
     * I preparing zip-package with all attachments for specified application form. Then I send this zip-packege over secure SFTP connection to PORTICO system.
     *
     * @param applicationForm application form containing attachments to be sent
     * @throws CouldNotCreateAttachmentsPack problem with building zip-pack with attachments
     * @throws LocallyDefinedSshConfigurationIsWrong local problems with preparing ssh session (like - no access to key file etc.)
     * @throws CouldNotOpenSshConnectionToRemoteHost network is down, some firewall stopped us or SSH authentication failed
     * @throws SftpTargetDirectoryNotAccessible target directory does not exist or our SSH user has no permissions to access this directory
     * @throws SftpTransmissionFailedOrProtocolError we were able to establish SSH connection but SFTP transfer over this connection failed - protocol error happened (possibly we lost the connection to the remote host)
     */
    public void sendApplicationFormDocuments(ApplicationForm applicationForm, TransferListener listener)  throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
        CouldNotOpenSshConnectionToRemoteHost,  SftpTargetDirectoryNotAccessible, SftpTransmissionFailedOrProtocolError
    {
        Session session = null;
        ChannelSftp sftpChannel = null;
        OutputStream sftpOs = null;
        try {
            //possible errors: local problems with preparing ssh session (like - no access to key file etc.)
            try {
                session = jSchFactory.getInstance();
            } catch (JSchException e) {
                throw new LocallyDefinedSshConfigurationIsWrong("Failed to configure SSH connection", e);
            } catch (ResourceNotFoundException e) {
                throw new LocallyDefinedSshConfigurationIsWrong("Failed to configure SSH connection", e);
            }

            //possible errors: network down, firewall or authentication failed
            try {
                session.connect();
            } catch (JSchException e) {
                throw new CouldNotOpenSshConnectionToRemoteHost("Failed to open SSH connection to PORTICO host, configured address was: " + sftpHost + ":" + sftpPort + " username/password=" + sftpUsername + "/" + sftpPassword, e);
            }

            uclExportService.triggerSshConnectionEstablished(listener);

            //possible errors: sftp protocol-level problems
            try {
                sftpChannel = (ChannelSftp) session.openChannel("sftp");
                sftpChannel.connect();
            } catch (JSchException e) {
                throw new SftpTransmissionFailedOrProtocolError("Failed to open sftp channel over previously established SSH connection, remote host address is: " + sftpHost + ":" + sftpPort, e);
            }

            //possible errors: target directory does not exist or permission denied
            try {
                sftpChannel.cd(targetFolder);
            } catch (SftpException e) {
                throw new SftpTargetDirectoryNotAccessible("Failed to access remote directory for SFTP transmission: " + targetFolder + ", remote host address is: " + sftpHost + ":" + sftpPort, e);
            }

            uclExportService.triggerAttachmentsSftpTransmissionStarted(listener);

            //possible errors: sftp protocol-level problems, connection lost during transmission and local problem with building zip-pack with attachments
            try {
                sftpOs = sftpChannel.put(applicationForm.getUclBookingReferenceNumber() + ".zip");
                this.writeZipEntries(applicationForm, applicationForm.getUclBookingReferenceNumber(), sftpOs);
            } catch (SftpException e) {
                throw new SftpTransmissionFailedOrProtocolError("SFTP protocol error during transmission of attachments for application form " + applicationForm.getId(), e);
            } catch (IOException e) {
                throw new SftpTransmissionFailedOrProtocolError("SFTP protocol error during transmission of attachments for application form " + applicationForm.getId() + "; possibly we lost the connection to the remote host", e);
            }

        } finally {
            IOUtils.closeQuietly(sftpOs);
            if (sftpChannel != null && sftpChannel.isConnected())
                sftpChannel.disconnect();
            if (session != null && session.isConnected())
                session.disconnect();
        }
    }

    private void writeZipEntries(ApplicationForm applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, CouldNotCreateAttachmentsPack {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(sftpOs);
            addTranscriptFiles(zos, applicationForm, referenceNumber);
            addReserchProposal(zos, applicationForm, referenceNumber);
            addLanguageTestCertificate(zos, applicationForm, referenceNumber);
            addCV(zos, applicationForm, referenceNumber);
            addReferences(zos, applicationForm, referenceNumber);
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }

    private void addReferences(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, CouldNotCreateAttachmentsPack {
        List<ReferenceComment> references = applicationForm.getReferencesToSend();
        String filename;
        switch (references.size()) {
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
                throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
        }
    }

    private void addCV(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException {
        Document cv = applicationForm.getCv();
        if (cv != null) {
            String filename = PorticoDocumentNameMappings.getCVFilename(referenceNumber) + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(cv.getContent());
            zos.closeEntry();
        }
    }

    private void addLanguageTestCertificate(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, CouldNotCreateAttachmentsPack {
        List<LanguageQualification> languageQualifications = applicationForm.getLanguageQualificationToSend();
        if (languageQualifications.size() > 1)
            throw new CouldNotCreateAttachmentsPack("There should be at most 1 languageQualification marked for sending to UCL");
        if (!languageQualifications.isEmpty()) {
            String filename = PorticoDocumentNameMappings.getEnglishLanguageCertificateFilename(referenceNumber) + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(languageQualifications.get(0).getLanguageQualificationDocument().getContent());
            zos.closeEntry();
        }
    }

    private void addReserchProposal(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException {
        Document personalStatement = applicationForm.getPersonalStatement();
        if (personalStatement != null) {
            String filename = PorticoDocumentNameMappings.getResearchProposalFilename(referenceNumber) + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(personalStatement.getContent());
            zos.closeEntry();
        }
    }

    private void addTranscriptFiles(ZipOutputStream zos, ApplicationForm applicationForm, String referenceNumber) throws IOException, CouldNotCreateAttachmentsPack {
        List<Document> qualifications = applicationForm.getQualificationsToSend();
        String filename;
        switch (qualifications.size()) {
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
                break;//todo: check if business ruless force us to have at least one transcript file attached - it yes, throw CouldNotCreateAttachmentsPack
            default:
                throw new CouldNotCreateAttachmentsPack("There should be at most 2 qualifications marked for sending to UCL");
        }
    }

}
