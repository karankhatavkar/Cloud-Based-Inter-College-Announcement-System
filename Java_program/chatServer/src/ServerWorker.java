import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String userId = null;
    private OutputStream outputStream;


    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
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
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null){
            String[] tokens = line.split("\\s+");
            if(tokens.length>0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                }else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                }else if ("message".equalsIgnoreCase(cmd)){
                    String[] tokenMessage = line.split("\\s+" , 3);
                    if(tokenMessage.length == 3) { handleMessage(tokenMessage); }
                }
                else {
                    String msg = "Unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }


    public String getUserId(){
        return userId;
    }

    private void send(String message) throws IOException{
        if(userId != null){
            outputStream.write(message.getBytes());
        }
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
                System.out.println("User logged in successfully : " + userId);


                List<ServerWorker> workerList = server.getWorkerList();

                //send user all other online logins
                for(ServerWorker worker : workerList){
                    if(worker.getUserId() !=  null){
                        if(! this.userId.equals(worker.getUserId())){
                            String alreadyOnline = "\nonline - " + worker.getUserId() + "\n";
                            send(alreadyOnline);
                        }
                    }
                }

                //send other online users current user's status
                String onlineMessage = "\nonline - " + this.userId + "\n";
                for(ServerWorker worker : workerList){
                    if(! this.userId.equals(worker.getUserId())){
                        worker.send(onlineMessage);
                    }
                }
            }
            else {
                String msg = "error login";
                outputStream.write(msg.getBytes());
            }
        }
    }


    //format : "message" "userId" "Body" ....
    //format : "message" "#topic" "Body" ....
    private void handleMessage(String[] tokens) throws IOException{
        String sendTo = tokens[1];
        String body = tokens[2];

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList){
            if(sendTo.equalsIgnoreCase(worker.getUserId())){
                String outMessage = "From " + this.userId + " : " + "To " + sendTo + " : " + body + "\n";
                worker.send(outMessage);
            }
        }
    }

    private void handleLogoff() throws IOException{
        List<ServerWorker> workerList = server.getWorkerList();

        //send other online users current user's status
        String offlineMessage = "\nOffline - " + this.userId + " \n";
        for(ServerWorker worker : workerList){
            if(!this.userId.equals(worker.getUserId())){
                worker.send(offlineMessage);
            }
        }
        server.removeWorker(this);
        clientSocket.close();
    }


}
