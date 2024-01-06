import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Countdown extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter countdownToUserInputStream;
    private BufferedReader userInputToCountdownStream;

    private int remainingCountdown = 1000;

    private boolean canceledByUser = false;

    public Countdown(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        try {
            clientSocket = serverSocket.accept();
            countdownToUserInputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            userInputToCountdownStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;

            //Schleife läuft, solange der Countdown läuft
            while (remainingCountdown > 0) {
                TimeUnit.SECONDS.sleep(1);
                remainingCountdown--;
                if(userInputToCountdownStream.ready()) {
                    inputLine = userInputToCountdownStream.readLine();
                    if (inputLine.equals("remaining")) {
                        countdownToUserInputStream.println(String.valueOf(remainingCountdown));
                    } else if (inputLine.startsWith("set")) {
                        String[] splitString = inputLine.split(" ");
                        remainingCountdown = Integer.parseInt(splitString[1]);
                        countdownToUserInputStream.println("Setze Countdown auf " + String.valueOf(remainingCountdown) + " Sekunden");
                    } else if(inputLine.equals("cancel")) {
                        canceledByUser = true;
                        remainingCountdown = 0;
                    }
                }
            }
            // wird ausgeführt, wenn der Countdown beendet ist
            String cancelMessage = canceledByUser ? "canceledByUser" : "canceledByTimer";
            countdownToUserInputStream.println(cancelMessage);
            if(canceledByUser) {
                countdownToUserInputStream.println("canceledByUser");
            } else {
                countdownToUserInputStream.println("canceledByTimer");
                if(userInputToCountdownStream.readLine().equals("ok")) System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                userInputToCountdownStream.close();
                countdownToUserInputStream.close();
                clientSocket.close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
