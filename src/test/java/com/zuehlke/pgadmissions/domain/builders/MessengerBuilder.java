package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Messenger;

public class MessengerBuilder {

	private Integer id;
	
	private String messengerAddress;
	
	public MessengerBuilder id(Integer id) {
		this.id = id;
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
		return messenger;
	}
}
