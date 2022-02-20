import java.io.*;
import java.net.Socket;
import java.security.*;
import java.nio.file.*;
import java.security.spec.*;
import java.net.UnknownHostException;
import java.util.*;


public class Client {
    private static Socket socket = null;
    private BufferedReader bufferedReader = null;
    // private DataInputStream dins = null;
    // private DataOutputStream dout = null;
    // private ObjectInputStream obin;
    // public Message msg;
    private static PublicKey pub;
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
            System.out.println("You are now Connected");
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(args[2]);
            System.out.println("sent id");
            printServerMessages(socket);
            collectUserMessage(userId);

        }catch(Exception e){
            e.printStackTrace();
        }


        // String pubKey = Base64.getEncoder().encodeToString(getPulicKey(userId));
        

        // System.out.println("public key: "+ getPulicKey(userId));

        // Client client = new Client(host, port); 


        // boolean sendFlag = true;


    }

    static void collectUserMessage(String userId){
        Scanner sc = new Scanner(System.in);
        do{
        System.out.print("do u want to send msg (y/n)?: ");
        String input = sc.nextLine();

        if(input.equals("y")||input.equals("yes")||input.equals("Y")){
            try{
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                dout.writeUTF(input);

                dout.writeUTF(userId);

                System.out.println("Enter the message: ");
                String body = sc.nextLine();
                dout.writeUTF(body);

                // Message msg = new Message((String)userId, (String)body);
                // ArrayList<String> msg = new ArrayList<String>();
                // msg.add(userId);
                // msg.add(body);
                timestamp = new Date();
                dout.writeUTF(timestamp.toString());

                // msg.add(timestamp.toString());

                // ObjectOutputStream obout = new ObjectOutputStream(socket.getOutputStream());
                // obout.writeObject(msg);
            }catch(Exception ignored){}
        }else{
            try{
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                dout.writeUTF("Bye");
            }catch(Exception e){e.printStackTrace();}
            // sendFlag = false;
            break;
        }

        }while(true);
    }

    static void printServerMessages(Socket s){
        try{
            DataInputStream dins = new DataInputStream(s.getInputStream());

            int msgnum = dins.readInt();//receives total number of message from server
            System.out.println("You have received "+msgnum+" Messages.");

            if(msgnum>0){
                for(int i =0; i<msgnum; i++){
                    String senderId = dins.readUTF();
                    String msgBody = dins.readUTF();
                    String timestamp = dins.readUTF();

                    System.out.println("Message ."+(int)(i+1));
                    System.out.println("sender: "+senderId);
                    System.out.println("message: "+msgBody);
                    System.out.println("dateTime: "+timestamp);
                    System.out.println("----------*****------******----------\n");
                }
                }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // public Client(String host, int port) {
    //     try {
    //         socket = new Socket(host, port);
    //         System.out.println("You are now Connected");
    //         // bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    //         // DataInputStream dins = new DataInputStream(socket.getInputStream());
    //         // dout = new DataOutputStream(socket.getOutputStream());
    //     } catch (UnknownHostException e) {
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
        // String line = "";
        // while (!line.equals("y")) {
        //     try
        //     {
        //         line = bufferedReader.readLine();
        //         dout.writeUTF(line);
        //     }
        //     catch(IOException i)
        //     {
        //         System.out.println(i);
        //     }
        // }
    // }

    private static PublicKey getPulicKey(String userid){
        try{
            File pubFile = new File(userid+".pub");
            ObjectInputStream obin = new ObjectInputStream(new FileInputStream(pubFile));
            PublicKey publicKey = (PublicKey)obin.readObject();
            obin.close();
            System.out.println(publicKey);
            // FileInputStream file = new FileInputStream(pubFile);
            // // byte[] keyBytes = Files.readAllBytes(Paths.get(userid+".pub"));
            // byte[] keyBytes = new byte[(int)pubFile.length()];
            // // System.out.println(keyBytes);
            // obin.read(keyBytes);
            // obin.close();
            // X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            // // RSAPublicKeySpec spec = new RSAPublicKeySpec(keyBytes);
            // System.out.println(spec);
            // KeyFactory kf = KeyFactory.getInstance("RSA");

            // pub = kf.generatePublic(spec);
            
        }catch(Exception ignored){
            // e.printStackTrace();
        }

    //     Security.addProvider(new BouncyCastleProvider());

    //     KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
    //     try {
    //         // PrivateKey priv = generatePrivateKey(factory, RESOURCES_DIR
    //         //         + "id_rsa");
    //         // LOGGER.info(String.format("Instantiated private key: %s", priv));

    //         PublicKey pub = generatePublicKey(factory,pubFile);
    //         // LOGGER.info(String.format("Instantiated public key: %s", pub));
    //     } catch (InvalidKeySpecException e) {
    //         e.printStackTrace();
    //     }

    //     try( Reader r = new FileReader(userid+".pub") ){
    //         KeyPair pair = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair)new PEMParser(r).readObject());
    //         pub = pair.getPublic();
    //     }catch(Exception ignored){}


        return pub;
    // }
    // private static PublicKey generatePublicKey(KeyFactory factory,String filename) throws InvalidKeySpecException,
    // FileNotFoundException, IOException {
    //     PemFile pemFile = new PemFile(filename);
    //     byte[] content = pemFile.getPemObject().getContent();
    //     X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
    //     return factory.generatePublic(pubKeySpec);
    // }
    }

}

class Message{
    public String senderId;
    public String message;
    public Date timestamp;

    public Message(String userId, String message){
        senderId = userId;
        message = message;
        timestamp = new Date();
    }

    public String getSenderId() {
        return this.senderId;
    }
 
    public Date getTimeStamp() {
        return this.timestamp;
    }
 
    public String getMessage() {
        return this.message;
    }
}

