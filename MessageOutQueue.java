import java.util.ArrayList;

public class MessageOutQueue {
    private ArrayList<String> messages;

    public MessageOutQueue(){
        messages = new ArrayList<String>();
    }

    public boolean isEmpty(){
        if (messages.size() == 0) return true;
        return false;
    }

    public synchronized void addMessage(String message){
        messages.add(message);
    }

    public synchronized String popMessage(){
        if (messages.size() == 0){
            return null;
        } else {
            String message = messages.get(0);
            messages.remove(0);
            return message;
        }
    }

    public synchronized void printAll(){
        if (messages.size() > 0){
            for(int i=0; i < messages.size()-1; i++){
                System.out.print(messages.get(i));
                System.out.print(", ");
            }
            System.out.println(messages.get(messages.size()-1));
        }
    }

    public static void main(String[] args){
        MessageOutQueue ex = new MessageOutQueue();
        ex.addMessage("message");

        System.out.println(ex.isEmpty());

        // ex.printAll();
    }
}
