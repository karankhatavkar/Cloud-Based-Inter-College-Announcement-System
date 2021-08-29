import java.io.*;
import java.net.Socket;

public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private String userId = null;

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
        
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null){
            String[] tokens = line.split("\\s+");
            if(tokens != null && tokens.length>0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd)) {
                    break;
                }
                else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                }
                else {
                    String msg = "Unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length==3){
            String login = tokens[1];
            String password = tokens[2];

            if((login.equals("guest") && password.equals("guest"))
                    || (login.equals("pam") && password.equals("waste"))){
                String msg = "ok login";
                outputStream.write(msg.getBytes());
                this.userId = login;
                System.out.println("User logged in successfully : " + login);
            }
            else {
                String msg = "error login";
                outputStream.write(msg.getBytes());
            }
        }
    }
}
