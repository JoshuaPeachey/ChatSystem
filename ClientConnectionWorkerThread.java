import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingWorker;

public class ClientConnectionWorkerThread extends SwingWorker<String, String>{

	private String address;
	private int port = 14002;
	private String chatName;
    private MessageOutQueue messageOutQueue;
    private ClientGUI gui;

    public ClientConnectionWorkerThread(String add, String name, MessageOutQueue messageQueue, ClientGUI g){
        address = add;
        chatName = name;
        messageOutQueue = messageQueue;
        gui = g;
    }

    @Override
    protected String doInBackground() {
        try(Socket serverSocket = new Socket(address, port)) {

			// Setting up the worker thread that will print all the message that come from the server.
            ChatListenerWorkerThread chatListener = new ChatListenerWorkerThread(serverSocket, gui);
            chatListener.execute();
			
			// Set up the ability to send the data to the server
			PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);

			// Sends the server the users chat name.
			serverOut.println("&" + chatName);
			
            // Waits for a message to appear in the message queue then sends the message to the server.
			while(true) {
                try{
                    synchronized(messageOutQueue){
                        while(messageOutQueue.isEmpty()){
                            messageOutQueue.wait();
                        }
                        String message = messageOutQueue.popMessage();
                        serverOut.println(message);

                    }

                } catch(InterruptedException e){
                    System.out.println("Thread interruped");
                }
			}			
		} catch (UnknownHostException e) {
            System.out.println("The IP of the host could not be determined");
            gui.reportFailureToConnect();
			// e.printStackTrace();
		} catch (IOException e) {
            System.out.println("Failed to connect to server");
            gui.reportFailureToConnect();
			// e.printStackTrace();
		}
        return "connection ended";
    }

    @Override
    // This called once or doInBackground method has finished
    protected void done() {
        gui.reportServerDisconnect();
    }
}
