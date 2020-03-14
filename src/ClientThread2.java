import java.io.*;
import java.net.Socket;

public class ClientThread2 implements Runnable{
    Socket socket;
    String filePath;

    public ClientThread2(Socket socket, String filePath) {
        this.socket = socket;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            File file = new File(filePath);
            if (!file.isFile())
            {
                return;
            }
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.write("UPLOAD "+file.getName()+"\r\n");
            pw.write("Content-Length: "+ file.length()+ "\r\n");
            pw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = br.readLine();
            System.out.println(response);

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[4096];

            int len = 0;
            
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
                // System.out.println(len);
            }
            bos.flush();
            fis.close();
            socket.shutdownOutput();

            response = br.readLine();
            System.out.println(response);

            socket.close();
            /* 

            String response = readLine(bis);
            System.out.println(response);
             
            bos.close();
            */
            System.out.println("FINISHED");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}