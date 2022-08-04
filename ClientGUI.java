import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ClientGUI implements ActionListener{

    private JFrame frame;

    // Set Name element
    private JPanel getNamePanel;
    private JTextArea enterNameField;
    private Button connectButton;

    // Chat Interface elements
    private JScrollPane chatHistory;
    private JTextArea chatHistoryText, sendMessageText;
    private JButton sendButton;

    // Fonts used for styling
    Font f1  = new Font(Font.DIALOG, Font.PLAIN,  20);
    Font f2 = new Font(Font.DIALOG_INPUT, Font.PLAIN, 16);

    private String chatName;
    private MessageOutQueue messageOutQueue;
    private SwingWorker<String, String> serverConnection;

    
    public ClientGUI(){
        messageOutQueue = new MessageOutQueue();

    }

    public void drawSetNameInterface() {
        JLabel label = new JLabel("Enter the chat Name you would like to use: ");
        label.setFont(f1);

        enterNameField = new JTextArea(1, 20);
        enterNameField.setFont(f2);
        enterNameField.setDocument(new JTextFieldLimit(18)); // This limits the amount of characters allowed in the textbox to 18.

        connectButton = new Button("Connect");
        connectButton.setFont(f1);
        connectButton.setPreferredSize(new Dimension(100, 30));
        connectButton.addActionListener(this);

        getNamePanel = new JPanel();

        getNamePanel.add(label);
        getNamePanel.add(Box.createVerticalStrut(50));
        getNamePanel.add(enterNameField);
        getNamePanel.add(Box.createVerticalStrut(50));
        getNamePanel.add(connectButton);

        frame = new JFrame();
        frame.setSize(540, 480);
        frame.setTitle("Chat Client");
        Image icon = Toolkit.getDefaultToolkit().getImage("ChatIcon.png");    
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(getNamePanel);
        frame.setVisible(true);
    }

    public void drawChatInterface() {

        // This display messages sent in the chat.
        chatHistoryText = new JTextArea(20, 50);
        chatHistoryText.setEditable(false);
        chatHistoryText.setLineWrap(true);
        chatHistoryText.setWrapStyleWord(true);
        chatHistory = new JScrollPane(chatHistoryText);

        // This is where the user types in the text they want to send.
        sendMessageText = new JTextArea( 3, 20);
        sendMessageText.setLineWrap(true);
        sendMessageText.setWrapStyleWord(true);

        // send button
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout());
        messagePanel.add(sendMessageText);
        messagePanel.add(sendButton);

        JPanel chatInterface = new JPanel();
        chatInterface.setLayout(new BorderLayout());
        chatInterface.add(chatHistory, BorderLayout.NORTH);
        chatInterface.add(messagePanel);
        // chatInterface.pack();
        frame.remove(getNamePanel);
        frame.add(chatInterface);
        frame.pack();
    }

    public void writeMessage(String message){
        chatHistoryText.append(message + "\n");
    }

    public void connect(){
        // This creates and starts a swing worker thread which is used to publish messages to the server that the user wants to send.
        serverConnection = new ClientConnectionWorkerThread("localhost", chatName, messageOutQueue, this);
        serverConnection.execute();
    }

    public void reportServerDisconnect(){
        writeMessage("Server Disconnected :(");
    }

    public void reportFailureToConnect(){
        writeMessage("Connection to server Failed :(");
        writeMessage("Please close the chatClient and check if the server is running");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // If the user clicks the connect button it sets the name entered and attempts to connect the user to the server.
        if (e.getSource() == connectButton) {
            chatName = enterNameField.getText();
            drawChatInterface();
            connect();
        }

        // If the user clicks the send button it will copy the users text and add it to the message out queue.
        if (e.getSource() == sendButton) {
            String message = sendMessageText.getText();
            writeMessage("me: " + message);
            sendMessageText.setText("");
            synchronized(messageOutQueue){
                messageOutQueue.addMessage(message);
                messageOutQueue.notify();
            }
        }
    }

    public static void main(String[] args){
        // This creates the chatters Graphical user Interface and connects them to the server.
        ClientGUI ex = new ClientGUI();
        ex.drawSetNameInterface();
    }
}
