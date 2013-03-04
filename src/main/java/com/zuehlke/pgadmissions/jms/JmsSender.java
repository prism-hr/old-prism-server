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
    public void doSend(int type) {
    	//template.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    	if (type  == 1) {
    		template.convertAndSend(mailQueue, "Hello World");
    	}
    	else {
    		template.convertAndSend(mailQueue, "This has to go through");
    	}
    }
}
