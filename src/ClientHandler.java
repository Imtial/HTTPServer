import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientHandler implements Runnable {
    Socket socket;
    BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String request = null;
        // while(this.socket.isConnected()) {
            try {
                request = in.readLine(); 
                System.out.println(request);   
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if(request == null) {
                // continue;
                // try {
                //     socket.close();
                // } catch (IOException e) {
                //     e.printStackTrace();
                // }
                return;
            }

            if(request.length() > 0) {
                new RequestHandler(socket, request).run();
            }
        // }
        // try {
        //     socket.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}