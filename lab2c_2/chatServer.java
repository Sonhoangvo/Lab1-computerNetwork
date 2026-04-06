package lab2c_2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class chatServer {
    public static void main(String[] args) {
        final int PORT = 12345;

        try (
            ServerSocket serverSocket = new ServerSocket(PORT);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Server is running at port " + PORT);

            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getInetAddress());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String clientMessage;
            while (true) {
                clientMessage = in.readLine();

                if (clientMessage == null) {
                    break;
                }

                System.out.println("Client: " + clientMessage);

                if (clientMessage.equalsIgnoreCase("bye")) {
                    System.out.println("Client close connection");
                    break;
                }

                System.out.print("Server: ");
                String serverMessage = keyboard.readLine();
                out.println(serverMessage);

                if (serverMessage.equalsIgnoreCase("bye")) {
                    System.out.println("Server ended the chat");
                    break;
                }
            }

            socket.close();
            System.out.println("Ended connection");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}