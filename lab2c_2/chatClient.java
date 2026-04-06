package lab2c_2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class chatClient {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1";
        final int PORT = 12345;

        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connected to server " + SERVER_IP + ":" + PORT);

            while (true) {
                System.out.print("Client: ");
                String clientMessage = keyboard.readLine();
                out.println(clientMessage);

                if (clientMessage.equalsIgnoreCase("bye")) {
                    System.out.println("Client end the chat");
                    break;
                }

                String serverReply = in.readLine();
                if (serverReply == null) {
                    break;
                }

                System.out.println("Server: " + serverReply);

                if (serverReply.equalsIgnoreCase("bye")) {
                    System.out.println("Server closed");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}