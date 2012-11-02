package com.zuehlke.pgadmissions.services.exporters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AdmissionApplicationsSFTPServiceTest {

    @Autowired
    private JSchFactory jSchFactory;

    private Document document;
    
    @Before
    public void setup() throws IOException {
        File pdfFile = new File("src/test/resources/pdf/valid.pdf");
        document = new DocumentBuilder().content(FileUtils.readFileToByteArray(pdfFile)).toDocument();
    }
    
    @Test
    @Ignore
    public void connectiviyTest() throws JSchException, IOException, SftpException {
        JSch jsch = jSchFactory.getInstance();

        Session session = jsch.getSession(jSchFactory.getSftpUsername(), jSchFactory.getSftpHost(), 22);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        sftpChannel.connect();

        OutputStream put = sftpChannel.put("test.zip");
        
        ZipOutputStream os = new ZipOutputStream(put);
        os.putNextEntry(new ZipEntry("test1.pdf"));
        os.write(document.getContent());
        os.closeEntry();
        IOUtils.closeQuietly(os);
        IOUtils.closeQuietly(put);

        sftpChannel.disconnect();
        
        session.disconnect();
    }
}
