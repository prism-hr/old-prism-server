package com.zuehlke.pgadmissions.jms;

import javax.annotation.Resource;
import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JmsSender {

    @Resource(name = "mailQueue")
    private Queue mailQueue;
    
    @Autowired
    private JmsTemplate template;
    
    public JmsSender() {
    }
    
    @Transactional
    public void doSend() {
        template.convertAndSend(mailQueue, "Hello World");
    }
}
