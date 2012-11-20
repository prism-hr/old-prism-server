package com.zuehlke.pgadmissions.services.exporters;

import java.io.OutputStream;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;

public class SftpAttachmentsSendingServiceTest {

    private JSchFactory jSchFactoryMock;

    private PdfDocumentBuilder pdfDocumentBuilderMock;

    private SftpAttachmentsSendingService documentExportService;

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

    @Test
    public void testSendApplicationFormDocuments() throws Exception {

        String referenceNumber = "123";
        Document cv = new DocumentBuilder().id(1).content("content".getBytes()).toDocument();
        ApplicationForm form = new ApplicationFormBuilder().id(1).cv(cv).toApplicationForm();
        form.setUclBookingReferenceNumber(referenceNumber);
        Session session = EasyMock.createMock(Session.class);
        ChannelSftp channelSftp = EasyMock.createMock(ChannelSftp.class);
        OutputStream os = EasyMock.createMock(OutputStream.class);
        EasyMock.expect(jSchFactoryMock.getInstance()).andReturn(session);
        EasyMock.expect(session.isConnected()).andReturn(true);
        EasyMock.expect(session.openChannel("sftp")).andReturn(channelSftp);
        EasyMock.expect(channelSftp.put(referenceNumber + ".zip")).andReturn(os);
        EasyMock.expect(channelSftp.isConnected()).andReturn(true);
        channelSftp.connect();
        channelSftp.cd(null);
        session.connect();
        os.write(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        os.write(EasyMock.aryEq("123~UA_CV~.pdf".getBytes()), EasyMock.eq(0), EasyMock.eq(14));
        os.write(EasyMock.anyObject(byte[].class), EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        channelSftp.disconnect();
        session.disconnect();
        os.close();
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(jSchFactoryMock, session, channelSftp, os);
        documentExportService.sendApplicationFormDocuments(form, new DeafListener());
        EasyMock.verify(jSchFactoryMock, session, channelSftp, os);
    }

    @Before
    public void setup() {
        jSchFactoryMock = EasyMock.createMock(JSchFactory.class);
        pdfDocumentBuilderMock = EasyMock.createMock(PdfDocumentBuilder.class);
        documentExportService = new SftpAttachmentsSendingService(jSchFactoryMock, pdfDocumentBuilderMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);
    }
}
