import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer implements Runnable {
	public static ArrayList<ChatServer> chatServers = new ArrayList<ChatServer>();

	private Socket clientSocket;
	private BufferedReader clientIn;
	private PrintWriter clientOut;
	
	private String chatName;

	public ChatServer(Socket newClientSocket) {
		clientSocket = newClientSocket;

		try {
			// Setup the ability to read the data from the client
			InputStreamReader clientCharStream = new InputStreamReader(clientSocket.getInputStream());
			clientIn = new BufferedReader(clientCharStream);
							
			// Set up the ability to send the data to the client	
			clientOut = new PrintWriter(clientSocket.getOutputStream(), true);

		} catch (IOException e){
			System.out.println("Connection failed");
		}

	}

	public void setChatName(String name) {
		chatName = name;
	}


	public void sendMessageToOthers(String message){
		// This sends a message to all the other users 
		for (ChatServer client: chatServers){
			if (client != this) {
				client.sendMessageToSelf(chatName + ": " + message);
			}
		}
	}

	public void sendMessageToSelf(String message){
		clientOut.println(message);
	}

	@Override
	public void run() {

		try{	
			// Read from the client, and send the message to the other users
			while(true) {
				String userInput = clientIn.readLine();
				if (!(userInput== null)){
					if (userInput.length() > 0){
						if (userInput.charAt(0) == '&') {
							chatName = userInput.substring(1);
						} else {
							sendMessageToOthers(userInput);
						}
					}
				}
			}
		} catch (IOException e) {
			chatServers.remove(this);
			System.out.println("User Disconnected");
			return; // Thread ends itself
		}
	}

	public static void main(String[] args) {
		int port = 14002;
		ChatServer newChatServer;
		Thread newChatServerThread;
		
		while(true){
			try(ServerSocket mySocket = new ServerSocket(port)) {
				System.out.println("Server listening...");
				// Accept a connection from a client
				Socket newClientSocket = mySocket.accept();
				System.out.println("Server accepted connection on: " + mySocket.getLocalPort() + " ; " + newClientSocket.getPort());
				
				newChatServer = new ChatServer(newClientSocket);
				ChatServer.chatServers.add(newChatServer);

				newChatServerThread = new Thread(newChatServer);

				newChatServerThread.start();
				

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}