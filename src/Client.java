import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        String ip = "192.168.0.104";
        int port = 6174;
        // int port = 9999;
/* 
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
        scanner.close(); */

        new Thread(new ClientThread2(new Socket(ip, port), "/home/imtial/Downloads/TextAdventure.docx")).start();
        new Thread(new ClientThread2(new Socket(ip, port), "/home/imtial/Downloads/epdf.pub_c-programming-for-microcontrollers.pdf")).start();
        new Thread(new ClientThread2(new Socket(ip, port), "/home/imtial/Downloads/'A Game Of Thrones - George RR Martin.epub'")).start();
    }

}