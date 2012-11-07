package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;
import java.io.OutputStream;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.exceptions.DocumentExportException;

public class DocumentExportServiceTest {

	private JSchFactory jSchFactoryMock;
	private DocumentExportService documentExportService;

	@Test
	public void testSendApplicationFormDocuments() throws DocumentExportException, JSchException, IOException, SftpException {
		
		String referenceNumber = "123";
		Document cv = new DocumentBuilder().id(1).content("content".getBytes()).toDocument();
		ApplicationForm form = new ApplicationFormBuilder().id(1).cv(cv).toApplicationForm();
		Session session = EasyMock.createMock(Session.class);
		ChannelSftp channelSftp = EasyMock.createMock(ChannelSftp.class);
		OutputStream os = EasyMock.createMock(OutputStream.class);
		EasyMock.expect(jSchFactoryMock.getInstance()).andReturn(session);
		EasyMock.expect(session.isConnected()).andReturn(true);
		EasyMock.expect(session.openChannel("sftp")).andReturn(channelSftp);
		EasyMock.expect(channelSftp.put(referenceNumber+".zip")).andReturn(os);
		EasyMock.expect(channelSftp.isConnected()).andReturn(true);
		channelSftp.connect();
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
		documentExportService.sendApplicationFormDocuments(form, referenceNumber);
		EasyMock.verify(jSchFactoryMock, session, channelSftp,os);
	}
	
	@Before
	 public void setup() {
		jSchFactoryMock = EasyMock.createMock(JSchFactory.class);
		documentExportService = new DocumentExportService(jSchFactoryMock);
	}
}
