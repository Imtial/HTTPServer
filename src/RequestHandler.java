import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;

public class RequestHandler implements Runnable {
    static final String root = "root";
    static final int BUFFER_SIZE = 4096;
    private Socket socket;
    private String request;
    private String requestType;
    private String requestedPath;
    String content;
    private Logger logger;
    BufferedReader in;

    public RequestHandler(Socket socket) {
        this.socket = socket;
        this.request = null;
        logger = new Logger();
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
            String logMessage = "";
            pr.write(status + "\r\n");
            logMessage += status + "\r\n";
            pr.write("Server: Java HTTP Server: 1.0\r\n");
            logMessage += "Server: Java HTTP Server: 1.0\r\n";
            pr.write("Date: " + new Date() + "\r\n");
            logMessage += "Date: " + new Date() + "\r\n";
            if (content == null) {
                pr.write("Content-Length: 0\r\n");
                logMessage += "Content-Length: 0\r\n";
            } else {
                pr.write("Content-Type: " + contentType + "\r\n");
                logMessage += "Content-Type: " + contentType + "\r\n";
                pr.write("Content-Length: " + content.length() + "\r\n");
                logMessage += "Content-Length: " + content.length() + "\r\n";
            }
            pr.write("\r\n");
            logMessage += "\r\n";
            if (content != null)
                pr.write(content);
            pr.flush();
            pr.close();
            logger.log("RESPONSE", logMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(File file) {

        long size = file.length();
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            PrintWriter pr = new PrintWriter(socket.getOutputStream());

            FileInputStream fis = new FileInputStream(file);
            String logMeassage = "";

            pr.write("HTTP/1.1 200 OK\r\n");
            logMeassage += "HTTP/1.1 200 OK\r\n";
            pr.write("Server: Java HTTP Server: 1.0\r\n");
            logMeassage += "Server: Java HTTP Server: 1.0\r\n";
            pr.write("Date: " + new Date() + "\r\n");
            logMeassage += "Date: " + new Date() + "\r\n";
            String mimeType = Files.probeContentType(file.toPath());
            pr.write("Content-Type: " + mimeType + "\r\n");
            logMeassage += "Content-Type: " + mimeType + "\r\n";
            pr.write("Content-Length: " + size + "\r\n");
            logMeassage += "Content-Length: " + size + "\r\n";
            pr.write("Content-Transfer-Encoding: binary\r\n");
            logMeassage += "Content-Transfer-Encoding: binary\r\n";
            pr.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n");
            logMeassage += "Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n";
            pr.write("\r\n");
            logMeassage += "\r\n";
            pr.flush();
            logger.log("RESPONSE", logMeassage);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            int len;
            while ((len = fis.read(buffer)) > 0) {
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

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = in.readLine();

            if (request == null) {
                in.close();
                return;
            }
            System.out.println(request);
            logger.log("REQUEST", request);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (request.length() > 0) {
            String requestSplit[] = request.split(" ");
            requestType = requestSplit[0];
            requestedPath = requestSplit[1].substring(requestSplit[1].indexOf("/")+1);
            if (requestType.equals("GET")) {
                File requestedFile = new File(root + File.separator + requestedPath);
                if (!requestedFile.exists()) {
                    content = null;
                    sendResponse("HTTP/1.1 404 NOT FOUND", null, content);
                }
                if (requestedFile.isDirectory()) {
                    content = htmlGenerator(requestedFile);
                    sendResponse("HTTP/1.1 200 OK", "text/html", content);
                } else {
                    sendFile(requestedFile);
                }
            } else if (requestType.equals("UPLOAD")) {
                try {
                    request = in.readLine();
                    System.out.println(request);
                    long size = Long.parseLong(request.substring(request.indexOf(" ")+1));
                    File f = new File(root+File.separator+requestedPath);

                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    pw.write("HTTP/1.1 202 ACCEPTED\r\n");
                    pw.flush();

                    BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

                    FileOutputStream fos = new FileOutputStream(f);
                    
                    byte [] buf = new byte[4096];
                    int len = 0;
                    while((len = bis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                        // System.out.println(len);
                    }
                    fos.close();
                    socket.shutdownInput();
                    if(size == f.length())
                        pw.write("HTTP/1.1 201 CREATED\r\n");
                    else 
                        pw.write("HTTP/1.1 409 Conflict\r\n");
                    pw.flush();

                    socket.close();

                    System.out.println("FINISHED");

                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }
            } 
        }      
    }
}