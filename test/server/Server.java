import java.io.*;
import java.net.*;

public class Server implements Runnable {

    public static String ServerIp;
    public static final int ServerPort = 7777;

    @Override
    public void run() {
        try {
            System.out.println("SERVER: Connectiong...");
            ServerSocket serverSocket = new ServerSocket(ServerPort);
            System.out.println("[ " + serverSocket.getInetAddress() + " ] server socket starting!");

            while (true) {
                // client 접속 대기
                Socket client = serverSocket.accept();
                System.out.println("[ " + client.getInetAddress() + " ] client connected");
                System.out.println("SERVER: Receiving...");

                try {
                    // client data 수신
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    System.out.println("SERVER: Received: " + str);
                } catch (Exception e) {
                    System.out.println("SERVER: ERROR on Receiving");
                    e.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("SERVER: DONE");
                }
            }
        } catch (Exception e) {
            System.out.println("SERVER: ERROR on Connecting");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Thread serverThread = new Thread(new Server());
        serverThread.start();
    }
}
