import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;

public class RequestHandler /*implements Runnable*/ {
    static final String root = "root";
    static final int BUFFER_SIZE = 4096;
    private Socket socket;
    private String request;
    private String requestType;
    private String requestedPath;
    String content;

    public RequestHandler(Socket socket, String request) {
        this.socket = socket;
        this.request = request;
    }

    private String htmlGenerator(File file) {
        String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body style=\"font-size:40px;\"><h1>Welcome to CSE 322 Offline 1</h1>";

        html += "<ul style=\"list-style-type:square\">";
        for (File f : file.listFiles()) {
            String link = f.toString().substring(f.toString().indexOf("/") + 1);
            String fname = link.substring(link.lastIndexOf("/") + 1);
            if (f.isDirectory()) {
                html += "<li><b><a href=\"http:////" + Main.IP + ":" + Main.PORT + "/" + link + "/\">" + fname
                        + "</a></b></li>";
            } else if (f.isFile()) {
                html += "<li><a href=\"http:////" + Main.IP + ":" + Main.PORT + "/" + link + "\">" + fname + "</li>";
            }
        }
        html += "</ul>";
        html += "</body></html>";

        return html;
    }

    private void sendResponse(String status, String contentType, String content) {
        try {
            PrintWriter pr = new PrintWriter(socket.getOutputStream());
        
            pr.write(status + "\r\n");
            pr.write("Server: Java HTTP Server: 1.0\r\n");
            pr.write("Date: " + new Date() + "\r\n");
            if (content == null) {
                pr.write("Content-Length: 0\r\n");
            } else {
                pr.write("Content-Type: " + contentType + "\r\n");
                pr.write("Content-Length: " + content.length() + "\r\n");
            }
            pr.write("\r\n");
            if (content != null)
                pr.write(content);
            pr.flush();
            pr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(File file) {
        
        long size = file.length();
        byte [] buffer = new byte[BUFFER_SIZE];
        try {
            PrintWriter pr = new PrintWriter(socket.getOutputStream());
            
            FileInputStream fis = new FileInputStream(file);
            pr.write("HTTP/1.1 200 OK\r\n");
            pr.write("Server: Java HTTP Server: 1.0\r\n");
            pr.write("Date: " + new Date() + "\r\n");
            String mimeType = Files.probeContentType(file.toPath());
            pr.write("Content-Type: "+ mimeType+ "\r\n");
            pr.write("Content-Length: "+ size+ "\r\n");
            pr.write("Content-Transfer-Encoding: binary\r\n");
            pr.write("Content-Disposition: attachment; filename=\""+ file.getName() +"\"\r\n");
            pr.write("\r\n");
            pr.flush();

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            int len;
            while((len = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, len);
            }
            dos.flush();
            pr.close();
            dos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Override
    public void run() { 
        String requestSplit[] = request.split(" ");
        requestType = requestSplit[0];
        requestedPath = requestSplit[1];      
        if(requestType.equals("GET")) {
            File requestedFile = new File(root + requestedPath);
            if(!requestedFile.exists()) {
                content = null;
                sendResponse("HTTP/1.1 404 NOT FOUND", null, content);
            }
            if(requestedFile.isDirectory()) {
                content = htmlGenerator(requestedFile);
                sendResponse("HTTP/1.1 200 OK", "text/html", content);
            } else {
                sendFile(requestedFile);
            }
        }        
    }
}