import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
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
            // takes input from the client socket
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());//sends output to the client socket

            String client= in.readUTF();
            System.out.println(client+" just logged in!");



            dout.writeInt(messageList.size());

            // System.out.println("sent id");
            
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

                        String signature = in.readUTF();

                        boolean verified = verifySignature(senderId, timestamp, msgBody, signature);

                        if(verified){
                            message.add(senderId);
                            message.add(msgBody);
                            message.add(timestamp);
    
                            messageList.add(message);

                            dout.writeUTF("Post added successfully!");
                            System.out.println("post added");
                            
                        }else{dout.writeUTF("invalid signature");
                    System.out.println("invalid singature");}


                    }catch(Exception ignored){}
                    }
            }

            }
            catch(IOException i){
                i.printStackTrace();
            }
    }

    static boolean verifySignature(String userid, String timestamp, String encMsg, String signature) throws Exception{
        // Get the key to create the signature
        PublicKey pub;
        
        ObjectInputStream obin = new ObjectInputStream(new FileInputStream(userid + ".pub"));
        pub = (PublicKey)obin.readObject();
        obin.close();


        //converts arguments into byte array
        byte[] byteTimeStamp = timestamp.getBytes();
        byte[] byteEncMsg = encMsg.getBytes();
        byte[] byteSignature = Base64.getDecoder().decode(signature);

        Signature sig = Signature.getInstance("SHA1withRSA"); 
        sig.initVerify(pub);

        //concats all byte array
        byte[] newSig = new byte[byteTimeStamp.length + byteEncMsg.length];

        System.arraycopy(byteTimeStamp, 0, newSig, byteTimeStamp.length, 0);
        System.arraycopy(byteEncMsg, 0, newSig, byteEncMsg.length, 0);

        sig.update(newSig); 

        return sig.verify(byteSignature);
    }

}

