package com.zuehlke.pgadmissions.domain.builders;

import javax.persistence.Column;

import com.zuehlke.pgadmissions.domain.Messenger;

public class MessengerBuilder {

	private Integer id;
	private String messengerType;
	
	private String messengerAddress;
	
	public MessengerBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public MessengerBuilder messengerType(String messengerType) {
		this.messengerType = messengerType;
		return this;
	}
	
	public MessengerBuilder messengerAddress(String messengerAddress) {
		this.messengerAddress = messengerAddress;
		return this;
	}
	
	public Messenger toMessenger(){
		Messenger messenger = new Messenger();
		messenger.setId(id);
		messenger.setMessengerAddress(messengerAddress);
		messenger.setMessengerType(messengerType);
		return messenger;
	}
}
