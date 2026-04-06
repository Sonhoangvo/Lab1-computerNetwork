package lab2c_3;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running = false;

    public ChatClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void startClient() {
        running = true;
        Scanner scanner = new Scanner(System.in);

        try {
            // Nhận yêu cầu nhập tên từ server
            String serverMsg = in.readLine();
            System.out.println(serverMsg);

            String name = scanner.nextLine();
            out.println(name);

            // Luồng nhận tin nhắn từ server
            Thread listenerThread = new Thread(() -> {
                try {
                    String msg;
                    while (running && (msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Disconnected from server.");
                    }
                }
            });

            listenerThread.start();

            // Luồng chính: gửi tin nhắn
            while (running) {
                String message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopClient();
            scanner.close();
        }
    }

    public void stopClient() {
        running = false;

        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (out != null) out.close();

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        try {
            ChatClient client = new ChatClient(host, port);
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
