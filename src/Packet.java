

import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SecretKey;

// This is the data model to communicate between server and client
public class Packet implements Serializable{
	private ArrayList<String> clientList = new ArrayList<>();
	private String username;
	private String password;
	private String header;
	private String sender;
	private String recipient;
	private String message;
	private String key;
	private String IV;

	public Packet(String header){
		this.header = header;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setIV(String IV) {
		this.IV = IV;
	}
	
	public String getIV() {
		return IV;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getHeader() {
		return header;
	}
	
	public void setClientList(ArrayList<String> clientList) {
		this.clientList = clientList;
	}
	
	public ArrayList<String> getClientList() {
		return clientList;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	public String getRecipient() {
		return recipient;
	}
}
