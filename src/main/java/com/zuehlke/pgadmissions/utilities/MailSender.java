package com.zuehlke.pgadmissions.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

public class MailSender {

	private MailSender mailSender;
	 
	private SimpleMailMessage message;
	
	@Autowired
	public MailSender(){
		
	}
	
	 public void setMailSender(MailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    public void setMessage(SimpleMailMessage message) {
	        this.message = message;
	    }

	    public SimpleMailMessage getMessage() {
	        return this.message;
	    }
	    
	    public MailSender getMailSender(){
	    	return this.mailSender;
	    }
    
    public void send(String firstname, String lastname, String emailAddress){
    	SimpleMailMessage msg = new SimpleMailMessage(this.message);
        msg.setTo(emailAddress);
        msg.setText(
            "Dear " + firstname
                + lastname
                + ", you have successfuly been registered with UCL postgraduate Portal. " +
                "Please click on the link below to activate your account. "
               );
    }
}
