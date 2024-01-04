import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UserInput extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String address;
    private int port;

    // A queue to hold the commands
    private LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    public UserInput(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a new thread for reading user input
            Thread inputThread = new Thread(() -> {
                try {
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                    String userInput;
                    while (true) {
                        System.out.println("Enter command (set X/cancel/remaining):");
                        userInput = stdIn.readLine();
                        commandQueue.put(userInput); // Store command in the queue
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            inputThread.start();

            // Main thread handles sending commands and server responses
            try {
                while (true) {
                    String commandToSend = commandQueue.take(); // Take the next command from the queue
                    out.println(commandToSend); // Send it to the server

                    // Wait for server response
                    String response = in.readLine();
                    if (response != null) {
                        handleResponse(response);
                    } else {
                        break; // Exit if the server closes the connection
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleResponse(String response) {
        if (response.equals("canceled")) {
            stopExecution();
        } else {
            System.out.println("Server response: " + response);
        }
    }

    private void stopExecution() {
        System.out.println("The Countdown finished!");
        System.exit(1);
    }
}
