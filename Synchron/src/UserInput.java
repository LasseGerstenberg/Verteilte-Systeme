import java.io.*;
import java.net.*;

public class UserInput extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String address;
    private int port;

    public UserInput(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Loop for continuous message exchange
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while (true) {
                checkIfCountdownIsCancled(in);
                System.out.println("Enter command (set X/cancel/remaining):");
                checkIfCountdownIsCancled(in);
                userInput = stdIn.readLine();
                checkIfCountdownIsCancled(in);
                out.println(userInput); // Send message to server
                checkIfCountdownIsCancled(in);
                handleResponse(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleResponse(String response) {
        if(response.equals("canceled")) {
            stopExecution();
        } else {
            System.out.println("Server response: " + response);
        }
    }

    private void checkIfCountdownIsCancled(BufferedReader in) throws IOException {
        if(in.ready()) {
            if (in.readLine().equals("canceled")) {
                stopExecution();
            }
        }
    }

    private void stopExecution() {
        System.out.println("The Countdown finished!");
        System.exit(1);
    }
}
