import java.io.*;
import java.net.*;

public class SocketServer {

    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.out.println("Invalid command-line arguments with length " + args.length);
            System.exit(-1);
        }

        int serverPort = Integer.parseInt(args[0]);
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Server is listening on port " + serverPort);

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("[ " + socket.getInetAddress() + " ] client connected");
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

// client 접속 대기
Socket client = serverSocket.accept();
// client data 수신
BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

