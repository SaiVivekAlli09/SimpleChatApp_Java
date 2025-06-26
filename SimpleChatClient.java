import java.io.*;
import java.net.*;

public class SimpleChatClient {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost"; // or IP address
        int port = 12345;
        Socket socket = new Socket(serverAddress, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        // Read server prompt for username
        System.out.print(in.readLine() + " ");
        String username = userInput.readLine();
        out.println(username);

        // Start a thread to read messages from server
        new Thread(() -> {
            String msgFromServer;
            try {
                while ((msgFromServer = in.readLine()) != null) {
                    System.out.println(msgFromServer);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }).start();

        // Main thread: send messages to server
        String message;
        while ((message = userInput.readLine()) != null) {
            out.println(message);
            if (message.equalsIgnoreCase("bye")) {
                break;
            }
        }
        socket.close();
        System.exit(0);
    }
}
