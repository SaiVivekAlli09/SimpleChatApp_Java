import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws IOException {
        System.out.println("SimpleChat Server started...");
        ServerSocket serverSocket = new ServerSocket(12345);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket);
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Add this client's writer to the set
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // First message from client is the username
                out.println("Enter your username:");
                userName = in.readLine();

                broadcast(">> " + userName + " joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("bye")) {
                        break;
                    }
                    broadcast(userName + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) {}
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                broadcast(">> " + userName + " left the chat.");
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
// This code implements a simple chat server in Java that allows multiple clients to connect and communicate with each other.
// Clients can send messages, and the server broadcasts these messages to all connected clients.