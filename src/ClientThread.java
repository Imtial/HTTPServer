import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable{
    Socket socket;
    String filePath;

    public ClientThread(Socket socket, String filePath) {
        this.socket = socket;
        this.filePath = filePath;
    }

    String readLine(BufferedInputStream in) {
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

    @Override
    public void run() {
        try {
            File file = new File(filePath);
            if (!file.isFile())
            {
                return;
            }
            OutputStream os = socket.getOutputStream();

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            bos.write(("UPLOAD "+file.getName()+"\r\n").getBytes());
            bos.write(("Content-Length: "+ file.length()+ "\r\n").getBytes());
            bos.flush();

            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[4096];

            int content = 0;
            
            while ((content = fis.read(buf)) > 0) {
                bos.write(buf, 0, content);
                System.out.println(content);
            }
            bos.flush();
            fis.close();

            String response = readLine(bis);
            System.out.println(response);
            
            bos.close();
            System.out.println("FINISHED");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}