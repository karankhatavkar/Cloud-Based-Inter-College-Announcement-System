import java.io.IOException;

public class chatMain {
    public static void main(String[] args) throws IOException {

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

        if (!client.connect()) {
            System.err.println("Connection failed.");
        } else {
            System.out.println("Connection successful");

            if (client.login("jim", "jim")) {
                System.out.println("Login successful");

                client.sendMessage("pam", "hi pam");

            } else {
                System.err.println("Login failed");
            }

            client.logoff();

        }
    }
}
