package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.exporters.JSchFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testSftpContext.xml")
@Ignore
public class UclXferPrismUclConnectionIT  {

    @Autowired
    private JSchFactory jSchFactory;
    
    @Value("${application.host}")
    private String configuredHost;

    private Document document;
    
    @Before
    public void setup() throws IOException {
        Resource testFileAsResurce = new ClassPathResource("/pdf/valid.pdf");
        document = new Document().withContent(FileUtils.readFileToByteArray(testFileAsResurce.getFile()));
    }
    
    /**
     * This test actually connects to the following machine: xfer-prism.ucl.ac.uk.
     * And tries to upload a sample ZIP file. The authentication mechanis is publickey and 
     * uses the real PRISM SSH key which is also used in production.
     * <p> 
     * This test only works from pgadmission-sit.
     */
    @Test
    public void uploadTest() {
        if (!StringUtils.equalsIgnoreCase(configuredHost, "http://pgadmissions-sit.zuehlke.com")) {
            return;
        }
        
        Session session = null;
        ChannelSftp channel = null;
        OutputStream sftpOs = null;
        try {
            try {
                session = jSchFactory.getInstance();
            } catch (JSchException e) {
                Assert.fail("Failed to configure SSH connection");
                ExceptionUtils.printRootCauseStackTrace(e);
            } catch (ResourceNotFoundException e) {
                Assert.fail("Failed to configure SSH connection");
                ExceptionUtils.printRootCauseStackTrace(e);
            }
    
            try {
                session.connect();
            } catch (JSchException e) {
                Assert.fail("Failed to open SSH connection to PORTICO host, configured address was: " + jSchFactory.getSftpHost() + ":" + jSchFactory.getSftpPort() + " username/password=" + jSchFactory.getSftpUsername() + "/" + jSchFactory.getSftpPassword());
                ExceptionUtils.printRootCauseStackTrace(e);
            }
    
            try {
                channel = (ChannelSftp) session.openChannel("sftp");
                channel.connect();
            } catch (JSchException e) {
                Assert.fail("Failed to open sftp channel over previously established SSH connection, remote host address is: " + jSchFactory.getSftpHost() + ":" + jSchFactory.getSftpPort());
                ExceptionUtils.printRootCauseStackTrace(e);
            }
            
            try {
                if (StringUtils.isNotEmpty(jSchFactory.getFolder())) {
                    channel.cd(jSchFactory.getFolder());
                }
            } catch (SftpException e) {
                Assert.fail("Failed to access remote directory for SFTP transmission: " + jSchFactory.getFolder());
                ExceptionUtils.printRootCauseStackTrace(e);
            }
            
            try {
                sftpOs = channel.put(UUID.randomUUID() + ".zip");
                ZipOutputStream os = new ZipOutputStream(sftpOs);
                os.putNextEntry(new ZipEntry("test1.pdf"));
                os.write(document.getContent());
                os.closeEntry();
                IOUtils.closeQuietly(os);
            } catch (SftpException e) {
                Assert.fail("SFTP protocol error during transmission");
                ExceptionUtils.printRootCauseStackTrace(e);
            } catch (IOException e) {
                Assert.fail("SFTP protocol error during transmission");
                ExceptionUtils.printRootCauseStackTrace(e);
            }
        } finally {
            IOUtils.closeQuietly(sftpOs);
            if (channel != null && channel.isConnected())
                channel.disconnect();
            if (session != null && session.isConnected())
                session.disconnect();
        }        
    }
}
