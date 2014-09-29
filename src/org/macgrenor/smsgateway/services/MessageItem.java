package org.macgrenor.smsgateway.services;

public class MessageItem {
	int id;
	String Sender;
	String Message;
	
	public MessageItem(int id, String Sender, String Message) {
		this.id = id;
		this.Sender = Sender;
		this.Message = Message;
	}
	
	public String toString() {
		return Sender + " : " + Message.substring(0, Math.min(Message.length(), 50));
	}
}
