import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server{
    public static void main(String args[])
    {
        // while(true){
        // try
        // {
            Server server = new Server(5000);

            server.handleClient();
        
    //         System.out.println("Waiting for a client ...");
    //         Socket socket = ss.server.accept();

    //         DataInputStream in = new DataInputStream(socket.getInputStream());

    //         String client= in.readUTF();
    //         System.out.println(client+" just logged in!");

    //         DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
    //         dout.writeInt(ss.messageList.size());

    //         if(messageList.size()>0){
    //             for(int i = 0; i<messageList.size(); i++){
    //                 dout.writeUTF(messageList.get(i).get(0));
    //                 dout.writeUTF(messageList.get(i).get(1));
    //                 dout.writeUTF(messageList.get(i).get(2));
    //             }
    //         }

    //     while(!input.equals("Bye")){
    //         input = in.readUTF();
    //         if(input.equals("Bye")){
    //             System.out.println(client+" just logged out");
    //             System.out.println("----------*****------******----------\n\n");
                
    //             // close connection
    //             socket.close();
    //             // in.close();
    //     }else{

    //             System.out.println("receiving a message from "+client);
    //             ss.handleClient(socket, client, messageList, message);
    //         }

                
                
    //         }
    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    // }

    }


    // private Socket socket   = null;
    private ServerSocket server   = null;
    // private DataInputStream in       =  null;
    // private DataOutputStream dout;
    // private ObjectInputStream obin;
    // private ObjectOutputStream obout;

    static ArrayList<ArrayList<String>> messageList = new ArrayList<ArrayList<String>>(); //ArrayList to store Message objects
    static ArrayList<String> message;




    // constructor with port
    public Server(int port)
    {
        // starts server
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

        }
        catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    // waiting for a connection
    void handleClient(){
        try
        {
            System.out.println("Waiting for a client ...");
            Socket socket = server.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            String client= in.readUTF();
            System.out.println(client+" just logged in!");




            // takes input from the client socket


            dout.writeInt(messageList.size());

            System.out.println("sent id");
            
            if(messageList.size()>0){
                for(int i = 0; i<messageList.size(); i++){
                    dout.writeUTF(messageList.get(i).get(0));
                    dout.writeUTF(messageList.get(i).get(1));
                    dout.writeUTF(messageList.get(i).get(2));
                }
            }

            String input = "";
            // reads message from client until "Bye" is sent
            while(true){
                // ObjectInputStream obin = new ObjectInputStream(socket.getInputStream());
                // Message msg = (Message)obin.readObject();
                if(input.equals("Bye")){
                    System.out.println(client+" just logged out");
                    System.out.println("----------*****------******----------\n\n");
                    
                    // close connection
                    socket.close();
                    in.close();
                    this.handleClient();
                }else{
                    input = in.readUTF();

                    System.out.println("receiving a message from "+client);
                    message = new ArrayList<String>();
                    try{
                        String senderId = in.readUTF();
                        String msgBody = in.readUTF();
                        String timestamp = in.readUTF();

                        message.add(senderId);
                        message.add(msgBody);
                        message.add(timestamp);

                        messageList.add(message);
                        System.out.println(messageList);
                        System.out.println("sender: "+message.get(0));
                        System.out.println("message: "+message.get(1));
                        System.out.println("dateTime: "+message.get(2));  

                    }catch(Exception ignored){}
                    }
            }

            }
            catch(IOException i){
                i.printStackTrace();
            }
    }
}


// class Message{
//     public String senderId;
//     public String message;
//     public Date timestamp;

//     public String getSenderId() {
//         return this.senderId;
//     }
 
//     public Date getTimeStamp() {
//         return this.timestamp;
//     }
 
//     public String getMessage() {
//         return this.message;
//     }
// }

