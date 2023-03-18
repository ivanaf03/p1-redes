package es.udc.redes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/** Multithread TCP echo server. */

public class WebServer {
    public static void main(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
            System.exit(-1);
        }
        ServerSocket serverSocket;
        Socket client=null;
        try {
            serverSocket= new ServerSocket(Integer.parseInt(argv[0]));
            // Create a server socket
            serverSocket.setSoTimeout(300000);
            // Set a timeout of 300 secs
            while (true) {
                client = serverSocket.accept();
                // Wait for connections
                ServerThread2 thread = new ServerThread2(client);
                // Create a ServerThread object, with the new connection as parameter
                thread.start();
                // Initiate thread using the start() method
            }
            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            assert client != null;
            client.close();
        }
    }
}
