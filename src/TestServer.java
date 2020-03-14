import java.io.*;
import java.net.*;

public class TestServer {
    static final int PORT = 6174;

    static String readLine(BufferedInputStream in) {
        try{
            StringBuilder sb = new StringBuilder();
            boolean eol = false;
            int i;
            while((i = in.read()) != -1) {
                char c = (char) i;
                if(c == '\r'){
                    eol = true;
                    continue;
                }
                if(eol) {
                    if(c == '\n') break;
                    else eol = false;
                }
                sb.append(c);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
            Socket socket = serverSocket.accept();
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            
            String request = readLine(bis);
            System.out.println(request);
            request = readLine(bis);
            System.out.println(request);
            long size = Long.parseLong(request.substring(request.indexOf(" ")+1));
            File file = new File("OUT.docx");
            FileOutputStream fos = new FileOutputStream(file);

            byte [] buf = new byte[4096];
            int len = 0;
            while((len = bis.read(buf)) > 0) {
                fos.write(buf, 0, len);
                System.out.println(len);
            }
            bis.close();
            fos.flush();
            fos.close();

            if(size == file.length()) {
                System.out.println("FILE OK");
                bos.write("HTTP/1.1 201 CREATED\r\n".getBytes());
                bos.flush();
            }
            bos.close();
            serverSocket.close();
            System.out.println("FINISHED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}