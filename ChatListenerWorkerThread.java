import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.SwingWorker;

public class ChatListenerWorkerThread extends SwingWorker<String, String>{
    private BufferedReader serverIn;
    private ClientGUI gui;


    public ChatListenerWorkerThread(Socket serverSocket, ClientGUI g) {
        gui = g;
        try {
            serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch(IOException e){
            System.out.println("Connection to Server Failed");
            gui.reportServerDisconnect();
        }
    }

    @Override
    protected String doInBackground() {
        String serverResponse;
        while(true){
            try {
                serverResponse = serverIn.readLine();
                if(serverResponse != null){
                    System.out.println(serverResponse);
                    System.out.println("here");
                    publish(serverResponse);
                }
            } catch (IOException e) {
                // System.out.println("Server Disconnected");
                return "Server Disconnected";
            }
        }
    }
    
    @Override
    // This called once or doInBackground method has finished
    protected void done() {
        gui.reportServerDisconnect();
    }

    @Override
    // We can safely update the GUI using this method
    protected void process(List<String> progress) {
        System.out.println(progress.get(0));
        if (progress.size() > 0){
            gui.writeMessage(progress.get(0));
            
            // System.out.println("write: " + progress.get(0));
            progress.remove(0);
        }
    }
}
