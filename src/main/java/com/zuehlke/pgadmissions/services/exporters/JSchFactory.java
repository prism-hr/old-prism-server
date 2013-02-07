package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

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
    
    @Value("${xml.data.export.sftp.folder}")
    private String folder;
    
    @Value("${xml.data.export.sftp.privatekeyfile}")
    private Resource privateKeyFile;

    @Value("${xml.data.export.sftp.privatekeyfile}")
    private String privateKeyFilePathAsString;

    private JSch jSch = null;
    
    public JSchFactory() {
    }
    
    public Session getInstance() throws JSchException, ResourceNotFoundException {
        jSch = new JSch();

        byte[] privateKeyAsByteArray;
        try {
            privateKeyAsByteArray = FileUtils.readFileToByteArray(privateKeyFile.getFile());
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not access SSH private key file, configured path was: " + privateKeyFilePathAsString, e);
        }

        final byte[] emptyPassPhrase = new byte[0];
        
        jSch.addIdentity("prismIdentity", privateKeyAsByteArray, null, emptyPassPhrase);
        
        Session session = jSch.getSession(sftpUsername, sftpHost, Integer.valueOf(sftpPort));
        session.setPassword(sftpPassword);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        return session;
    }
    
    public JSch getjSch() {
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
