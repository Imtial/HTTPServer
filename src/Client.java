import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        String ip = "192.168.0.104";
        int port = 6174;

        System.out.print("How many files? : ");
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        scanner.nextLine();
        for(int i=1; i<=n; i++) {
            System.out.print("Enter #"+i+" File Path: ");
            String path = scanner.nextLine();
            Socket socket = new Socket(ip, port);

            new Thread(new ClientThread(socket, path)).start();
        }
        scanner.close();
    }

}