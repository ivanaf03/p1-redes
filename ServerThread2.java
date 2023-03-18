package es.udc.redes.webserver;
import java.net.*;
import java.io.*;

/** Thread that processes an echo server connection. */

public class ServerThread2 extends Thread {

    private final Socket socket;

    public ServerThread2(Socket s) {
        this.socket=s;
    }

    public void run() {
        OutputStream out;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Set the input channel
            out = socket.getOutputStream();
            // Set the output channel
            String mensaje;
            // Receive the message from the client
            while ((mensaje = in.readLine()) != null) {
                Http http=new Http(mensaje.split(" "), out, in);
                http.print();
            }
            // Sent the echo message to the client
            in.close();
            out.close();
            // Close the streams
            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
