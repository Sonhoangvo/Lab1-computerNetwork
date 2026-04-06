package lab2c_3;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    // Danh sách client đang kết nối
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void startServer() {
        running = true;
        System.out.println("Server is running...");

        try {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);

                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    clients.add(handler);
                    handler.start();

                } catch (IOException e) {
                    if (!running) {
                        System.out.println("Server stopped.");
                        break;
                    }
                    e.printStackTrace();
                }
            }
        } finally {
            stopServer();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void stopServer() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        try {
            ChatServer server = new ChatServer(port);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            // Dòng đầu tiên client gửi lên là tên
            out.println("Enter your name:");
            clientName = in.readLine();

            if (clientName == null || clientName.trim().isEmpty()) {
                clientName = "Anonymous";
            }

            System.out.println(clientName + " joined.");
            server.broadcast("[SERVER] " + clientName + " joined the chat.", this);

            String message;
            while (running && (message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                String fullMessage = clientName + ": " + message;
                System.out.println(fullMessage);
                server.broadcast(fullMessage, this);
            }

        } catch (IOException e) {
            System.out.println(clientName + " disconnected.");
        } finally {
            server.removeClient(this);
            server.broadcast("[SERVER] " + clientName + " left the chat.", this);
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() {
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
}