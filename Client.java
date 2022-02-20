import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Client {
    private static Socket socket = null;
    // private BufferedReader bufferedReader = null;
    // private DataInputStream dins = null;
    // private DataOutputStream dout = null;
    // private ObjectInputStream obin;
    // public Message msg;
    private static PublicKey pub;
    private static PrivateKey prv;
    public static Date timestamp;


    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String userId = args[2];

        System.out.println("Host: "+host);
        System.out.println("Port: "+port);
        System.out.println("UserId: "+userId);
        System.out.println("----------*****------*****----------\n\n");

        try{
            socket = new Socket(host, port);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            if(validUserId(userId)){
                System.out.println("You are now Connected");
                dout.writeUTF(args[2]);
                printServerMessages(socket, userId);
                collectUserMessage(userId);
            }else{System.out.println("Invalid userId");socket.close();}

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    static void collectUserMessage(String userId) throws IOException{
        Scanner sc = new Scanner(System.in);
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        do{
        System.out.print("do u want to send Post (y/n)?: ");
        String input = sc.nextLine();

        if(input.equals("y")||input.equals("yes")||input.equals("Y")){
            try{
                dout.writeUTF(input);

                dout.writeUTF(userId);

                System.out.print("who do u want to send this Post to?(type 'all' if the msg is public): ");
                String recipientId = sc.nextLine();

                System.out.print("Enter the message: ");
                String body = sc.nextLine();

                String encriptedMsg = "";
                if(recipientId.equals("all")){
                    encriptedMsg = body;
                }else if(validUserId(recipientId)){
                    encriptedMsg = encriptMsg(recipientId, body);
                }else{System.out.println("invalid useId");continue;}

                dout.writeUTF(encriptedMsg);
                

                timestamp = new Date();
                dout.writeUTF(timestamp.toString());

                dout.writeUTF(signature(userId, timestamp.toString(), encriptedMsg));

                DataInputStream dins = new DataInputStream(socket.getInputStream());
                System.out.println(dins.readUTF());

            }catch(Exception ignored){}
        }else{
            try{
                dout.writeUTF("Bye");
            }catch(Exception e){e.printStackTrace();}
            // sendFlag = false;
            break;
        }

        }while(true);
    }

    static void printServerMessages(Socket s, String userId){
        try{
            DataInputStream dins = new DataInputStream(s.getInputStream());

            int msgnum = dins.readInt();//receives total number of message from server
            System.out.println("You have received "+msgnum+" Posts.");

            if(msgnum>0){
                for(int i =0; i<msgnum; i++){
                    String senderId = dins.readUTF();
                    String msgBody = decryptMsg(userId, dins.readUTF());
                    String timestamp = dins.readUTF();

                    System.out.println("Post ."+(int)(i+1));
                    System.out.println("sender: "+senderId);
                    System.out.println("message: "+msgBody);
                    System.out.println("dateTime: "+timestamp);
                    System.out.println("----------*****------*****----------\n");
                }
                }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static String signature(String userid, String timestamp, String encMsg ) throws Exception{ //undone
        // Get the key to create the signature
        ObjectInputStream obin = new ObjectInputStream(new FileInputStream(userid + ".prv"));
        prv = (PrivateKey)obin.readObject();
        obin.close();


        //converts arguments into byte array
        byte[] byteTimeStamp = timestamp.getBytes();
        byte[] byteEncMsg = encMsg.getBytes();

        Signature sig = Signature.getInstance("SHA1withRSA"); 
        sig.initSign(prv);

        //concats all byte array
        byte[] newSig = new byte[byteTimeStamp.length + byteEncMsg.length];

        System.arraycopy(byteTimeStamp, 0, newSig, byteTimeStamp.length, 0);
        System.arraycopy(byteEncMsg, 0, newSig, byteEncMsg.length, 0);

        sig.update(newSig); 

        byte[] signature = sig.sign();

        return stringIt(signature);
    }


    static PublicKey getPubKey(String userId){
        try{
            File pubFile = new File(userId+".pub");
            ObjectInputStream obin = new ObjectInputStream(new FileInputStream(pubFile));
            pub = (PublicKey)obin.readObject();
            obin.close();
            // System.out.println(publicKey);
            
        }catch(Exception ignored){
            // e.printStackTrace();
        }
        return pub;

    }

    static PrivateKey getPrivateKey(String userId){
        try{
            File privFile = new File(userId+".prv");
            ObjectInputStream obin = new ObjectInputStream(new FileInputStream(privFile));
            prv = (PrivateKey) obin.readObject();

            obin.close();
        }catch(Exception ignored){}
        return prv;
    }

    static String encriptMsg(String rcid, String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytemsg = msg.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PublicKey pubkey = getPubKey(rcid);
        cipher.init(Cipher.ENCRYPT_MODE, pubkey);
        
        byte[] encBytemsg = cipher.doFinal(bytemsg);
        String encMsg = stringIt(encBytemsg);

        return encMsg;
    }

    static String decryptMsg(String rcid, String encMsg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException{
        try{
            byte[] bytemsg = byteIt(encMsg);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PrivateKey pvkey = getPrivateKey(rcid);
            cipher.init(Cipher.DECRYPT_MODE,pvkey);

            byte[] dycBytemsg = cipher.doFinal(bytemsg);

            return new String(dycBytemsg, "UTF8");

        }catch(IllegalArgumentException | BadPaddingException e){
            // e.printStackTrace();
            System.out.println("this post was not sent you you");
            return encMsg;
        }
    }

    static String stringIt(byte[] input){
        return Base64.getEncoder().encodeToString(input);
    }

    static byte[] byteIt(String input){
        return Base64.getDecoder().decode(input);
    }

    static boolean validUserId(String userId){
        File file = new File(userId+".pub");

        return file.exists();
    }

}

