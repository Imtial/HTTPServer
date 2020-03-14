import java.io.*;
import java.net.*;

public class TestServer2 {
    static final int PORT = 6174;

    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket socket;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
            socket = serverSocket.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = br.readLine();
            System.out.println(request);

            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.write("HTTP/1.1 202 ACCEPTED\r\n");
            pw.flush();

            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

            FileOutputStream fos = new FileOutputStream("OUT.docx");

            byte [] buf = new byte[4096];
            int len = 0;
            while((len = bis.read(buf)) > 0) {
                fos.write(buf, 0, len);
                System.out.println(len);
            }
            fos.close();
            socket.shutdownInput();
            
            pw.write("HTTP/1.1 201 CREATED\r\n");
            pw.flush();


            socket.close();
            serverSocket.close();
            System.out.println("FINISHED");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}