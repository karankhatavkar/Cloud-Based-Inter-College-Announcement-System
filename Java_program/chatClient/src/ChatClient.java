import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by karan on 4/09/2021
 *
 */

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners =new ArrayList<>();

    ChatClient(String serverName , int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /*public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String userId) {
                System.out.println("ONLINE: " + userId);
            }

            @Override
            public void offline(String userId) {
                System.out.println("OFFLINE: " + userId);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromUserId, String messageBody) {
                System.out.println("You got a message from " + fromUserId + " : " + messageBody);
            }
        });

        if(!client.connect()){
            System.err.println("Connection failed.");
        } else {
            System.out.println("Connection successful");

            if(client.login("jim" , "jim")){
                System.out.println("Login successful");

                client.sendMessage("pam", "hi pam");
            }else {
                System.err.println("Login failed");
            }
            //client.logoff();
        }

    }
*/
    public boolean login(String userId, String password) throws IOException {
        String cmd = "login " + userId + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line : " + response);
        if("ok login".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        }else {
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "login\n";
        serverOut.write(cmd.getBytes());
    }

    public void sendMessage(String sendTo, String messageBody) throws IOException {
        String data = "message " + sendTo + " " + messageBody + "\n";
        serverOut.write(data.getBytes());
    }

    private void startMessageReader() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                readMessageLoop();
            }
        };
    }

    private void readMessageLoop(){
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("message".equalsIgnoreCase(cmd)) {
                        String[] tokensMessage = StringUtils.split(line, null, 3);
                        handleMessage(tokensMessage);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOnline(String[] tokens) {
        String userid = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(userid);
        }
    }

    private void handleOffline(String[] tokens) {
        String userid = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(userid);
        }
    }

    private void handleMessage(String[] tokensMessage) {
        String userId = tokensMessage[1];
        String messageBody = tokensMessage[2];

        for(MessageListener listener : messageListeners){
            listener.onMessage(userId , messageBody);
        }
    }


    public boolean connect() {
        try {
            this.socket = new Socket(this.serverName , this.serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    public void addMessageListener(MessageListener listener) { messageListeners.remove(listener); }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }
    public void addUserStatusListener(MessageListener listener) { messageListeners.add(listener); }
}
