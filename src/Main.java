// import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    // static final String IP = "localhost";
    static final String IP = "192.168.0.104";
    static final int PORT = 6174;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started.");
        System.out.println("Listening for connections on port " + PORT + "...\n");

        while(!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            new Thread(new RequestHandler(socket)).start();
        }

        serverSocket.close();
    }
}