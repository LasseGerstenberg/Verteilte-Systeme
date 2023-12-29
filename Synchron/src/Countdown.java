import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Countdown extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private int remainingCountdown = 100;

    public Countdown(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        try {
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while (remainingCountdown > 0) {
                TimeUnit.SECONDS.sleep(1);
                remainingCountdown--;
                if(in.ready()) {
                    inputLine = in.readLine(); // Read message from client
                    if (inputLine.equals("remaining")) {
                        out.println(String.valueOf(remainingCountdown));
                    } else if (inputLine.startsWith("set")) {
                        String[] splitString = inputLine.split(" ");
                        remainingCountdown = Integer.parseInt(splitString[1]);
                        out.println("Set countdown to " + String.valueOf(remainingCountdown) + " seconds");
                    } else if(inputLine.equals("cancel")) {
                        remainingCountdown = 0;
                    }
                }
            }
            out.println("canceled");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
