import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserInput extends Thread {
    private Socket socket;
    private PrintWriter userInputToCountdownStream;
    private BufferedReader countdownToUserInputStream;
    private String address;
    private int port;

    private final ConcurrentLinkedQueue<String> inputQueue = new ConcurrentLinkedQueue<>();
    public UserInput(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            userInputToCountdownStream = new PrintWriter(socket.getOutputStream(), true);
            countdownToUserInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread, der Input vom User liest
            Thread inputThread = new Thread(() -> {
                try {
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                    String userInput;
                    while (true) {
                        System.out.println("Befehl eingeben: (set X/cancel/remaining):");
                        userInput = stdIn.readLine();
                        inputQueue.offer(userInput); // Userinput in Queue packen, die vom Main-Thread gelesen wird
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            // Main-Thread: Sendet Userinput an den Countdown und empfängt Antworten/Nachrichten  vom Countdown
            try {
                while (true) {
                    //Senden und Warten auf Antwort, falls Userinput vorliegt
                    String userInput = (String) inputQueue.poll(); //Lesen der Queue, welche vom Userinput-Thread befüllt wird. Poll ist nicht blockierend
                    if(userInput != null) {
                        userInputToCountdownStream.println(userInput); // Senden
                        String response = countdownToUserInputStream.readLine(); // Antwort lesen, readLine() ist blockierend
                        if (response != null) {
                            handleMessageFromCountdown(response);
                        }
                    }

                    // Prüfen, ob Countdown Ende des Countdowns mitteilt
                    if(countdownToUserInputStream.ready()) {
                        String response = countdownToUserInputStream.readLine();
                        if (response != null) {
                            handleMessageFromCountdown(response);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (countdownToUserInputStream != null) countdownToUserInputStream.close();
                if (userInputToCountdownStream != null) userInputToCountdownStream.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleMessageFromCountdown(String response) {
        String endOfCountdownMessage = "Der Countdown wurde beendet!";
        if (response.equals("canceledByUser")) {
            System.out.println(endOfCountdownMessage);
            System.exit(0);
        } else if (response.equals("canceledByTimer")){
            userInputToCountdownStream.println("ok");
            System.out.println(endOfCountdownMessage);
        } else {
            System.out.println("Antwort des Countdowns: " + response);
        }
    }
}
