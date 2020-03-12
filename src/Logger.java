import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger {
    FileOutputStream fos;

    public Logger() {
        try {
            fos = new FileOutputStream("Log.txt", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(String type, String msg) {
        try {
            fos.write((type + ": " + msg + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}