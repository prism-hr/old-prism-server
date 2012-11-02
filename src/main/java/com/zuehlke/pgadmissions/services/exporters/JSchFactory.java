package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

@Component
public class JSchFactory {

    @Value("${xml.data.export.sftp.host}")
    private String sftpHost;
    
    @Value("${xml.data.export.sftp.port}")
    private String sftpPort;
    
    @Value("${xml.data.export.sftp.username}")
    private String sftpUsername;
    
    @Value("${xml.data.export.sftp.password}")
    private String sftpPassword;
    
    @Value("${xml.data.export.sftp.privatekeyfile}")
    private Resource privateKeyFile;

    public JSchFactory() {
    }
    
    public JSch getInstance() throws JSchException, IOException {
        JSch jSch = new JSch();
        final byte[] privateKeyAsByteArray = FileUtils.readFileToByteArray(privateKeyFile.getFile());
        final byte[] emptyPassPhrase = new byte[0];
        jSch.addIdentity("prismIdentity", privateKeyAsByteArray, null, emptyPassPhrase);
        return jSch;
    }
    
    public String getSftpHost() {
        return sftpHost;
    }

    public void setSftpHost(String sftpHost) {
        this.sftpHost = sftpHost;
    }

    public String getSftpPort() {
        return sftpPort;
    }

    public void setSftpPort(String sftpPort) {
        this.sftpPort = sftpPort;
    }

    public String getSftpUsername() {
        return sftpUsername;
    }

    public void setSftpUsername(String sftpUsername) {
        this.sftpUsername = sftpUsername;
    }

    public String getSftpPassword() {
        return sftpPassword;
    }

    public void setSftpPassword(String sftpPassword) {
        this.sftpPassword = sftpPassword;
    }

    public Resource getSftpKeyfile() {
        return privateKeyFile;
    }

    public void setSftpKeyfile(Resource sftpKeyfile) {
        this.privateKeyFile = sftpKeyfile;
    }
}
