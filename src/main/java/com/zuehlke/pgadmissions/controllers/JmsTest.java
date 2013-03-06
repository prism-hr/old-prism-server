package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.jms.JmsSender;

@Controller
@RequestMapping("/doSend")
// TODO: Kevin - This is just for test purposes. And change the security.xml file as well
public class JmsTest {

    @Autowired
    private JmsSender jmsSender;
    
    public JmsTest() {
    }
    
    @RequestMapping(value="1", method = RequestMethod.GET)
    @ResponseBody
    public String doSend1() {
        jmsSender.doSend(1);
        return "OK1";
    }

    @RequestMapping(value="2", method = RequestMethod.GET)
    @ResponseBody
    public String doSend2() {
    	jmsSender.doSend(2);
    	return "OK1";
    }
}
