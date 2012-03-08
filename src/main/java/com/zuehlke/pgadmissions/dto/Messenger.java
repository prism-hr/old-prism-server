package com.zuehlke.pgadmissions.dto;

public class Messenger {

	
	private Integer messengerId;
	
	private String messengerType;
	
	private String messengerAddress;

	public Integer getMessengerId() {
		return messengerId;
	}

	public void setMessengerId(Integer messengerId) {
		this.messengerId = messengerId;
	}

	public String getMessengerType() {
		return messengerType;
	}

	public void setMessengerType(String messengerType) {
		this.messengerType = messengerType;
	}

	public String getMessengerAddress() {
		return messengerAddress;
	}

	public void setMessengerAddress(String messengerAddress) {
		this.messengerAddress = messengerAddress;
	}

}
