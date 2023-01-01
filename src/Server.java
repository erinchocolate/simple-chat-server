
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import model.Packet;

public class Server implements Runnable{
	private ServerSocket serverSocket;
	private int port;
	private UserDatabase db;
	private RSAEncryption rsa;
	private HashMap<String, Connection> clientMap = new HashMap<>();
	
	public Server(int port) {
		this.port = port;
		db = new UserDatabase();
		rsa = new RSAEncryption();		
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			while(!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				Connection connection = new Connection(clientSocket);	
				Thread thread = new Thread(connection);			
				thread.start();
			}			
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}	
	}
	
	
	public void sendPacketToOtherClient(String recipient, Packet packet){	
		Connection connection = clientMap.get(recipient);
		try {
			connection.out.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Shutdown server 
	public void close() {
		try {		
			if(serverSocket!=null) {
				serverSocket.close();
			}
		}
		catch (IOException e){
			e.printStackTrace();
		} 
	}
	
	/*
	 * Connection class is for communication between server and each client's socket
	 */
	class Connection implements Runnable {
		private Socket clientSocket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private String username;
		private String aesKey;
		private AESEncryption aes;
	
		public Connection(Socket clientSocket) {
			try {
				this.clientSocket = clientSocket;		
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in = new ObjectInputStream(clientSocket.getInputStream());
				aes = new AESEncryption();
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getUsername() {
			return username;
		}
		
		public Socket getClientSocket() {
			return clientSocket;
		}
		
		@Override
		public void run() {
			while(clientSocket.isConnected()) {
				try {
					
					// Call different methods depends on packet header
					Packet packet = (Packet) in.readObject();
					String header = packet.getHeader();
					if(header.equals("login")) {
						loginResponse(packet);				
					}else if(header.equals("signup")) {
						signupResponse(packet);					
					}else if(header.equals("client list")){
						clientListResponse();	
					}else {
						messageResponse(packet);
					}
				} catch ( Exception e) {
					close();
					break;
				} 
			}	
		}
		
		public void sendPacketToClient(Packet packet){
			try {
				out.writeObject(packet);
			} catch (IOException e) {
				e.printStackTrace();
				close();
			}
		}
		
		
		public void loginResponse(Packet packet){			
			try {
				// Decrypt keys using private key 
				String aesKey = rsa.decrypt(packet.getKey());
				String IVKey =  rsa.decrypt(packet.getIV());
				// Set keys for AES encryption
				aes.initFromStrings(aesKey, IVKey);
				// Decrypt packet message using AES 
				String username = aes.decrypt(packet.getUsername());
				String pwd = aes.decrypt(packet.getPassword());
				Packet response = new Packet("login");
				// If the username exsits in the database and password match what stored,
				// Then the user can login, send a packet to tell client
				if(db.isUsernameExsit(username)&&db.isPasswordMatch(username, pwd)) {
					clientMap.put(username, this);			
					response.setMessage("success");
					response.setRecipient(username);
				}else {
					response.setMessage("error");
				}
				sendPacketToClient(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void signupResponse(Packet packet) throws Exception {
			String aesKey = rsa.decrypt(packet.getKey());
			String IVKey =  rsa.decrypt(packet.getIV());
			aes.initFromStrings(aesKey, IVKey);
			
			String username = aes.decrypt(packet.getUsername());
			String pwd = aes.decrypt(packet.getPassword());
			Packet response = new Packet("signup");
			
			// If username doesn't exsits in database, insert username
			// and password, otherwise send an error message
			try {			
				if(!db.isUsernameExsit(username)) {
					db.insertIntoDB(username, pwd);
					response.setMessage("success");
				}else {
					response.setMessage("error");
				}		
			sendPacketToClient(response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Send the list of what client is connected with server
		public void clientListResponse() {
			Packet response = new Packet("client list");
			ArrayList<String> list = new ArrayList<String>(clientMap.keySet());
			response.setClientList(list);
			sendPacketToClient(response);
		}
		
		public void messageResponse(Packet packet) throws Exception {
			String aesKey = rsa.decrypt(packet.getKey());
			String IVKey =  rsa.decrypt(packet.getIV());
			aes.initFromStrings(aesKey, IVKey);
			String recipient = aes.decrypt(packet.getRecipient());
			String message = aes.decrypt(packet.getMessage());
			String sender = aes.decrypt(packet.getSender());
			
			Packet packetFromServer = new Packet("message");
			packetFromServer.setRecipient(recipient);
			packetFromServer.setMessage(message);
			packetFromServer.setSender(sender);
			
			sendPacketToOtherClient(recipient, packetFromServer);
		}
			
		public void close() {
			try {
				in.close();
				out.close();
				if(!clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter a port number: ");
		int port = scanner.nextInt();
		Server server = new Server(port);
		System.out.println("Server running");
		server.run();	
	}
}
