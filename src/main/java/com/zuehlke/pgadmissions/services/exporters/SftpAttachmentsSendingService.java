package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

/**
 * This service encapsulates:<ol>
 * <li>Packing all files attached to given application form into a single, properly formatted zip file (as expected by PORTICO system).</li>
 * <li>Sending this file via SSH/SFTP to UCL server (i.e. PORTICO).</li>
 * </ol>
 */
@Service
public class SftpAttachmentsSendingService {

    public static class CouldNotCreateAttachmentsPack extends Exception {
        private static final long serialVersionUID = 79819935845687782L;
        public CouldNotCreateAttachmentsPack(String message) {
            super(message);
        }
    }

    public static class LocallyDefinedSshConfigurationIsWrong extends Exception {
        private static final long serialVersionUID = -3795035471781335158L;
        public LocallyDefinedSshConfigurationIsWrong(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class CouldNotOpenSshConnectionToRemoteHost extends Exception {
        private static final long serialVersionUID = -7766133504265217986L;
        public CouldNotOpenSshConnectionToRemoteHost(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SftpTargetDirectoryNotAccessible extends Exception {
        private static final long serialVersionUID = 1205344425274265339L;
        public SftpTargetDirectoryNotAccessible(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SftpTransmissionFailedOrProtocolError extends Exception {
        private static final long serialVersionUID = -3899282644603215520L;
        public SftpTransmissionFailedOrProtocolError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final JSchFactory jSchFactory;
    
    private PorticoAttachmentsZipCreator attachmentsZipCreator;

    private String sftpHost;

    private String sftpPort;

    private String sftpUsername;

    private String sftpPassword;

    private String targetFolder;

    public SftpAttachmentsSendingService() {
        this(null, null, null, null, null, null, null);
    }
    
    @Autowired
    public SftpAttachmentsSendingService(
            JSchFactory jSchFactory, 
            PorticoAttachmentsZipCreator attachmentsZipCreator,
            @Value("${xml.data.export.sftp.host}") String sftpHost, 
            @Value("${xml.data.export.sftp.port}") String sftpPort, 
            @Value("${xml.data.export.sftp.username}") String sftpUsername, 
            @Value("${xml.data.export.sftp.password}") String sftpPassword, 
            @Value("${xml.data.export.sftp.folder}") String targetFolder) {
        super();
        this.jSchFactory = jSchFactory;
        this.attachmentsZipCreator = attachmentsZipCreator;
        this.sftpHost = sftpHost;
        this.sftpPort = sftpPort;
        this.sftpUsername = sftpUsername;
        this.sftpPassword = sftpPassword;
        this.targetFolder = targetFolder;
    }

    /**
     * I preparing zip-package with all attachments for specified application form. Then I send this zip-packege over secure SFTP connection to PORTICO system.
     *
     * @param applicationForm application form containing attachments to be sent
     * @throws CouldNotCreateAttachmentsPack problem with building zip-pack with attachments
     * @throws LocallyDefinedSshConfigurationIsWrong local problems with preparing ssh session (like - no access to key file etc.)
     * @throws CouldNotOpenSshConnectionToRemoteHost network is down, some firewall stopped us or SSH authentication failed
     * @throws SftpTargetDirectoryNotAccessible target directory does not exist or our SSH user has no permissions to access this directory
     * @return the name of the created ZIP file on the server
     * @throws SftpTransmissionFailedOrProtocolError we were able to establish SSH connection but SFTP transfer over this connection failed - protocol error happened (possibly we lost the connection to the remote host)
     */
    public String sendApplicationFormDocuments(ApplicationForm applicationForm, TransferListener listener)  throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
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
            
            //possible errors: sftp protocol-level problems
            try {
                sftpChannel = (ChannelSftp) session.openChannel("sftp");
                sftpChannel.connect();
            } catch (JSchException e) {
                throw new SftpTransmissionFailedOrProtocolError("Failed to open sftp channel over previously established SSH connection, remote host address is: " + sftpHost + ":" + sftpPort, e);
            }

            //possible errors: target directory does not exist or permission denied
            try {
                if (StringUtils.isNotBlank(targetFolder)) {
                    sftpChannel.cd(targetFolder);
                }
            } catch (SftpException e) {
                throw new SftpTargetDirectoryNotAccessible("Failed to access remote directory for SFTP transmission: " + targetFolder + ", remote host address is: " + sftpHost + ":" + sftpPort, e);
            }

            //possible errors: sftp protocol-level problems, connection lost during transmission and local problem with building zip-pack with attachments
            try {
                String finalZipName = applicationForm.getUclBookingReferenceNumber() + ".zip";
                sftpOs = sftpChannel.put(finalZipName, ChannelSftp.OVERWRITE);
                attachmentsZipCreator.writeZipEntries(applicationForm, applicationForm.getUclBookingReferenceNumber(), sftpOs);
                return finalZipName;
            } catch (SftpException e) {
                throw new SftpTransmissionFailedOrProtocolError("SFTP protocol error during transmission of attachments for application form " + applicationForm.getId(), e);
            } catch (IOException e) {
                throw new SftpTransmissionFailedOrProtocolError("SFTP protocol error during transmission of attachments for application form " + applicationForm.getId() + "; possibly we lost the connection to the remote host", e);
            }

        } finally {
            IOUtils.closeQuietly(sftpOs);
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void setPorticoAttachmentsZipCreator(PorticoAttachmentsZipCreator zipCreator) {
        this.attachmentsZipCreator = zipCreator;
    }
}
